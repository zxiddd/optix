# 07 - Android Native Architecture & Implementation Specification

## Purpose
This document details the native Android system architecture, package organization, dependency injection graphs, reactive data streams, database access objects, state management patterns, and background workers for the **Optix** Android application.

---

## Overview
The Optix Android application is built entirely in Kotlin using modern Android development practices: Jetpack Compose for declarative UI, MVVM with Clean Architecture principles, Hilt for dependency injection, Room for encrypted local storage, Retrofit for networking, and WorkManager for resilient background sync orchestration.

---

## Software Architecture & Layer Division

```
+-----------------------------------------------------------------------------------+
|                                PRESENTATION LAYER                                 |
|  +-----------------------------------------------------------------------------+  |
|  | Jetpack Compose Screens / UI Components (Material 3 Theme & Design Tokens)   |  |
|  +--------------------------------------+--------------------------------------+  |
|                                         | Observes StateFlow / Sends User Actions |
|                               +---------v----------+                              |
|                               | ViewModels (Hilt)  |                              |
|                               +---------+----------+                              |
+-----------------------------------------|-----------------------------------------+
                                          | Executes Use Cases
                                          v
+-----------------------------------------------------------------------------------+
|                                  DOMAIN LAYER                                     |
|  +-----------------------------------------------------------------------------+  |
|  | Use Cases (CalculateCartTotalUseCase, ProcessPaymentUseCase, SyncOutboxUseCase)|  |
|  +--------------------------------------+--------------------------------------+  |
|                                         | Calls Repository Interfaces             |
+-----------------------------------------|-----------------------------------------+
                                          v
+-----------------------------------------------------------------------------------+
|                                   DATA LAYER                                      |
|  +-----------------------------------------------------------------------------+  |
|  | Repository Implementations (BillRepositoryImpl, ProductRepositoryImpl)       |  |
|  +-------------------+------------------------------------+--------------------+  |
|                      |                                    |                       |
|        +-------------v------------+          +------------v-------------+         |
|        | Room Local Data Source   |          | Retrofit Remote Network  |         |
|        | (SQLite Room DAOs)       |          | (REST API Client)        |         |
|        +--------------------------+          +--------------------------+         |
+-----------------------------------------------------------------------------------+
```

---

## Package Organization Structure

```
com.optix.pos/
├── OptixApplication.kt
├── di/                        # Hilt DI Modules
│   ├── DatabaseModule.kt
│   ├── NetworkModule.kt
│   ├── RepositoryModule.kt
│   └── PrinterModule.kt
├── data/                      # Data Layer
│   ├── local/
│   │   ├── OptixDatabase.kt
│   │   ├── dao/
│   │   │   ├── ProductDao.kt
│   │   │   ├── BillDao.kt
│   │   │   └── OutboxDao.kt
│   │   └── entity/
│   │       ├── ProductEntity.kt
│   │       └── BillEntity.kt
│   ├── remote/
│   │   ├── OptixApiService.kt
│   │   └── dto/
│   └── repository/
│       ├── BillRepositoryImpl.kt
│       └── ProductRepositoryImpl.kt
├── domain/                    # Domain Layer
│   ├── model/
│   │   ├── Cart.kt
│   │   └── Bill.kt
│   ├── repository/            # Repository Interfaces
│   └── usecase/
│       ├── CalculateCartUseCase.kt
│       └── FinalizeBillUseCase.kt
├── presentation/              # Presentation Layer
│   ├── checkout/
│   │   ├── CheckoutScreen.kt
│   │   └── CheckoutViewModel.kt
│   ├── catalog/
│   └── theme/
│       ├── Color.kt
│       └── Theme.kt
└── worker/                    # WorkManager Sync Jobs
    ├── SyncOutboxWorker.kt
    └── SyncCatalogWorker.kt
```

---

## Core Technical Components & Data Flow

### 1. Hilt Dependency Injection Graph
- `@HiltAndroidApp` annotates `OptixApplication`.
- `DatabaseModule`: Provides `@Singleton` instance of `OptixDatabase` encrypted with SQLCipher key.
- `NetworkModule`: Provides Retrofit instance with custom OkHttp interceptors attaching Bearer JWT headers and loggers.

### 2. Room DAO & Entity Design
- DAO interfaces declare explicit Flow-returning queries for real-time UI updates:
  `fun observeProductsByCategory(categoryId: String): Flow<List<ProductEntity>>`
- Write operations wrapped in transactional primitives (`@Transaction`).

### 3. State Management (StateFlow & SharedFlow)
- **UI State**: Exposed via immutable `StateFlow<CheckoutUiState>` inside ViewModels.
- **One-Off UI Events**: Navigation, Toast alerts, and ESC/POS print errors emitted via `SharedFlow<UiEvent>`.

### 4. Background Sync Orchestration (WorkManager)
- `SyncOutboxWorker` extends `CoroutineWorker`.
- Configured with constraints: `NetworkType.CONNECTED`.
- Uses exponential backoff retry policy: `BackoffPolicy.EXPONENTIAL`, initial delay 10 seconds.

---

## Reactive Checkout Data Flow Sequence

```
User Taps Product Tile 
       |
       v
CheckoutViewModel.onEvent(CheckoutEvent.AddItem(productId))
       |
       v
CalculateCartUseCase.execute(currentCart, addedItem)
       | (Computes totals, tax, discounts in <5ms)
       v
CheckoutUiState updated -> Jetpack Compose Recomposes Cart Column
       |
User Taps "Pay Cash" -> CheckoutViewModel.finalizeOrder()
       |
FinalizeBillUseCase.execute()
       |
       +---> Writes BillEntity to Room DB (@Transaction)
       +---> Writes OutboxEntity to Room DB
       +---> Triggers ESC/POS Bluetooth Print Job
       +---> Enqueues One-Time WorkManager Sync Request
```

---

## Critical Edge Cases

1. **Android OS Kills Process During Active Checkout**: System reclaims app memory while cashier is building a 20-item cart.  
   *Mitigation*: ViewModels utilize `SavedStateHandle` to preserve draft cart state across process death.
2. **WorkManager Constraint Execution Delay**: Device re-connects to Wi-Fi, but Android OS delays WorkManager execution due to battery optimization settings.  
   *Mitigation*: When network status changes to online, foreground repository directly triggers immediate outbox flush while retaining WorkManager as fallback backup.

---

## Technical Dependencies
- Kotlin 1.9+, Jetpack Compose (BOM 2024+), Hilt 2.50+, Room 2.6+, Retrofit 2.9+, WorkManager 2.9+, SQLCipher for Android 4.5+, Kotlin Coroutines & Flow.

---

## Best Practices
1. Never perform database reads/writes or bitmap receipt formatting on the Main UI Dispatcher; explicitly scope I/O bound operations to `Dispatchers.IO`.
2. Keep Jetpack Compose Composables strictly stateless by hoisting UI state up to ViewModel wrappers.
3. Validate preview capability for every Composable component using `@Preview` annotations.

---

## Open Technical Questions
1. **Multi-Window Tablet Support**: Should Jetpack Compose navigation implement `ListDetailPaneScaffold` to natively support dual-screen customer-facing displays on Android POS consoles?
