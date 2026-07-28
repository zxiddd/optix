# 10 - Enterprise Synchronization Engine & Conflict Architecture

## Purpose
This document defines the technical design, protocol specifications, conflict resolution algorithms, and queue mechanics for the **Optix Synchronization Engine**. It governs asynchronous data exchange between local Android Room databases and the central cloud PostgreSQL infrastructure.

---

## Overview
Offline-first synchronization is the most complex subsystem in a distributed POS architecture. Cashiers must continue operating during extended network disconnects without risking financial data loss, duplicate invoice numbers, or database corruption upon re-establishing connectivity. The Optix Sync Engine implements an **Outbox Event Sourcing Pattern** coupled with **Deterministic Conflict Resolution Protocols**.

---

## Sync Engine Topology & Data Flow

```
+-----------------------------------------------------------------------------------+
|                            CLIENT SIDE (ANDROID ROOM)                             |
|                                                                                   |
|  [ Local UI Operation ] -> [ Save Business Entity ] -> [ Append Outbox Event ]     |
|                                                                |                  |
|                                                                v                  |
|  [ Local Outbox Cleared ] <- [ WorkManager Push Worker ] <--- [ Outbox Queue ]    |
+-----------------------------------------|-----------------------------------------+
                                          | HTTP POST /api/v1/sync/push
                                          v
+-----------------------------------------------------------------------------------+
|                             SERVER SIDE (NODE.JS & POSTGRES)                      |
|                                                                                   |
|  [ Verify Device Auth ] -> [ Execute Idempotent Batch ] -> [ Update Watermark ]   |
|                                                                |                  |
|                                                                v                  |
|  [ HTTP 200 PROCESSED ] --------------------------------> [ PostgreSQL DB ]       |
+-----------------------------------------------------------------------------------+
```

---

## Outbox Pattern & Event Serialization

Every local mutation (creating a bill, archiving a product, closing a shift) executes as an atomic local Room SQLite transaction containing two writes:
1. Write to domain table (e.g., `bills`).
2. Write to `outbox_events` table:

```sql
CREATE TABLE outbox_events (
    event_id TEXT PRIMARY KEY NOT NULL,
    entity_name TEXT NOT NULL,
    entity_id TEXT NOT NULL,
    action_type TEXT NOT NULL, -- 'CREATE', 'UPDATE', 'ARCHIVE'
    payload_json TEXT NOT NULL,
    status TEXT DEFAULT 'PENDING', -- 'PENDING', 'SYNCING', 'FAILED'
    retry_count INTEGER DEFAULT 0,
    created_at INTEGER NOT NULL
);
```

---

## Conflict Resolution Matrix & Domain Rules

| Data Domain | Synchronization Strategy | Conflict Resolution Rule | Rationale |
| :--- | :--- | :--- | :--- |
| **Bills & Payments** | Monotonic Append-Only Ledger | **Zero Overwrite (Server Union)** | Financial transactions are immutable. Synced bills are inserted sequentially; duplicate invoice numbers are prevented via unique device ID prefixes. |
| **Catalog & Products** | Delta Version Watermarking | **Last-Server-State-Wins (LWW)** | Central cloud dashboard is master authority for global pricing and catalog structure. |
| **Inventory Counts** | Delta Stock Modification Log | **Additive Delta Aggregation** | Deductions from offline sales are subtracted as relative delta values (`-2.0`) rather than absolute count overrides. |
| **Customer Registers** | Unique Attribute Matching | **Merge by Phone / Client UUID** | Offline created customers merge on unique phone number or global UUID. |

---

## Queue Management & Retry Mechanics

1. **Batching**: Outbox items are grouped into micro-batches of up to 50 events per HTTP payload to optimize network socket efficiency.
2. **Network Detection & WorkManager**: WorkManager listens for network state changes (`NetworkType.CONNECTED`). Upon connection:
   - Triggers `POST /api/v1/sync/push`.
   - Upon successful HTTP 200 response, marks events `status = 'SYNCED'` and schedules async purge of synced records.
3. **Exponential Backoff Jitter**: If push fails due to network drop or 5xx server error, WorkManager retries using exponential backoff with randomized jitter:
   $$\text{Next Retry Delay} = \min\left(\text{Initial Delay} \times 2^{\text{Attempt}}, \text{Max Delay}\right) \pm \text{Jitter}$$
   - Initial Delay: 5 seconds.
   - Max Delay: 15 minutes.

---

## Operational Edge Cases

1. **Server Receives Duplicate Push Payload (Network Timeout)**: Android app sends batch `POST /sync/push`. Server processes database writes successfully, but mobile network drops before server HTTP 200 response reaches Android app.  
   *Resolution*: Android app retries same batch upon reconnect. Server validates payload using idempotent `event_id` hashes, skips duplicate database inserts, and returns `HTTP 200 PROCESSED`.
2. **Device Offline for Extended Period (Watermark Drift)**: Terminal remains offline for 30 days while catalog undergoes 500 price changes on cloud dashboard.  
   *Resolution*: Upon reconnect, device triggers `GET /sync/pull?last_synced_timestamp=<Watermark>`. Server streams catalog deltas in paged batches of 500 records until local local watermark matches server timestamp.

---

## Technical Dependencies
- Room SQLite Outbox Table, AndroidX WorkManager, Kotlin Coroutines, Node.js Idempotent Processing Middleware, PostgreSQL Transactions.

---

## Best Practices
1. Never execute raw DELETE statements on domain tables; append tombstone markers (`is_archived = true`) to sync deletions downstream.
2. Include monotonically increasing `version_timestamp` on every synced record entity.

---

## Open Technical Questions
1. **Outbox Table Storage Capping**: Should local outbox tables automatically archive synced records older than 90 days to maintain fast SQLite vacuum times on 32GB flash storage Android devices?
