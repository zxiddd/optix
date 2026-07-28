# 05 - REST API Specification & Data Contracts

## Purpose
This document defines the REST API endpoints, JSON request/response contracts, authentication protocols, rate limits, error schemas, and synchronization endpoints for the **Optix** central Node.js backend core.

---

## Overview
The Optix API operates as an asynchronous synchronization gateway and management endpoint. Unlike traditional cloud POS platforms where every UI click makes an HTTP call, Optix client terminals interact with the backend primarily for initial hydration, periodic outbox push syncs, delta catalog pulls, and remote web dashboard management.

---

## API Architecture & Standards

- **Base URL**: `https://api.optixpos.com/api/v1`
- **Protocol**: HTTPS / TLS 1.3 Strict
- **Authentication Header**: `Authorization: Bearer <Firebase_JWT_Token>`
- **Content-Type**: `application/json`
- **Error Standard**: RFC 7807 Problem Details (`application/problem+json`)

---

## Complete API Endpoint Specifications

### Endpoint 1: Auth Session & Device Registration
- **Method & Route**: `POST /auth/device-register`
- **Purpose**: Authenticates Firebase JWT, registers new Android terminal ID, returns business profile and tenant access claims.
- **Permissions**: Valid Firebase User Token (`OWNER` or `MANAGER` role).
- **Request Payload**:
  ```json
  {
    "device_hardware_id": "ARM64-TABLET-90B42",
    "device_name": "Counter POS 1",
    "app_version": "1.4.2"
  }
  ```
- **Response Payload (200 OK)**:
  ```json
  {
    "status": "SUCCESS",
    "data": {
      "business_id": "b18a42f5-31a8-4e12-a720-0021c4ef99a1",
      "business_name": "Metro Bakery & Cafe",
      "business_type": "BAKERY",
      "device_token": "eyJhbGciOiJIUzI1NiIsIn...",
      "server_timestamp": 1722211200000
    }
  }
  ```
- **Status Codes**: `200 OK`, `401 Unauthorized`, `403 Forbidden`, `422 Unprocessable Entity`.

---

### Endpoint 2: Batch Delta Outbox Push (Sync Engine)
- **Method & Route**: `POST /sync/push`
- **Purpose**: Batched upload of local Room outbox events (bills created, stock adjusted, shifts closed) accumulated offline.
- **Permissions**: Registered Device Token / Staff Auth Token.
- **Request Payload**:
  ```json
  {
    "device_id": "ARM64-TABLET-90B42",
    "batch_id": "batch-8842-1722211300",
    "events": [
      {
        "event_id": "evt-001",
        "entity_type": "BILL",
        "action": "CREATE",
        "timestamp": 1722211250000,
        "payload": {
          "bill_id": "9a12c8b0-4421-4822-9011-cc21134a1b02",
          "invoice_number": "DEV01-INV-1042",
          "staff_id": "u-441-mgr",
          "subtotal": 45.00,
          "tax_total": 4.50,
          "gross_total": 49.50,
          "status": "FINALIZED",
          "items": [
            {
              "product_id": "p-901-croissant",
              "product_name_snapshot": "Butter Croissant",
              "unit_price": 4.50,
              "quantity": 10.0,
              "line_total": 45.00
            }
          ]
        }
      }
    ]
  }
  ```
- **Response Payload (200 OK)**:
  ```json
  {
    "status": "PROCESSED",
    "batch_id": "batch-8842-1722211300",
    "processed_count": 1,
    "failed_events": []
  }
  ```
- **Status Codes**: `200 OK` (Partial/Full Success), `400 Bad Request`, `409 Conflict`, `429 Too Many Requests`.

---

### Endpoint 3: Delta Catalog Pull (Sync Engine)
- **Method & Route**: `GET /sync/pull`
- **Purpose**: Downloads all catalog entities (categories, products, tax rules) created or updated since the last watermark timestamp.
- **Query Parameters**: `?last_synced_timestamp=1722200000000&limit=500`
- **Response Payload (200 OK)**:
  ```json
  {
    "status": "SUCCESS",
    "watermark_timestamp": 1722211400000,
    "has_more": false,
    "deltas": {
      "categories": [],
      "products": [
        {
          "id": "p-901-croissant",
          "category_id": "c-10-pastry",
          "title": "Butter Croissant",
          "unit_price": 4.50,
          "is_archived": false,
          "version_timestamp": 1722211350000
        }
      ],
      "tombstones": []
    }
  }
  ```

---

## Standard Error Schema (RFC 7807)

```json
{
  "type": "https://optixpos.com/errors/invalid-inventory-override",
  "title": "Inventory Constraint Violation",
  "status": 409,
  "detail": "Product 'Amoxicillin 500mg' stock count (2.0) is insufficient for requested checkout quantity (10.0) in Strict Inventory Mode.",
  "instance": "/api/v1/sync/push/event/evt-004",
  "code": "ERR_INVENTORY_STRICT_BLOCK"
}
```

---

## Rate Limiting & Edge Controls

| Route Tier | Limit Window | Request Limit | Action Upon Exceeding |
| :--- | :--- | :--- | :--- |
| Auth Routes | 15 Minutes | 20 Requests | 429 Block for IP |
| Sync Push / Pull | 1 Minute | 120 Requests | HTTP 429 Retry-After Header |
| General API | 1 Minute | 300 Requests | Soft Throttle |

---

## Operational Edge Cases

1. **Partial Sync Failure in Batch Push**: Out of 50 queued events in an offline sync payload, event #12 fails due to a schema constraint.  
   *Handling*: API processes all valid events (1-11, 13-50) in a single database transaction, returning event #12 in `failed_events` array with error details. Client retains ONLY event #12 in outbox.
2. **Expired Firebase Token During Background Sync**: WorkManager triggers `POST /sync/push` while cashier is logged out.  
   *Handling*: API returns `401 Unauthorized`. Android client triggers token refresh via Firebase SDK before re-submitting payload.

---

## Dependencies & API Tools
- Express.js / Node.js 20, TypeScript, Prisma ORM, Zod Schema Validation, Helmet Security Middleware, Express Rate Limit.

---

## Best Practices
1. Ensure all sync POST endpoints are strictly **idempotent** using client-generated `event_id` keys.
2. Compress sync response payloads using Gzip / Brotli compression.

---

## Open Technical Questions
1. **gRPC vs REST for Sync**: Should high-volume sync push/pull channels be migrated from JSON REST to gRPC / Protobuf payloads to reduce serialization CPU overhead on budget Android tablets?
