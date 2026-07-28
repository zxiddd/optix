# 09 - Platform Security & Compliance Checklist Specification [v1.0 FROZEN ARCHITECTURE]

## Purpose
This document defines the security architecture, cryptographic baselines, authentication boundaries, multi-tenant data isolation rules, local storage encryption, infrastructure hardening, and audit compliance controls across the **Optix** ecosystem.

---

## Goals
1. Establish a zero-trust security model protecting merchant financial records, customer personal data, and system API endpoints.
2. Enforce **100% Multi-Tenant Isolation**: Guarantee zero cross-tenant data leakage via strict backend database scoping and Row-Level Security (RLS).
3. Protect local POS hardware data at rest using AES-256 SQLCipher database encryption backed by the Android KeyStore System.
4. Guarantee OWASP Top 10 security compliance across all Fastify REST API routes.

---

## Master Security Control Checklist

```
+-----------------------------------------------------------------------------------+
|                           OPTIX SECURITY MATRIX                                   |
|                                                                                   |
|  +--------------------+   +---------------------+   +--------------------------+  |
|  | AUTHENTICATION     |   | AUTHORIZATION       |   | DATA AT REST             |  |
|  | Firebase JWT       |   | Multi-tenant scoping|   | SQLCipher AES-256 DB     |  |
|  | Signed Device Token|   | 7-Role RBAC Matrix  |   | Android KeyStore Key     |  |
|  +--------------------+   +---------------------+   +--------------------------+  |
|                                                                                   |
|  +--------------------+   +---------------------+   +--------------------------+  |
|  | DATA IN TRANSIT    |   | API INPUT SAFETY    |   | AUDIT INTEGRITY          |  |
|  | TLS 1.3 Strict     |   | Zod Schema Compiler |   | Immutable Audit Logs     |  |
|  | Certificate Pinning|   | Parameterized SQL   |   | Manager PIN Signatures   |  |
|  +--------------------+   +---------------------+   +--------------------------+  |
+-----------------------------------------------------------------------------------+
```

---

## Detailed Security Domain Controls

### 1. Authentication & Token Management
- [x] **Firebase JWT Validation**: Every API request must pass valid Bearer JWT. Backend validates signature against Firebase public keys.
- [x] **Signed Device Hardware Tokens**: Devices receive cryptographically signed device tokens during onboarding (`POST /auth/device-register`), bounding requests to registered hardware UUIDs.
- [x] **Manager PIN Protection**: Local manager PIN hashed with bcrypt (cost factor 10). 3 consecutive wrong PIN entries lock authorization overlay for 60 seconds.

### 2. Authorization & Multi-Tenant Data Isolation
- [x] **Mandatory Tenant Scoping**: Backend `TenantContextMiddleware` extracts `business_id` and `outlet_id` from claims, prepending `WHERE business_id = $1` to every Prisma ORM database query.
- [x] **Row-Level Security (RLS)**: PostgreSQL tables enforce `ENABLE ROW LEVEL SECURITY` policies as a defense-in-depth database guard.
- [x] **RBAC Enforcement**: API endpoints validate user role against the 50+ permission matrix (`14-Permissions.md`).

### 3. Data Encryption at Rest & in Transit
- [x] **Local Database Encryption**: Android Room SQLite database encrypted using SQLCipher AES-256 (`SupportFactory`).
- [x] **Android KeyStore Integration**: Database encryption key generated and stored inside hardware-backed Android KeyStore (`MasterKey.Builder`).
- [x] **Encrypted Preferences**: Local key-value pairs stored via `EncryptedSharedPreferences`.
- [x] **TLS 1.3 Strict Transport**: All API network traffic enforced over HTTPS TLS 1.3 with certificate pinning on the Android app.

### 4. Input Sanitization & Anti-Injection Controls
- [x] **Zod DTO Schema Compilation**: Incoming request bodies validated against strict Zod schemas; invalid payloads rejected with HTTP 422 immediately.
- [x] **SQL Injection Immunity**: Prisma ORM utilizes parameterized query bindings exclusively; raw SQL string concatenation is strictly banned.

### 5. Infrastructure & Application Hardening
- [x] **Security Headers (Helmet.js)**: Configures HSTS, X-Content-Type-Options, X-Frame-Options (`DENY`), and Content-Security-Policy.
- [x] **Android Code Obfuscation (R8/ProGuard)**: Release APKs compiled with R8 shrinking and code obfuscation, stripping debug symbols and class names.
- [x] **UFW Firewall & Non-Root PM2**: Server ports restricted to 80, 443, and SSH 22. Node.js PM2 process runs under unprivileged `optix` system user.

---

## Step-by-Step Security Implementation Sequence

1. **Step 1**: Implement Android KeyStore MasterKey helper and SQLCipher database integration.
2. **Step 2**: Implement Fastify `FirebaseAuthMiddleware` and `TenantContextMiddleware`.
3. **Step 3**: Configure PostgreSQL Row-Level Security (RLS) policies on `products`, `bills`, and `customers` tables.
4. **Step 4**: Enable R8 ProGuard code obfuscation rules in Android `app/build.gradle.kts`.
5. **Step 5**: Run automated Snyk dependency vulnerability scans in GitHub Actions CI workflow.

---

## Frozen Architecture Sign-Off
- **Status**: FROZEN (v1.0)
- **Tag**: `v1.0-security-checklist-freeze`
