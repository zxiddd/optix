# 03 - Node.js Backend Architecture & Service Blueprint [v1.0 FROZEN ARCHITECTURE]

## Purpose
This document defines the backend software architecture, framework decision rationale, API gateway standards, Domain-Driven Design (DDD) modular structure, event bus, Redis cache & queue system, transaction boundaries, idempotency protocols, disaster recovery objectives, and observability alerting thresholds for the **Optix Node.js/TypeScript Backend Core**.

---

## Goals
1. Establish a high-throughput, sub-100ms p99 response time API engine using **Fastify** on Node.js 20 LTS & TypeScript 5+.
2. Implement **Domain-Driven Design (DDD)** co-locating controllers, services, repositories, DTOs, mappers, validators, and events inside `/src/api/v1/modules/`.
3. Decouple synchronous API requests from asynchronous background processing via a **Redis + BullMQ Queue Engine** feeding dedicated worker processes (`workers/`).
4. Enforce strict multi-tenant isolation (`business_id` scoping), idempotency key validation, and granular endpoint-specific rate limiting.
5. Provide enterprise-grade observability, SLA alerting thresholds, and a 1-hour Recovery Time Objective (RTO) / 5-minute Recovery Point Objective (RPO) disaster recovery plan.

---

## Technical Dependencies & Stack Selection

```
                  BACKEND TECHNOLOGY STACK
+-----------------------+-----------------------------------------------+
| Layer                 | Technology Selected & Rationale               |
+-----------------------+-----------------------------------------------+
| Runtime & Language    | Node.js 20 LTS, TypeScript 5.3+ (Strict Mode) |
| Web Framework         | Fastify 4.26+ (Selected over Express for 2x   |
|                       | speed, built-in JSON schema validation & TS)  |
| Database ORM & Pool   | Prisma ORM 5.10+, PostgreSQL 16, pgBouncer    |
| In-Memory Cache/Queue | Redis 7.2+, BullMQ 5.0+                       |
| Authentication        | Firebase Admin SDK (JWT), Device Auth Tokens  |
| Process Manager       | PM2 Cluster Mode on Ubuntu 24.04 LTS VPS      |
| Storage Abstraction   | Local Storage (Dev) / AWS S3 MinIO (Prod)     |
| Observability         | Winston, Prom-Client, OpenTelemetry           |
+-----------------------+-----------------------------------------------+
```

### Framework Rationale: Fastify vs. Express
- **Selected**: **Fastify**
- **Rationale**: Optix handles thousands of simultaneous offline sync payloads and barcode scans. Benchmark tests demonstrate Fastify processes up to 75,000 req/sec compared to Express's 25,000 req/sec. Fastify's native JSON Schema compiler (AJV) validates incoming DTO payloads with zero performance overhead, providing native TypeScript type safety.

---

## System Topology Blueprint

```
+-----------------------------------------------------------------------------------+
|                            UBUNTU 24.04 LTS HOST VPS                              |
|                                                                                   |
|  +-----------------------------------------------------------------------------+  |
|  |                            Nginx Reverse Proxy                              |  |
|  |  - TLS 1.3 Termination (Certbot SSL)                                          |  |
|  |  - HTTP Rate Limiting & DDoS Mitigation                                       |  |
|  +--------------------------------------+--------------------------------------+  |
|                                         |                                         |
|         +-------------------------------+-------------------------------+         |
|         |                               |                               |         |
|  +------v-------+                +------v-------+                +------v-------+  |
|  | Fastify API  |                | Fastify API  |                | BullMQ Queue |  |
|  | Instance 1   |                | Instance 2   |                | Worker Node  |  |
|  +------+-------+                +------+-------+                +------+-------+  |
|         |                               |                               |         |
|         +-------------------------------+-------------------------------+         |
|                                         |                                         |
|                        +----------------+----------------+                        |
|                        |                                 |                        |
|              +---------v----------+            +---------v----------+             |
|              | Redis Cache/Queue  |            | pgBouncer Pool     |             |
|              | (Port 6379)        |            | (Port 6432)        |             |
|              +--------------------+            +---------+----------+             |
|                                                          |                        |
|                                                +---------v----------+             |
|                                                | PostgreSQL 16 DB   |             |
|                                                +--------------------+             |
+-----------------------------------------------------------------------------------+
```

---

## Cache & Queue Architecture (Redis + BullMQ)

### 1. Redis Cache Layer Responsibilities
- **Idempotency Keys**: Caches `Idempotency-Key` hashes for 24 hours to prevent duplicate bill processing.
- **Short-Lived Catalog Cache**: Stores serialized store product catalogs (`TTL: 15 minutes`), invalidated on product update events.
- **Rate Limiter Counters**: High-speed sliding-window rate limit counters per IP/Tenant.
- **Session & Feature Flags**: Caches tenant active subscription flags and remote feature flags (`TTL: 5 minutes`).

### 2. BullMQ Background Queue Topology
```
[ API Handler ] ---> [ Push Job to BullMQ Queue ] ---> [ Redis Broker ]
                                                             |
                                                             v
[ Dead-Letter Queue (DLQ) ] <--- (Fail x5) <--- [ Background Worker Process ]
```
- **Job Queues**:
  - `sync-queue`: Process offline outbox payloads.
  - `email-queue`: Dispatch receipt PDFs and welcome emails.
  - `analytics-queue`: Aggregate hourly store metrics.
  - `backup-queue`: Execute nightly database snapshots.
- **Retry & Timeout Policy**:
  - Max Retries: 5 attempts.
  - Backoff: Exponential starting at 10 seconds.
  - Job Timeout: 30 seconds per job execution.
  - Failure Handling: Unrecoverable failures move to `sync-dlq` (Dead-Letter Queue) triggering an instant PagerDuty alert.

---

## API Standards & Conventions

### 1. Success Response Standard Format (200 OK / 201 Created)
```json
{
  "status": "SUCCESS",
  "data": {
    "bill_id": "9a12c8b0-4421-4822-9011-cc21134a1b02",
    "invoice_number": "DEV01-INV-1042",
    "gross_total": 49.50
  },
  "meta": {
    "timestamp": 1722211250000,
    "correlation_id": "req-9022-a84"
  }
}
```

### 2. Error Response Standard Format (RFC 7807)
```json
{
  "type": "https://optixpos.com/errors/inventory-insufficient",
  "title": "Insufficient Stock Count",
  "status": 409,
  "detail": "Product 'Croissant' stock (2.0) is lower than requested checkout quantity (5.0).",
  "instance": "/api/v1/bills/checkout",
  "code": "ERR_INVENTORY_STRICT_BLOCK"
}
```

### 3. Pagination Standard
- **Cursor Pagination** for high-volume infinite scroll endpoints (`bills`, `products`, `audit_logs`):
  `GET /api/v1/bills?limit=50&cursor=eyJpZCI6ImJpbGwtOTAyMiJ9`
- **Offset Pagination** for administrative web tables (`GET /api/v1/admin/merchants?page=1&limit=25`).

---

## Idempotency Strategy & Transaction Boundaries

### 1. Idempotency Key Engine
- Mandatory Header for mutating endpoints (`POST /api/v1/bills`, `POST /api/v1/sync/push`, `POST /api/v1/payments/refund`):
  `Idempotency-Key: 7b9a4012-c419-4e02-a890-cc12093a11b0`
- **Execution Flow**:
  1. API checks Redis for `idempotency:<key>`.
  2. If key exists and status is `PROCESSING`, API returns `HTTP 409 Conflict (Request in progress)`.
  3. If key exists and status is `COMPLETED`, API returns cached response body immediately.
  4. If key does not exist, API sets key with status `PROCESSING`, executes transaction, updates key status to `COMPLETED` with cached response payload (`TTL: 24 hours`).

### 2. Database Transaction Boundaries (Prisma `$transaction`)
Multi-table writes must execute inside atomic database transactions:
- **Bill Finalization Transaction**:
  ```typescript
  await prisma.$transaction(async (tx) => {
      // 1. Create Bill Header
      // 2. Create Line Items
      // 3. Deduct Inventory Stock
      // 4. Accrue Customer Loyalty Points
      // 5. Update Customer Khata Ledger Balance
  });
  ```
- **Stock Inwarding (GRN) Transaction**: Updates inventory stock, creates batch entity, updates Weighted Average Costing (WAC).

---

## Endpoint-Specific Rate Limiting Specifications

| Route Category | Window | Limit | Response Upon Exceeding |
| :--- | :--- | :--- | :--- |
| **Auth Routes** (`/auth/*`) | 15 Minutes | 20 Requests | HTTP 429 - IP Block |
| **Sync Push/Pull** (`/sync/*`) | 1 Minute | 120 Requests | HTTP 429 - Retry-After Header |
| **Standard API** (`/products`, `/bills`) | 1 Minute | 300 Requests | HTTP 429 - Soft Throttle |
| **Public Web Hooks** | 1 Minute | 60 Requests | HTTP 429 - Drop |

---

## Security, Token Policy & Audit Logging

1. **Authentication Token Lifecycle**:
   - Access Token: Short-lived Firebase JWT (`TTL: 1 hour`).
   - Refresh Token: Stored securely in HTTP-only, SameSite cookies (`TTL: 30 days`).
   - Device Hardware Token: Bound to unique hardware device UUID signed by backend on onboarding.
2. **Key Rotation & Secrets**:
   - JWT signing keys rotated every 90 days. Secrets injected via environment variables (never committed to git).
3. **Immutable Security Audit Log**: Every elevated action (price override, void, role change) appends to immutable `audit_logs` database table.

---

## File Storage Architecture

```
                       FILE STORAGE ABSTRACTION
+-------------------------------------------------------------------+
|                        StorageService Interface                   |
+-------------------------------------------------------------------+
                                  |
         +------------------------+------------------------+
         |                                                 |
         v                                                 v
[ Local Disk Driver ]                             [ AWS S3 / MinIO Driver ]
(Used during Local Dev)                           (Used in Production VPS)
- Upload Path: ./uploads/                         - S3 Bucket: optix-merchant-assets
- Serves static merchant logos                    - Pre-signed URLs for receipt images
```

---

## Observability & SLA Alerting Thresholds

Prometheus metrics monitoring triggers automated PagerDuty / Slack alerts when SLAs cross thresholds:

| Metric Indicator | Alert Trigger Threshold | Urgency Level |
| :--- | :--- | :--- |
| **CPU Usage** | > 80% sustained for 5 minutes | Critical |
| **Memory Allocation** | > 85% of VPS total RAM | Critical |
| **PostgreSQL Pool** | > 90% connection pool exhaustion | Critical |
| **Worker Failure** | Any job routed to Dead-Letter Queue (DLQ)| High |
| **Sync Backlog** | Outbox queue backlog > 1,000 events | High |
| **HTTP 5xx Error Rate**| > 1% of total requests | Critical |

---

## Disaster Recovery, Backup & RPO/RTO Objectives

- **Recovery Point Objective (RPO)**: **5 Minutes** (Maximum acceptable data loss window).
- **Recovery Time Objective (RTO)**: **1 Hour** (Maximum acceptable total system outage duration).
- **Backup Schedule**:
  - Continuous PostgreSQL Write-Ahead Logging (WAL) archiving.
  - Daily full database dumps (`pg_dump`) executed at 02:00 AM UTC, uploaded to encrypted offsite S3 buckets.
  - 30-day automated backup retention policy.
- **Restore Protocol**: Automated recovery script (`scripts/db-restore.sh`) pulling latest full dump and applying WAL logs up to point-in-time failure.

---

## Step-by-Step Backend Implementation Order

1. **Step 1**: Initialize `apps/backend/` Fastify TypeScript project, install dependencies, setup strict `tsconfig.json`.
2. **Step 2**: Configure Prisma ORM with pgBouncer and execute baseline migrations.
3. **Step 3**: Setup Redis connection client and BullMQ queue engine infrastructure.
4. **Step 4**: Implement Fastify plugin pipeline (`RequestLogger`, `FastifyHelmet`, `RateLimiter`, `FirebaseAuth`, `TenantContext`).
5. **Step 5**: Build `/src/api/v1/modules/` core domain modules (`auth`, `business`, `products`, `billing`, `sync`).
6. **Step 6**: Implement `StorageService` driver (Local/S3) and file upload endpoints.
7. **Step 7**: Deploy background worker process scripts (`workers/`) using PM2 process manager.
8. **Step 8**: Configure Prometheus metric exporters and health probe endpoints (`/health/readiness`).

---

## Risks & Mitigation Matrix

| Risk Factor | Impact | Mitigation Strategy |
| :--- | :--- | :--- |
| **Redis Broker Failure** | High | Configure Redis with AOF persistence; fall back to local in-memory fallback cache for rate limiting if Redis drops. |
| **Prisma Transaction Deadlocks** | Medium | Keep interactive `$transaction` callbacks concise (<500ms execution time); acquire locks in deterministic table order. |

---

## Frozen Architecture Sign-Off
- **Status**: FROZEN (v1.0)
- **Tag**: `v1.0-backend-architecture-freeze`
