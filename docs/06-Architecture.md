# 06 - High-Level System Architecture & Infrastructure Blueprint

## Purpose
This document specifies the end-to-end system architecture, component topologies, infrastructure design, process orchestration, and security boundaries for the **Optix** enterprise POS ecosystem.

---

## Overview
Optix is structured around a distributed **Local-First, Cloud-Synced Architecture**. The core operating principle is complete decentralization of runtime dependencies: an Android POS terminal relies zero percent on active server connectivity to perform core business functions. The cloud infrastructure acts as a resilient analytical central hub, tenant manager, and cross-device sync relay server.

---

## Complete Infrastructure & Component Topology

```
+-----------------------------------------------------------------------------------+
|                              UBUNTU 24.04 LTS VPS                                 |
|                                                                                   |
|  +-----------------------------------------------------------------------------+  |
|  |                            Nginx Reverse Proxy                              |  |
|  |  - TLS 1.3 Termination (Let's Encrypt Certbot)                                |  |
|  |  - Rate Limiting / DDoS Mitigation                                          |  |
|  |  - Static Asset / Web Dashboard CDN Routing                                 |  |
|  +--------------------------------------+--------------------------------------+  |
|                                         |                                         |
|                               +---------v----------+                              |
|                               | PM2 Process Manager|                              |
|                               +---------+----------+                              |
|                                         |                                         |
|         +-------------------------------+-------------------------------+         |
|         |                               |                               |         |
|  +------v-------+                +------v-------+                +------v-------+  |
|  | Node.js Core |                | Node.js Core |                | Worker Node  |  |
|  | Cluster App1 |                | Cluster App2 |                | Sync Process |  |
|  +------+-------+                +------+-------+                +------+-------+  |
|         |                               |                               |         |
|         +-------------------------------+-------------------------------+         |
|                                         |                                         |
|                               +---------v----------+                              |
|                               | PostgreSQL 16 DB   |                              |
|                               | (Prisma ORM Pool)  |                              |
|                               +--------------------+                              |
+-----------------------------------------------------------------------------------+
```

---

## Component Responsibilities & Subsystems

### Subsystem A: Android Local Engine
- **Presentation Layer**: Native Jetpack Compose screens rendering responsive Material 3 UI layouts.
- **Application Layer**: ViewModel state holders managing StateFlow primitives and domain Use Cases.
- **Persistence Layer**: Encrypted Room SQLite storage managing transactional entities and local outboxes.
- **Background Orchestrator**: WorkManager managing network detection, retry policies, and background outbox sync flushes.

### Subsystem B: Edge Hardware Layer
- Thermal Printers (ESC/POS over Bluetooth SPP/BLE, LAN TCP, USB OTG).
- RJ11 Cash Drawers driven by printer solenoid pulses.
- Barcode Scanners (USB HID / Bluetooth SPP) and Digital Weighing Scales (RS232 Serial over USB adapter).

### Subsystem C: Cloud Infrastructure Layer
- **OS Baseline**: Ubuntu 24.04 LTS VPS with UFW firewall (Ports 80, 443, SSH key-only 22).
- **Reverse Proxy**: Nginx 1.24+ forwarding traffic to local PM2 cluster ports via HTTP/2 sockets.
- **App Server**: Node.js 20 LTS running TypeScript Express API clustered across available CPU cores via PM2.
- **Database Server**: PostgreSQL 16 with tuned connection pooling (`pgBouncer` / Prisma Pool).

---

## Security Model & Tenant Isolation

1. **Authentication Boundary**: Firebase Authentication manages user identity verification. Firebase issues JWT tokens containing signed user UIDs.
2. **Authorization & Multi-Tenancy**: Node.js authentication middleware verifies JWT signature, queries tenant association, and attaches `req.user = { uid, business_id, role }` to every request.
3. **Database Tenant Isolation**: PostgreSQL database queries strictly enforce `WHERE business_id = $1`. Database Row-Level Security (RLS) policies validate table-level permissions.
4. **Local Database Security**: Room SQLite encrypted via SQLCipher using a device-unique encryption key stored securely in the Android KeyStore System.

---

## Deployment & Production Process Setup

```bash
# Production Deployment Process Architecture (Server Deployment Script)
# 1. Pull latest TypeScript build from repository
# 2. Run Prisma database migrations
# 3. Reload PM2 cluster instances zero-downtime

git pull origin main
npm ci
npm run build
npx prisma migrate deploy
pm2 reload optix-api --update-env
```

---

## Operational Edge Cases

1. **VPS Cloud Server Hard Reboot / Crash**: Sudden power outage or cloud node failure on Ubuntu VPS.  
   *Recovery*: PM2 configured with `systemd` startup hook (`pm2 startup`), automatically restoring Node.js instances on boot. PostgreSQL service automatically recovers via WAL (Write-Ahead Logging) replay. POS terminals continue operating offline without disruption.
2. **Database Connection Pool Exhaustion**: 500 POS terminals trigger background sync simultaneously upon network restoration.  
   *Mitigation*: Nginx rate limits incoming sync bursts; Prisma handles connection queuing; WorkManager on client implements randomized exponential backoff retry jitter.

---

## Technical Dependencies
- Ubuntu 24.04 LTS, Nginx 1.24, PM2 5.3+, Node.js 20 LTS, PostgreSQL 16, Prisma ORM, SQLCipher for Android.

---

## Best Practices
1. Terminate all TLS encryption at Nginx; communicate with Node.js PM2 worker instances over local loopback (`127.0.0.1`).
2. Maintain automated PostgreSQL daily WAL backup snapshots to secondary isolated storage.
3. Never expose raw database ports (5432) to the public internet; restrict DB access strictly to local host.

---

## Open Technical Questions
1. **Containerization Strategy**: Should the production backend be migrated to Docker / Kubernetes containers, or maintained as direct system services on Ubuntu 24.04 VPS for lower memory overhead?
