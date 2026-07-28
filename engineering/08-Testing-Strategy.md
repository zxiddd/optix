# 08 - Automated Testing Framework & QA Execution Strategy [v1.0 FROZEN ARCHITECTURE]

## Purpose
This document defines the automated testing framework, test suite organization, hardware I/O test mocks, offline sync simulation protocols, load/stress benchmarks, and Continuous Integration (CI) test execution rules across the **Optix** ecosystem.

---

## Goals
1. Establish a 100% automated testing pipeline covering Unit, Integration, UI, Sync, Hardware Printer, Security, and Load test suites.
2. Guarantee sub-5ms local cart computation accuracy via comprehensive Unit tests.
3. Validate offline sync resiliency using simulated network drop and outbox collision test suites.
4. Benchmark server API performance under 5,000 requests/second load thresholds using K6.

---

## Testing Matrix & Framework Selection

```
                       OPTIX TESTING ARCHITECTURE
+-----------------------+-----------------------+-------------------------------+
| Test Category         | Testing Framework     | Execution Target              |
+-----------------------+-----------------------+-------------------------------+
| Android Unit Tests    | JUnit5, Mockk, Truth  | ViewModels, Use Cases, Math   |
| Compose UI Tests      | Compose Test Rule     | Compose Screen Rendering & UI |
| Room DB Integration   | AndroidJUnit4, Room   | Encrypted SQLite DAO Integrity|
| Backend Unit/Integration| Jest, Supertest     | Domain Services, Fastify APIs |
| Offline Sync Tests    | MockWebServer, Room   | Outbox Queue & Conflict Merges|
| Printer Hardware Tests| ESC/POS Byte Verifier | Buffer Hex Verification       |
| API Load & Stress     | K6 Load Testing Engine| 5,000 req/sec Server Threshold|
| End-to-End (E2E)      | Playwright / Espresso | Complete Cashier User Journey |
+-----------------------+-----------------------+-------------------------------+
```

---

## Detailed Test Suite Specifications

### 1. Android Cart Computation Unit Tests (`CalculateCartUseCaseTest`)
- Tests cart subtotal, tax splits, line item discounts, and Banker's Rounding (`HALF_EVEN`) precision.
- Validates fractional quantity calculations for weighable products (e.g., `1.845 kg * $12.00/kg`).

### 2. Encrypted Room SQLite Integration Tests (`BillDaoTest`)
- Validates `@Transaction` cart-to-bill persistence inside local Room database.
- Verifies Outbox event generation upon bill creation.

### 3. Backend API Integration Tests (`sync.test.ts`)
- Validates idempotent batch event processing on `POST /api/v1/sync/push`.
- Asserts HTTP 409 Conflict returns on duplicate `Idempotency-Key` headers.

### 4. Offline Synchronization & Network Interruption Tests (`SyncEngineTest`)
- **Simulated Test Scenario**:
  1. POS creates 500 bills offline (`status = 'PENDING'`).
  2. Network connection drops mid-batch push.
  3. WorkManager triggers exponential backoff retry.
  4. Upon network restoration, server receives push, performs deduplication, and clears local outbox queue.

### 5. ESC/POS Printer Hardware Byte Code Tests (`EscPosPrinterTest`)
- Verifies exact hexadecimal command byte generation (`ESC @`, `GS V A 0`, `ESC p 0 25 250`).
- Validates 1-bit monochrome raster converter output against test bitmap templates.

### 6. API Load & Stress Benchmarks (`k6-load-test.js`)
- Simulates 5,000 virtual POS terminals pushing sync payloads simultaneously.
- SLA Target: p99 response time <150ms; 0.00% error rate under load.

---

## Step-by-Step Testing Implementation Order

1. **Step 1**: Setup JUnit5 and Mockk dependencies in Android `core-database` and `feature-billing` modules.
2. **Step 2**: Setup Jest and Supertest in `apps/backend/`.
3. **Step 3**: Write core domain unit tests for cart calculation and pricing strategies.
4. **Step 4**: Write Room DAO integration tests using in-memory SQLite instances.
5. **Step 5**: Write backend Fastify API integration tests for `/auth/device-register` and `/bills/checkout`.
6. **Step 6**: Write offline sync simulation tests with MockWebServer.
7. **Step 7**: Configure K6 load testing scripts and integrate tests into GitHub Actions CI workflow (`.github/workflows/ci.yml`).

---

## Risks & Mitigation Matrix

| Risk Factor | Impact | Mitigation Strategy |
| :--- | :--- | :--- |
| **Flaky UI Tests in CI Pipeline** | Medium | Use explicit Compose `waitUntil` assertions rather than static Thread.sleep timeouts. |
| **Physical Printer Requirement for Unit Tests** | High | Abstract printer hardware behind `PrinterDriver` interface; test bytecode buffers in memory. |

---

## Frozen Architecture Sign-Off
- **Status**: FROZEN (v1.0)
- **Tag**: `v1.0-testing-strategy-freeze`
