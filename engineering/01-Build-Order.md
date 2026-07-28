# 01 - Master Project Build Order & Milestone Execution Roadmap

## Purpose
This document defines the master engineering build order, milestone sequence, dependency hierarchy, feature packaging, acceptance criteria, and Definition of Done (DoD) for building the **Optix** enterprise POS platform. It establishes an incremental, risk-mitigated construction sequence where every milestone builds exclusively upon previously verified components.

---

## Goals
1. Establish a strict, deterministic sequence of development phases to eliminate circular dependencies.
2. Ensure the core offline-first Room database engine and sync outbox are fully functional before building UI layers.
3. Validate hardware I/O interfaces (ESC/POS thermal printers, digital weight scales) early in the build cycle.
4. Guarantee that every completed milestone yields a deployable, testable software increment.

---

## Technical Dependencies & Build Hierarchy

```
+-----------------------------------------------------------------------------------+
|                           OPTIX BUILD DEPENDENCY GRAPH                            |
|                                                                                   |
|  [ M1: PostgreSQL & Room Core DB ] ---> [ M2: Auth & Device Registration ]        |
|                                                     |                             |
|                                                     v                             |
|  [ M4: Compose POS & ESC/POS Print ] <--- [ M3: Outbox & WorkManager Sync ]       |
|                 |                                                                 |
|                 v                                                                 |
|  [ M5: Shift, RBAC & CRM ] -------------> [ M6: Vertical Business Plugins ]      |
|                 |                                          |                      |
|                 v                                          v                      |
|  [ M7: SaaS Subscriptions & Admin ] <---- [ M8: Advanced PO & Expenses ]          |
|                                                            |                      |
|                                                            v                      |
|                                           [ M9: AI Suite & Voice Billing ]        |
+-----------------------------------------------------------------------------------+
```

---

## Step-by-Step Milestone Implementation Order

### Milestone 1: Core Database Schemas & Data Model Engine
- **Required Dependencies**: None (Foundational Milestone).
- **Estimated Complexity**: High.
- **Features Included**:
  - Central PostgreSQL 16 schema creation (Tenants, Outlets, Users, Categories, Products, Bills, Payments).
  - Encrypted SQLite Room database setup with SQLCipher on Android client.
  - Core domain entity mappings and DTO data converters.
- **Acceptance Criteria**:
  - PostgreSQL schema installs cleanly via Prisma ORM migration.
  - Room database initializes on Android ARM64 device with SQLCipher encryption in <100ms.
- **Definition of Done**: 100% of core database tables created, indexed, and verified via automated schema migration unit tests.

---

### Milestone 2: Authentication, Multi-Tenancy & Device Hydration
- **Required Dependencies**: Milestone 1.
- **Estimated Complexity**: Medium.
- **Features Included**:
  - Firebase Authentication integration (Phone/Email/PIN JWT validation).
  - Multi-tenant tenant claims verification (`business_id` scoping).
  - Device activation flow and initial catalog seed pull API (`/api/v1/sync/pull`).
- **Acceptance Criteria**:
  - Android device completes onboarding registration and pulls initial catalog into local Room DB in <2 seconds.
  - Unauthenticated server requests strictly return HTTP 401 Unauthorized.
- **Definition of Done**: Device registration use cases passed, multi-tenant isolation verified via integration tests.

---

### Milestone 3: Local Outbox Queue & Background Sync Engine
- **Required Dependencies**: Milestones 1, 2.
- **Estimated Complexity**: Extreme.
- **Features Included**:
  - Room `outbox_events` table and atomic transaction interceptors.
  - WorkManager `CoroutineWorker` push pipeline (`POST /api/v1/sync/push`).
  - Idempotent event processor on Node.js backend.
  - Conflict resolution handlers (Append-only bills vs LWW catalog updates).
- **Acceptance Criteria**:
  - 1,000 local transactions created offline flush successfully to central PostgreSQL DB upon network restoration with zero data loss or duplicate entries.
- **Definition of Done**: Passed 72-hour simulated network drop and reconnect stress tests without payload corruption.

---

### Milestone 4: Jetpack Compose Billing Register & ESC/POS Printing
- **Required Dependencies**: Milestones 1, 2, 3.
- **Estimated Complexity**: High.
- **Features Included**:
  - Apple-inspired glassmorphic Compose UI layout for 7" and 10" POS displays.
  - Cart calculation engine (Subtotal, Tax, Discounts, Gross Total in <5ms).
  - ESC/POS hardware print engine (Bluetooth SPP/BLE, LAN TCP, USB OTG).
  - RJ11 cash drawer solenoid kick integration.
- **Acceptance Criteria**:
  - Cashier scans item, calculates total, finalizes sale, prints receipt, and kicks cash drawer in under 3 seconds total interaction time.
- **Definition of Done**: Printed receipt templates verified on 58mm and 80mm hardware thermal printers.

---

### Milestone 5: Shift Management, RBAC & Customer CRM
- **Required Dependencies**: Milestone 4.
- **Estimated Complexity**: Medium.
- **Features Included**:
  - Shift opening/closing workflows with cash drawer float variance calculation.
  - Staff role permission matrix (7 roles, 50+ permission toggles).
  - Manager PIN authorization overlay with randomized keypad.
  - Customer directory, store credit ("Khata") ledger, loyalty point accrual.
- **Acceptance Criteria**:
  - Unauthorized cashier action triggers Manager PIN modal; incorrect PIN 3 times locks screen for 60 seconds.
- **Definition of Done**: Passed RBAC security audit and customer credit limit assertion tests.

---

### Milestone 6: Specialized Vertical Business Modules
- **Required Dependencies**: Milestone 5.
- **Estimated Complexity**: High.
- **Features Included**:
  - **Restaurant**: Floorplan grid, KDS screen, table split/merge, waiter captain app mode.
  - **Chicken Shop**: USB digital weight scale driver, processing yield loss calculation, daily market pricing.
  - **Medical**: Generic salt lookup, batch expiry date lock, Schedule H register, batch recall.
  - **Bakery, Retail, Salon**: Recipe BOM, high-speed continuous scan, stylist appointment grid.
- **Acceptance Criteria**:
  - Restaurant waiter sends KOT from tablet; ticket appears on kitchen KDS screen in <500ms over local Wi-Fi.
- **Definition of Done**: All 6 vertical domain plugins verified in target test environments.

---

### Milestone 7: SaaS Subscriptions, Admin Portal & Executive Analytics
- **Required Dependencies**: Milestones 5, 6.
- **Estimated Complexity**: Medium-High.
- **Features Included**:
  - Tiered subscription engine (Free Trial 10 bills/day limit, Monthly, Yearly).
  - 7-day offline subscription grace period handler.
  - Super Admin Web Portal (Tenant manager, MRR/ARR analytics, remote feature flags, broadcast engine).
  - Executive BI analytics (Sales trajectory, profit margins, peak hour heatmaps).
- **Acceptance Criteria**:
  - Expired subscription displays graceful warning banner offline without wiping un-synced sales data.
- **Definition of Done**: Passed Super Admin portal end-to-end management testing.

---

### Milestone 8: Advanced Inventory, Purchase Orders & Expense Engine
- **Required Dependencies**: Milestone 7.
- **Estimated Complexity**: Medium-High.
- **Features Included**:
  - Supplier directory and item catalog mapping.
  - Purchase Orders (PO) and Stock Inwarding (GRN) with Weighted Average Costing (WAC).
  - Inter-branch stock transfers and wastage/damage logging.
  - Store expense tracking (Rent, Salaries, Utilities, Vendor Payouts) with photo attachments.
- **Acceptance Criteria**:
  - Stock Inwarding increases product stock counts and recalculates item cost prices accurately.
- **Definition of Done**: Inventory audit variance report verified against physical stock counts.

---

### Milestone 9: AI Automation Suite & Voice Billing
- **Required Dependencies**: Milestone 8.
- **Estimated Complexity**: High.
- **Features Included**:
  - Camera Menu & Invoice OCR scanner (ML Kit / TFLite).
  - AI Inventory forecasting engine (Time-series ML models).
  - AI Fraud & Void anomaly detection algorithms.
  - Natural language AI Executive WhatsApp synthesizer.
  - Hands-free AI Voice billing command handler.
- **Acceptance Criteria**:
  - OCR scanner parses 20-item physical paper menu into structured product entities in <5 seconds.
- **Definition of Done**: AI forecasting accuracy verified against 90-day historic sales data.

---

### Milestone 10: Multi-Outlet Enterprise Fleet & Global Scaling
- **Required Dependencies**: Milestone 9.
- **Estimated Complexity**: High.
- **Features Included**:
  - Multi-outlet chain central management dashboard.
  - Global franchise royalty calculation engines.
  - Multi-currency and regional tax localization profiles.
- **Acceptance Criteria**:
  - Central dashboard renders consolidated real-time analytics across 50 simulated store branches.
- **Definition of Done**: Load-tested central PostgreSQL DB under 5,000 req/sec peak push traffic.

---

## Risks & Mitigation Matrix

| Risk Factor | Impact | Mitigation Strategy |
| :--- | :--- | :--- |
| **Sync Deadlocks During High-Volume Offline Push** | High | Enforce strict append-only ledger rules for bills; process catalog updates via idempotent LWW timestamp checks. |
| **Bluetooth Thermal Printer Connection Drops** | Medium | Maintain socket retry pools with 2.5s timeouts and non-blocking failover UI dialogs. |
| **Android Memory Leaks on Budget 2GB Tablets** | High | Use Jetpack Compose stateless composables; profile heap allocations using Android Studio Profiler. |

---

## Best Practices
1. Never advance to a subsequent milestone until the current milestone passes 100% of its Definition of Done checks.
2. Maintain automated regression test pipelines on every PR merge.

---

## Common Mistakes to Avoid
- Building UI screens before validating underlying Room database transactional DAOs.
- Postponing hardware printer integration to the end of the project.

---

## Future Expansion
- Integration with smart IoT weight scale Bluetooth sensors and autonomous smart payment terminals.
