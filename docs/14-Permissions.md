# 14 - Comprehensive Role-Based Access Control (RBAC) & Permission Matrix

## Purpose
This document defines the Role-Based Access Control (RBAC) architecture, system roles, granular permission flags, authorization prompt rules, and manager override workflows for the **Optix** platform.

---

## Overview
Staff permission management is essential for preventing cashier fraud, inventory shrinkage, unauthorized price tampering, and unauthorized access to financial metrics. Optix enforces a 7-tier role hierarchy with over 50 granular permission controls.

---

## Staff Role Definitions

1. **Owner**: Primary account administrator. Has unrestricted access to all outlets, billing, financial settings, staff management, reports, subscriptions, and security configs.
2. **Manager**: Store operational manager. Can authorize voids, refunds, manual discounts, open cash drawers, manage inventory, and view store-level daily sales reports.
3. **Supervisor**: Shift supervisor. Can oversee waitstaff, approve minor discounts (up to 10%), manage table assignments, and perform mid-shift drawer float checks.
4. **Cashier**: Counter billing operator. Can create carts, process payments, scan items, apply pre-configured promo codes, and print receipts. Cannot void bills or edit prices.
5. **Kitchen Staff**: Chef / KDS operator. Can view kitchen orders, update prep status, mark items ready, or flag out-of-stock raw ingredients. Cannot view prices or financial metrics.
6. **Delivery Driver**: Delivery fulfillment staff. Can view assigned delivery orders, customer address details, payment status (COD vs Paid), and mark orders delivered.
7. **Accountant**: Financial auditor. Read-only access to historical transaction ledgers, tax reports (GST/VAT), expense records, and profit/loss analytics. Cannot operate POS register.

---

## Master Granular Permission Matrix

| Permission Code | Permission Description | Owner | Manager | Supervisor | Cashier | Kitchen | Delivery | Accountant |
| :--- | :--- | :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| **BILL_CREATE** | Create new bill / cart | YES | YES | YES | YES | NO | NO | NO |
| **BILL_FINALIZED_VOID** | Void a finalized bill | YES | YES | NO | NO | NO | NO | NO |
| **BILL_REFUND** | Issue customer refund | YES | YES | NO | NO | NO | NO | NO |
| **DISCOUNT_MANUAL_CUSTOM**| Apply manual discount (>10%) | YES | YES | NO | NO | NO | NO | NO |
| **DISCOUNT_MANUAL_LIMIT** | Apply manual discount (<=10%) | YES | YES | YES | NO | NO | NO | NO |
| **PRICE_OVERRIDE** | Change product unit price in cart | YES | YES | NO | NO | NO | NO | NO |
| **DRAWER_NO_SALE_OPEN** | Open cash drawer without sale | YES | YES | NO | NO | NO | NO | NO |
| **CUSTOMER_CREDIT_ALLOW** | Process sale on Store Credit | YES | YES | YES | NO | NO | NO | NO |
| **INVENTORY_ADJUST_MANUAL**| Manually adjust stock counts | YES | YES | NO | NO | NO | NO | NO |
| **PURCHASE_ORDER_CREATE** | Create & approve Purchase Orders | YES | YES | NO | NO | NO | NO | NO |
| **EXPENSE_LOG_ADD** | Log store operational expenses | YES | YES | YES | NO | NO | NO | NO |
| **REPORTS_FINANCIAL_VIEW**| View revenue & profit analytics | YES | YES | NO | NO | NO | NO | YES |
| **SETTINGS_SYSTEM_EDIT** | Modify business & tax settings | YES | NO | NO | NO | NO | NO | NO |
| **STAFF_MANAGE_ROLES** | Create/edit staff accounts & PINs | YES | YES | NO | NO | NO | NO | NO |

---

## Authorization Prompt & Manager PIN Overrides

When a Cashier attempts an action marked `Manager Only` (e.g., voiding a line item or applying a 20% discount):
1. **Modal Overlay**: POS displays full-screen overlay: *"Manager Authorization Required for Action: VOID ITEM"*.
2. **PIN Entry**: Manager enters 4-digit PIN on a randomized numeric keypad.
3. **Audit Log Record**: System validates PIN and appends audit log event:
   - `staff_id`: Cashier ID
   - `authorizing_staff_id`: Manager ID
   - `action_type`: "VOID_ITEM"
   - `timestamp`: Current epoch ms
   - `metadata`: Item name, price, void reason code.

---

## Operational Edge Cases

1. **Manager Left Store Without Delegating Authority**: Cashier needs to void a mistake but Manager is off-site.  
   *Product Rule*: System allows Owner to generate a 4-digit temporary One-Time Passcode (OTP) via WhatsApp/SMS or approve action remotely via Web Dashboard.

---

## Dependencies
- Universal Business Rules (`01-Business-Rules.md`), Database Specs (`04-Database.md`), Audit Logs.

---

## Best Practices
1. Never hardcode permission flags; store role permission matrices as dynamic configurable configurations.
2. Require PIN entry on a randomized numeric pad to prevent cashiers from memorizing finger key patterns.

---

## Open Technical Questions
1. **Biometric Authentication Support**: Should Optix support Android fingerprint / face unlock sensors for instant manager authorization on hardware tablets?
