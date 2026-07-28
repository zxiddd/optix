# 02 - Comprehensive System Requirements

## Purpose
This document establishes the official Functional Requirements (FRs) and Non-Functional Requirements (NFRs) for the **Optix** platform. It defines strict system benchmarks, performance SLAs, hardware specifications, and security baselines required to build, test, and deploy the enterprise POS platform.

---

## Overview
Optix is engineered for high-intensity commercial environments. A cashier at a busy bakery or retail counter cannot tolerate spinners, screen freezes, or failed printer connections. The system requirements prioritize ultra-low latency, deterministic offline execution, memory efficiency on low-cost Android hardware, and bulletproof data integrity across central PostgreSQL servers.

---

## Functional Requirements (FR)

### Module 1: Catalog & Pricing Management
- **FR-1.01**: System must support hierarchical catalog organization (Categories -> Subcategories -> Items -> Variants -> Modifiers).
- **FR-1.02**: System must support multiple pricing models: fixed price, weight-based price (per kg/g/lb), market-rate dynamic price, and variable open-amount price.
- **FR-1.03**: System must support tax rules: inclusive tax, exclusive tax, zero-tax, and multi-tier regional taxes (e.g., GST/VAT split).
- **FR-1.04**: System must support barcode scanning lookup using internal SKU, EAN-13, UPC-A, and custom weight-embedded barcodes (GS1-128 / Price-Verifier barcodes).

### Module 2: Cart & Billing Engine
- **FR-2.01**: System must assemble cart items, compute subtotal, apply line-item and cart-level discounts, compute tax splits, and render gross total in <10ms.
- **FR-2.02**: System must support multiple concurrent order hold/park slots per terminal.
- **FR-2.03**: System must support split payment tender: Cash + Card, Cash + UPI/QR, Split among multiple customers.
- **FR-2.04**: System must record staff member ID responsible for order creation and payment collection.

### Module 3: Offline Storage & Synchronization
- **FR-3.01**: System must store all local catalog data, transaction history, customer registers, and outbox queues in encrypted Room SQLite database.
- **FR-3.02**: System must operate completely disconnected from internet for at least 30 consecutive days without performance degradation.
- **FR-3.03**: System must background-sync queued local outbox events to central Node.js backend when network connection is established using WorkManager.

### Module 4: Hardware & Printing Subsystem
- **FR-4.01**: System must connect to ESC/POS thermal printers via Bluetooth (SPP/BLE), Wi-Fi/LAN (TCP port 9100), and USB OTG.
- **FR-4.02**: System must support receipt formatting for 58mm (2-inch) and 80mm (3-inch) roll paper widths.
- **FR-4.03**: System must trigger cash drawer kick pulse (RJ11 pin 2/5) upon successful cash payment finalization.

### Module 5: Vertical Domain Features
- **FR-5.01 (Restaurant)**: Visual table grid management, kitchen display system (KDS) routing, kitchen order ticket (KOT) printing.
- **FR-5.02 (Medical)**: Drug batch tracking, expiry date warning block, generic salt lookup table.
- **FR-5.03 (Chicken/Butcher)**: Processing yield loss calculation (live weight to dressed weight ratio).

---

## Non-Functional Requirements (NFR)

| Metric ID | Performance Metric | Benchmark Target | Maximum Threshold |
| :--- | :--- | :--- | :--- |
| **NFR-P01** | Local Cart Addition Latency | < 5 ms | 15 ms |
| **NFR-P02** | Local Transaction Finalization & DB Commit | < 30 ms | 50 ms |
| **NFR-P03** | Cold App Launch Time | < 1.0 sec | 1.5 sec |
| **NFR-P04** | ESC/POS Receipt Print Job Dispatch | < 100 ms | 250 ms |
| **NFR-P05** | Server API Response Time (p99) | < 150 ms | 300 ms |
| **NFR-P06** | Peak Server Throughput | 5,000 req/sec | N/A |

### Security & Compliance Requirements
- **NFR-S01 (Data Encryption)**: Local Room SQLite database must be encrypted at rest using SQLCipher (AES-256).
- **NFR-S02 (Authentication)**: Server API communication requires valid Firebase JWT bearer tokens passed in HTTP Authorization headers.
- **NFR-S03 (PIN Authentication)**: Staff local login requires 4-digit or 6-digit hashed PIN validation with bcrypt cost factor 10.
- **NFR-S04 (Transport Security)**: All HTTP network communication enforced via TLS 1.3 with certificate pinning on Android app.

### Hardware Specifications Baseline
- **Android Terminal**: Minimum Android 10 (API Level 29), Recommended Android 13+. Minimum 3 GB RAM (4 GB+ recommended), 32 GB flash storage, ARM64 quad-core CPU.
- **Display Dimensions**: Supports 7-inch tablets, 10-inch POS consoles, and 6-inch handheld POS devices (adaptive UI layouts).

---

## System Context Architecture Diagram

```
+-------------------------------------------------------------------------------+
|                            RETAIL COUNTER TOPOLOGY                            |
|                                                                               |
|  +--------------------+        +---------------------+      +--------------+  |
|  | Digital Weight Scale|------->| Android POS Terminal|----->| USB Barcode  |  |
|  | (RS232 / USB Serial)|       | (Optix Native App)  |      | Scanner      |  |
|  +--------------------+        +----------+----------+      +--------------+  |
|                                           |                                   |
|                        +------------------+------------------+                |
|                        |                                     |                |
|                        v                                     v                |
|             +--------------------+                 +--------------------+     |
|             | Thermal Receipt    |                 | Cash Drawer        |     |
|             | Printer (ESC/POS)  |                 | (RJ11 Kick Pulse)  |     |
|             +--------------------+                 +--------------------+     |
+-------------------------------------------|-----------------------------------+
                                            | Asynchronous Sync (Wi-Fi / 4G)
                                            v
+-------------------------------------------------------------------------------+
|                             CLOUD VPS INFRASTRUCTURE                          |
|  +-------------------------------------------------------------------------+  |
|  | Ubuntu VPS -> Nginx TLS 1.3 -> PM2 Node.js Backend -> Postgres DB 16    |  |
|  +-------------------------------------------------------------------------+  |
+-------------------------------------------------------------------------------+
```

---

## Operational Edge Cases

1. **Hardware Disconnection During Print Job**: Thermal printer runs out of paper mid-receipt or Bluetooth drops.  
   *System Action*: POS pauses checkout finish screen, displays retry/re-route receipt dialog, retains finalized transaction in DB without duplicating billing index.
2. **Extreme Storage Constrained Device**: Android device disk space drops below 100 MB.  
   *System Action*: POS alerts manager, triggers local log cleanup, suspends non-essential image caching, preserves database outbox integrity.

---

## Future Expansion
- Integration with external card EMV terminals (Verifone, Pax, Ingenico) via serial/Ethernet protocols.
- Automated system health telemetry reporting device battery status, storage state, and Bluetooth connection quality.

---

## Dependencies
- **Android Libraries**: SQLCipher for Android, AndroidX Room, WorkManager, Hilt, Retrofit2, OkHttp3.
- **Server Infrastructure**: Node.js 20 LTS, TypeScript 5+, PostgreSQL 16, Prisma ORM, Nginx 1.24+, PM2.

---

## Best Practices
1. Conduct performance profiling on low-end ARM64 budget tablets to guarantee SLAs.
2. Enforce strict timeouts on hardware I/O interfaces (Bluetooth socket timeout 3.0 seconds max).
3. Benchmark local database writes under synthetic loads of 50,000 un-synced outbox rows.

---

## Open Technical Questions
1. **Low-Memory Device Tiering**: Should we build a simplified low-memory rendering mode in Jetpack Compose for Android devices with only 2 GB RAM?
2. **Custom Barcode Format Extensibility**: How will custom barcode regex rules be defined on cloud backend and pushed dynamically to local Android scanners?
