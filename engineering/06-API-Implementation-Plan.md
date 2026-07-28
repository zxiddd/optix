# 06 - REST API Construction & Endpoint Specification Plan

## Purpose
This document defines the REST API design, endpoint contracts, validation pipelines, middleware execution flows, rate limits, error schemas, and implementation roadmap for the **Optix Fastify/TypeScript Backend API Gateway**.

---

## Goals
1. Provide complete API endpoint specifications for all core POS actions, catalog management, customer CRM, shift closing, and offline synchronization.
2. Enforce strict **Fastify JSON Schema / Zod Input Validation** on every request body, query parameter, and URL parameter.
3. Enforce **Multi-Tenant Context Scoping**: Every API handler operates strictly within the resolved `business_id` and `outlet_id` extracted from validated JWT headers.
4. Guarantee idempotency on mutating operations using the `Idempotency-Key` header mechanism.
5. Adhere to RFC 7807 Problem Details for standard, machine-readable error responses.

---

## Technical Dependencies & Architecture

- **Framework**: Fastify 4.26+ running on Node.js 20 LTS & TypeScript 5.3+.
- **Routing Base**: `/api/v1/`
- **Validation**: Zod 3.22+ compiled into Fastify JSON Schema schemas.
- **Authentication**: Firebase Admin SDK (JWT Validation) & Device Tokens.
- **Error Standard**: RFC 7807 Problem Details (`application/problem+json`).

---

## Global Middleware Pipeline Sequence

```
+-----------------------------------------------------------------------------------+
|                        FASTIFY HTTP REQUEST MIDDLEWARE PIPELINE                   |
|                                                                                   |
|  [ Incoming Request ]                                                             |
|           |                                                                       |
|           v                                                                       |
|  [ 1. RequestLoggerMiddleware ]    --> Assigns X-Correlation-ID & logs HTTP method  |
|           |                                                                       |
|           v                                                                       |
|  [ 2. FastifyHelmetMiddleware ]    --> Injects HSTS, CSP, X-Frame-Options headers   |
|           |                                                                       |
|           v                                                                       |
|  [ 3. RateLimiterMiddleware ]      --> Enforces route-specific sliding window caps  |
|           |                                                                       |
|           v                                                                       |
|  [ 4. FirebaseAuthMiddleware ]     --> Validates Bearer JWT & extracts user UID   |
|           |                                                                       |
|           v                                                                       |
|  [ 5. TenantContextMiddleware ]    --> Resolves business_id, outlet_id & user role|
|           |                                                                       |
|           v                                                                       |
|  [ 6. IdempotencyMiddleware ]      --> Checks Redis for Idempotency-Key locks      |
|           |                                                                       |
|           v                                                                       |
|  [ 7. Fastify Zod Validator ]      --> Validates DTO body schema (returns 422)    |
|           |                                                                       |
|           v                                                                       |
|  [ 8. Module Service Controller ]  --> Executes Domain Business Logic             |
+-----------------------------------------------------------------------------------+
```

---

## Complete API Endpoint Specifications

### 1. Auth & Device Gateway Module (`/api/v1/auth`)

#### Endpoint: `POST /api/v1/auth/device-register`
- **Purpose**: Authenticates Firebase token, registers new Android POS terminal ID, returns signed tenant token and initial hydration payload.
- **Headers**: `Authorization: Bearer <Firebase_JWT>`
- **Request Body**:
  ```json
  {
    "device_hardware_uuid": "ARM64-TABLET-90B42",
    "device_name": "Counter POS 1",
    "app_version": "1.4.2"
  }
  ```
- **Response (200 OK)**:
  ```json
  {
    "status": "SUCCESS",
    "data": {
      "business_id": "b18a42f5-31a8-4e12-a720-0021c4ef99a1",
      "outlet_id": "o-9022-main",
      "business_name": "Metro Bakery & Cafe",
      "currency_symbol": "$",
      "device_token": "eyJhbGciOiJIUzI1NiIsIn..."
    }
  }
  ```

---

### 2. Products & Catalog Module (`/api/v1/products`)

#### Endpoint: `GET /api/v1/products`
- **Purpose**: Returns active store product catalog. Supports pagination and search.
- **Query Params**: `?category_id=<uuid>&search=<string>&limit=50&cursor=<string>`
- **Response (200 OK)**:
  ```json
  {
    "status": "SUCCESS",
    "data": [
      {
        "id": "p-901-croissant",
        "title": "Butter Croissant",
        "sku": "BAK-001",
        "barcode": "2012345678901",
        "pricing_strategy": "FIXED",
        "unit_price": 4.5000,
        "current_stock": 42.000,
        "version_timestamp": 1722211350000
      }
    ],
    "meta": { "next_cursor": "eyJpZCI6InAtOTAxIn0=" }
  }
  ```

#### Endpoint: `POST /api/v1/products`
- **Purpose**: Creates new product entity in catalog.
- **Permissions**: `Manager` or `Owner`.
- **Request Body**:
  ```json
  {
    "title": "Sourdough Loaf",
    "category_id": "c-10-bread",
    "pricing_strategy": "FIXED",
    "unit_price": 6.50,
    "cost_price": 2.10,
    "track_inventory": true,
    "current_stock": 20.0
  }
  ```

---

### 3. Billing & Checkout Module (`/api/v1/bills`)

#### Endpoint: `POST /api/v1/bills/checkout`
- **Purpose**: Finalizes sales transaction, creates immutable bill, stores payment tenders, and updates stock ledger.
- **Headers**: `Idempotency-Key: <UUIDv4>`
- **Request Body**:
  ```json
  {
    "invoice_number": "DEV01-INV-1042",
    "customer_id": "cust-8801",
    "subtotal": 45.0000,
    "tax_total": 4.5000,
    "discount_total": 0.0000,
    "gross_total": 49.5000,
    "items": [
      {
        "product_id": "p-901-croissant",
        "product_name_snapshot": "Butter Croissant",
        "unit_price": 4.5000,
        "quantity": 10.000,
        "line_total": 45.0000
      }
    ],
    "payments": [
      { "payment_method": "CASH", "amount": 49.5000 }
    ]
  }
  ```

---

### 4. Offline Synchronization Gateways (`/api/v1/sync`)

#### Endpoint: `POST /api/v1/sync/push`
- **Purpose**: Batched upload of local Room outbox events accumulated offline.
- **Headers**: `Idempotency-Key: <Batch_UUID>`
- **Request Body**: See [`engineering/05-Sync-Protocol.md`](file:///c:/Users/zaid/Desktop/optix/engineering/05-Sync-Protocol.md).
- **Response (200 OK)**:
  ```json
  {
    "status": "PROCESSED",
    "batch_id": "batch-1722211300-9902",
    "processed_count": 1,
    "failed_events": []
  }
  ```

#### Endpoint: `GET /api/v1/sync/pull`
- **Purpose**: Downloads all catalog entities updated since the client's last watermark timestamp.
- **Query Params**: `?last_synced_timestamp=1722200000000&limit=500`

---

## Step-by-Step API Implementation Order

1. **Step 1**: Implement Fastify core server setup and Zod validator compiler plugin.
2. **Step 2**: Implement global middleware handlers (`RequestLogger`, `FirebaseAuth`, `TenantContext`, `Idempotency`).
3. **Step 3**: Construct `/api/v1/auth/device-register` onboarding route.
4. **Step 4**: Construct `/api/v1/products` catalog management routes.
5. **Step 5**: Construct `/api/v1/bills/checkout` financial billing endpoints.
6. **Step 6**: Construct `/api/v1/sync/push` and `/api/v1/sync/pull` synchronization endpoints.
7. **Step 7**: Construct `/api/v1/customers`, `/api/v1/inventory`, and `/api/v1/reports` endpoints.

---

## Risks & Mitigation Matrix

| Risk Factor | Impact | Mitigation Strategy |
| :--- | :--- | :--- |
| **Duplicate Checkout Requests** | High | Require `Idempotency-Key` header on all billing POST endpoints; lock keys in Redis for 24 hours. |
| **Payload Invalidation Overhead** | Medium | Compile Zod schemas into Fastify's native AJV JSON Schema engine during startup for maximum throughput. |

---

## Frozen Architecture Sign-Off
- **Status**: FROZEN (v1.0)
- **Tag**: `v1.0-api-implementation-freeze`
