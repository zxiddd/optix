# 05 - Sync Protocol & Event Payload Serialization Specs

## Purpose
This document provides exact JSON sync payload schemas, outbox SQLite entity structures, Lamport logical clock algorithms, and delta sync endpoints for the **Optix Synchronization Subsystem**.

---

## 1. Local Room Outbox Table DDL (`outbox_events`)

```sql
CREATE TABLE IF NOT EXISTS outbox_events (
    event_id TEXT PRIMARY KEY NOT NULL,
    entity_type TEXT NOT NULL,      -- 'BILL', 'PRODUCT', 'CUSTOMER', 'SHIFT'
    entity_id TEXT NOT NULL,
    action_type TEXT NOT NULL,      -- 'CREATE', 'UPDATE', 'ARCHIVE'
    payload_json TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'PENDING', -- 'PENDING', 'SYNCING', 'SYNCED', 'FAILED'
    retry_count INTEGER NOT NULL DEFAULT 0,
    created_at INTEGER NOT NULL     -- Epoch milliseconds
);

CREATE INDEX IF NOT EXISTS idx_outbox_status_created ON outbox_events(status, created_at);
```

---

## 2. Sync Push JSON Payload Contract (`POST /api/v1/sync/push`)

```json
{
  "device_id": "ARM64-TABLET-90B42",
  "batch_id": "batch-1722211300-9902",
  "events": [
    {
      "event_id": "evt-b840-9901",
      "entity_type": "BILL",
      "action_type": "CREATE",
      "timestamp": 1722211250000,
      "payload": {
        "bill_id": "9a12c8b0-4421-4822-9011-cc21134a1b02",
        "invoice_number": "DEV01-INV-1042",
        "staff_id": "u-441-mgr",
        "subtotal": 45.0000,
        "tax_total": 4.5000,
        "discount_total": 0.0000,
        "gross_total": 49.5000,
        "status": "FINALIZED",
        "items": [
          {
            "product_id": "p-901-croissant",
            "product_name_snapshot": "Butter Croissant",
            "unit_price": 4.5000,
            "quantity": 10.000,
            "line_total": 45.0000
          }
        ]
      }
    }
  ]
}
```

---

## 3. Sync Push Response JSON Contract (200 OK)

```json
{
  "status": "PROCESSED",
  "batch_id": "batch-1722211300-9902",
  "processed_count": 1,
  "failed_events": [],
  "server_timestamp": 1722211305000
}
```

---

## 4. Logical Lamport Clock Version Algorithm

```typescript
// Node.js TypeScript Sync Server Timestamp Generator
export function calculateNextVersionTimestamp(currentServerVersion: bigint, clientEventTimestamp: bigint): bigint {
    const now = BigInt(Date.now());
    let nextVersion = now > currentServerVersion ? now : currentServerVersion + 1n;
    if (clientEventTimestamp > nextVersion) {
        nextVersion = clientEventTimestamp + 1n;
    }
    return nextVersion;
}
```
