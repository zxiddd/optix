# 04 - Native Android Application Architecture & Design Blueprint [v1.0 FROZEN ARCHITECTURE]

## Purpose
This document defines the native Android client software architecture, navigation graphs, UI state models, offline-first behavior matrices, Hilt dependency injection scoping, hardware printer abstractions, security controls, and performance budgets for the **Optix Android POS Application**. This document is **FROZEN (v1.0)** as the authoritative Android client handbook.

---

## Goals
1. Build an ultra-responsive native Android POS application achieving sub-50ms cart compute speeds and <2-second cold start times on Android 10+ (API Level 29+).
2. Enforce **Local-First Data Persistence**: Room SQLite encrypted with SQLCipher is the primary application source of truth.
3. Standardize UI Screen States (`Loading`, `Success`, `Empty`, `Offline`, `Error`, `Unauthorized`) across all Jetpack Compose screens.
4. Implement a hardware printer abstraction layer supporting Bluetooth (SPP/BLE), LAN TCP, USB OTG, and Mock test drivers.
5. Provide strict accessibility compliance (WCAG 2.1 AA, screen readers, RTL support) and process death state restoration.

---

## Technical Dependencies & Technology Stack

```
                     ANDROID TECHNOLOGY STACK
+-----------------------+-----------------------------------------------+
| Layer                 | Technology Selected                           |
+-----------------------+-----------------------------------------------+
| Language & Runtime    | Kotlin 1.9+, JVM Target 17                    |
| UI Framework          | Jetpack Compose (Material 3 BOM 2024+)        |
| Architecture          | MVVM + Clean Architecture                     |
| Dependency Injection  | Hilt 2.50+ / Dagger Annotations               |
| Local Database        | Room 2.6+, SQLCipher for Android 4.5 (AES-256)|
| Networking            | Retrofit 2.9+, OkHttp 4.12+ (TLS 1.3 Strict)  |
| Reactive Streams      | Kotlin Coroutines 1.8+, StateFlow, SharedFlow |
| Background Sync       | AndroidX WorkManager 2.9+ (Hilt Worker)       |
| Hardware Connectivity | Android Bluetooth RFCOMM, USB Host API, Serial|
| Image Loading         | Coil 2.6+ (Memory & Disk Cache)               |
+-----------------------+-----------------------------------------------+
```

---

## Offline-First Behavior Matrix

Optix POS terminals operate 100% disconnected from the internet for core cashier functions:

| Feature Domain | Offline Capabilities | Online Sync Actions |
| :--- | :--- | :--- |
| **Billing & Cart** | ✔ Add items, compute totals, apply discounts<br>✔ Finalize bill & print receipt<br>✔ Open cash drawer<br>✔ Save bill in Room DB outbox | ✔ Push queued outbox events<br>✔ Receive global invoice numbers |
| **Catalog & Prices** | ✔ Search items by name/SKU/barcode<br>✔ View categories & variants<br>✔ Calculate taxes | ✔ Pull delta catalog updates (`GET /sync/pull`) |
| **Customers & Khata** | ✔ Search local customer directory<br>✔ Record credit sales up to credit limit<br>✔ Accrue local loyalty points | ✔ Sync customer credit balance<br>✔ Dispatch WhatsApp receipts |
| **Inventory & Stock** | ✔ Deduct stock counts in Room DB<br>✔ Display out-of-stock warnings | ✔ Aggregate multi-terminal deltas<br>✔ Refresh re-order alerts |
| **Shift Closing** | ✔ Perform cash drawer float count<br>✔ Generate local X/Z-Report | ✔ Upload shift closing log to cloud |

---

## Navigation Graph Architecture

Navigation executes via a single-activity architecture (`MainActivity`) hosting a root Jetpack Compose `NavHost`:

```
+-----------------------------------------------------------------------------------+
|                            ROOT NAVIGATION GRAPH (NavHost)                        |
|                                                                                   |
|  [ Splash Screen ] -> [ Auth Graph: Login / PIN Activation ]                      |
|                              |                                                    |
|                              v                                                    |
|  [ Business Selection Graph: Select Outlet & Register ]                           |
|                              |                                                    |
|                              v                                                    |
|  +-----------------------------------------------------------------------------+  |
|  | MAIN DASHBOARD GRAPH (Bottom Navigation / Split Pane Drawer)                |  |
|  |  ├── BillingRegisterRoute   (Primary Checkout & Payment Sheet)             |  |
|  |  ├── ProductCatalogRoute   (Item Management & Category Grid)               |  |
|  |  ├── CustomerDirectoryRoute(Khata Credit & Loyalty Profiles)               |  |
|  |  ├── ShiftClosingRoute     (Drawer Cash Float & Z-Report)                  |  |
|  |  ├── InventoryAuditRoute   (Batch Expiry & Stock Inwarding)                |  |
|  |  └── StoreSettingsRoute    (Hardware Printers & Tax Config)                |  |
|  +-----------------------------------------------------------------------------+  |
+-----------------------------------------------------------------------------------+
```

---

## Standard UI Screen State Model

Every Jetpack Compose screen observes a standardized sealed UI state wrapper from its ViewModel:

```kotlin
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Empty(val message: String) : UiState<Nothing>
    data class Offline(val cachedData: T? = null, val unsyncedCount: Int) : UiState<T>
    data class Error(val errorType: ErrorType, val message: String) : UiState<Nothing>
    data object Unauthorized : UiState<Nothing>
}
```

---

## Hardware Printer Abstraction Layer (`core-hardware`)

```kotlin
// Hardware Printer Driver Interface
interface PrinterDriver {
    suspend fun connect(): Boolean
    suspend fun disconnect()
    suspend fun printReceipt(rasterBytes: ByteArray): PrintResult
    suspend fun kickCashDrawer(): Boolean
}

// Implementations: BluetoothPrinterDriver, UsbPrinterDriver, TcpLanPrinterDriver, MockPrinterDriver
```

---

## Measurable Performance Budget Targets

| Performance Benchmark | Target SLA | Maximum Threshold |
| :--- | :--- | :--- |
| **App Cold Start Time** | < 1.0 sec | 2.0 sec |
| **Cart Addition & Total Compute** | < 5 ms | 50 ms |
| **Barcode Scan & Product Search** | < 30 ms | 100 ms |
| **Local Bill DB Finalization** | < 20 ms | 300 ms |
| **ESC/POS Receipt Print Job Dispatch** | < 100 ms | 1,000 ms (1.0s) |

---

## Dependency Injection Rules (Hilt Scoping)

- `@Singleton`: Single instance throughout app lifecycle (`OptixDatabase`, `OkHttpClient`, `PrinterManager`, `FeatureFlagManager`).
- `@ViewModelScoped`: Bound to ViewModel lifecycle (`CartViewModel`, `CatalogViewModel`).
- `@CustomScoped` / Worker: Bound to WorkManager lifecycle (`SyncOutboxWorker`).
- **Rule**: Direct Service Locators (`Koin` / manual global singletons) are strictly forbidden; inject dependencies strictly via `@Inject constructor(...)`.

---

## Reusable UI Component Library (`core-designsystem`)

- `AppButton`: Tactile primary button with click debouncing and loading spinner.
- `AppCard`: Glassmorphic card container with 16dp rounded corners and subtle border stroke.
- `AppTextField`: Text input with explicit validation error messages and clear icons.
- `AppDialog` & `AppBottomSheet`: Standardized modal sheet wrappers for alerts and payment entry.
- `AppEmptyState` & `AppLoading`: Reusable empty asset placeholders and shimmer loaders.

---

## Security Hardening & State Restoration

1. **Database & Preferences Encryption**:
   - Room SQLite encrypted with SQLCipher (AES-256).
   - Sensitive user tokens saved using `EncryptedSharedPreferences`.
   - Master key stored securely in Android KeyStore.
2. **Process Death & State Restoration**:
   - ViewModels utilize `SavedStateHandle` to preserve draft cart state across process death.
   - Compose input fields wrap transient UI state inside `rememberSaveable`.

---

## Accessibility, Localization & Ergonomics

- **Touch Targets**: Minimum 48dp x 48dp on all clickable components.
- **Color Contrast**: Complies with WCAG 2.1 AA (minimum contrast ratio 4.5:1).
- **RTL & Localization**: Full Support for Right-to-Left (RTL) screen mirroring and multi-language string resources (`res/values-ar/`, `res/values-es/`).
- **Screen Reader Semantics**: `Modifier.semantics { contentDescription = "..." }` attached to all product tiles and action icons.

---

## Step-by-Step Android Implementation Order

1. **Step 1**: Initialize `apps/android/` Gradle project, configure Hilt, Room, Compose, and Retrofit dependencies.
2. **Step 2**: Implement `core-database` (Room database, SQLCipher KeyStore helper, outbox entity).
3. **Step 3**: Implement `core-designsystem` (Glassmorphic M3 theme, tokens, reusable components).
4. **Step 4**: Implement `feature-auth` (Login, PIN overlay, navigation graph setup).
5. **Step 5**: Implement `feature-billing` (Cart ViewModel, CalculateCartUseCase, CheckoutScreen).
6. **Step 6**: Implement `core-hardware` (ESC/POS bytecode builder, Bluetooth/USB/LAN/Mock printer drivers).
7. **Step 7**: Implement `core-sync` (`SyncOutboxWorker` WorkManager implementation).
8. **Step 8**: Implement vertical extension plugins (`module-restaurant`, `module-medical`, etc.).

---

## Frozen Architecture Sign-Off
- **Status**: FROZEN (v1.0)
- **Tag**: `v1.0-android-architecture-freeze`
