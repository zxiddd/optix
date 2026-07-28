# 19 - Super Admin Portal & Platform Operations Specification

## Purpose
This document defines the architecture, administrative capabilities, merchant management tools, system telemetry, feature flag controls, and platform financial metrics for the **Optix Super Admin Portal**.

---

## Overview
The Super Admin Portal is the internal control center used by the Optix executive team, customer support engineers, and platform administrators. It provides complete oversight across thousands of merchant tenants, active subscriptions, platform revenue metrics, system health logs, and global feature deployments.

---

## Admin Portal Modules & Capabilities

```
+-----------------------------------------------------------------------------------+
|                           OPTIX SUPER ADMIN PORTAL                                |
|                                                                                   |
|  +--------------------+   +---------------------+   +--------------------------+  |
|  | Merchant Manager   |   | Platform Financials |   | Remote Feature Flags     |  |
|  | - View 10,000+ SMBs|   | - MRR / ARR Analytics|  | - Toggle Verticals & AI  |  |
|  | - Device Audits    |   | - Churn / Retention |   | - Staged Rollouts        |  |
|  +--------------------+   +---------------------+   +--------------------------+  |
|                                                                                   |
|  +--------------------+   +---------------------+   +--------------------------+  |
|  | Broadcast Engine   |   | System Health & Logs|   | Coupon & Promo Engine    |  |
|  | - Global Banners   |   | - Sync Outbox Logs  |   | - Discount Codes         |  |
|  | - WhatsApp Alerts  |   | - VPS Server Telemetry| | - Trial Extension Keys   |  |
|  +--------------------+   +---------------------+   +--------------------------+  |
+-----------------------------------------------------------------------------------+
```

---

## Module Specifications

### Module 1: Merchant & Tenant Directory
- Search, filter, and audit all registered businesses by name, vertical type, registration date, subscription status, and total transaction volume.
- Impersonate merchant account (Support Mode) to diagnose reporting discrepancies.
- Remote device lock / revoke token for stolen POS hardware.

### Module 2: Platform Financial & SaaS Metrics
- Real-time tracking of:
  - **Monthly Recurring Revenue (MRR)** & **Annual Recurring Revenue (ARR)**.
  - **Net Revenue Churn** & **Gross Logo Churn**.
  - **Average Revenue Per User (ARPU)** & **Customer Acquisition Cost (CAC) Payback**.

### Module 3: Remote Feature Flagging & Staged Rollouts
- Dynamically enable/disable features per business type or specific merchant ID:
  - `FEATURE_KDS_MODULE`: Enable/disable KDS module.
  - `FEATURE_AI_FORECASTING`: Enable/disable AI forecasting.
- Staged percentage rollouts (e.g., enable new UI layout for 10% of merchants).

### Module 4: Global Announcement & In-App Broadcast Engine
- Dispatch targeted push banners to POS Android devices:
  - *Example*: "System Maintenance Scheduled for Sunday 2:00 AM - Offline billing will continue normally."

### Module 5: Promotional Coupon & Subscription Override Engine
- Create promo codes (e.g., `STARTUP50` for 50% off 6 months).
- Support agents can grant manual subscription extensions (e.g., +14 days trial extension).

---

## Security & Admin Role Permissions

1. **Super Admin**: Full access to financial metrics, feature flags, database backups, and staff accounts.
2. **Support Agent**: Access limited to merchant lookup, ticket logs, and device re-sync triggers. Cannot view platform revenue or alter database schemas.

---

## Operational Edge Cases

1. **Malicious Merchant Attempting Unlimited Billing**: Merchant attempts to tamper with local app code to bypass subscription checks.  
   *Product Rule*: Server detects tampered client signature during background sync, revoking sync access while flagging account in Admin Portal for review.

---

## Dependencies
- SaaS Subscription Engine (`18-Subscription.md`), Database Specs (`04-Database.md`), API Contracts (`05-API.md`).

---

## Best Practices
1. Require Multi-Factor Authentication (MFA / WebAuthn) for all Super Admin portal logins.
2. Log every administrative action (e.g., manual subscription extension, merchant account lock) in immutable admin audit trails.

---

## Open Technical Questions
1. **Automated Fraud Detection for Merchants**: Should the admin portal automatically flag merchants exhibiting abnormal void frequencies or sudden spike in high-value refunds?
