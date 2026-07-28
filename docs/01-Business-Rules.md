# 01 - Universal Business Rules & Product Domain Invariants

## Purpose
This document defines the universal business rules, product domain invariants, operational boundaries, and data integrity constraints for the **Optix** platform. These rules govern all client POS applications, web portals, and server APIs.

---

## Overview
A Point of Sale platform is a financial and operational system of record. Ambiguous business rules lead to cashier fraud, tax audit failures, negative inventory discrepancies, and financial leakage. Optix enforces strict domain rules across all business modules, explicitly explaining the business rationale ("WHY") behind every rule.

---

## Universal Product Business Rules

### Rule 101: Bill Composition & Line Item Mandate
- **Rule**: A bill (receipt/invoice) cannot exist or be finalized without at least one valid product line item.
- **Enforcement**: Checkout buttons remain disabled on empty carts. Draft carts with zero items cannot be persisted to historical registers.
- **Why It Exists**: Prevents database clutter, blank receipt printing, and erroneous tax ledger entries.

### Rule 102: Immutability of Finalized Financial Ledgers
- **Rule**: Once a bill transitions to state `FINALIZED` or `PAID`, its header, items, applied discounts, taxes, and staff attributions are permanently immutable.
- **Enforcement**: Editing or deleting a finalized bill is strictly blocked. Corrective actions require generating a distinct `reversal` or `refund` transaction record with mandatory manager authorization.
- **Why It Exists**: Ensures legal accounting compliance, tax audit integrity, and prevents cashiers from retroactively altering bills to steal cash.

### Rule 103: Product Archival Over Permanent Deletion
- **Rule**: Products, categories, tax profiles, and staff accounts referenced in past transactions can never be permanently deleted from the system.
- **Enforcement**: Deleting an entity sets `is_archived = true`. Archived products are hidden from cashier product catalogs but remain preserved in historical reports, receipts, and analytics.
- **Why It Exists**: Maintains historical accuracy for reports, tax returns, and customer receipt lookups.

### Rule 104: Flexible Product Pricing Strategies (`pricing_strategy`)
- **Rule**: Products must specify a structured `pricing_strategy` attribute rather than static boolean flags:
  - `FIXED`: Standard unit price (e.g., $4.50 per Croissant).
  - `WEIGHT`: Price calculated per mass unit (e.g., $12.00/kg * 1.425kg).
  - `VARIABLE`: Open manual price entered by cashier at checkout (e.g., custom service fee).
  - `MARKET`: Daily floating commodity price, pre-filled with today's market rate but overridable.
- **Enforcement**: Cart total calculation delegates to the respective strategy handler.
- **Why It Exists**: Provides an extensible pricing engine that accommodates retail, butcher shops, cafes, and service businesses without database schema alterations.

### Rule 105: Inventory Non-Negativity & Vertical Override Policies
- **Rule**: Product stock counts cannot drop below zero (`stock >= 0`).
- **Vertical Exception**:
  - *Strict Mode (Retail, Medical, Bakery)*: Out-of-stock items block checkout.
  - *Override Mode (Restaurant, Chicken Shop)*: Checkout is permitted under temporary negative overrides if enabled in merchant settings, but generates an un-reconciled inventory alert.
- **Why It Exists**: Prevents ghost inventory counts while allowing fast-paced kitchens to serve food without halting checkout during stock count delays.

### Rule 106: Staff Role-Based Action Authorization (RBAC)
- **Rule**: High-risk actions (voiding bills, line-item price overrides, manual discounts above threshold, opening cash drawer without sale, manual stock adjustments) require explicit elevated permissions or Manager PIN validation.
- **Enforcement**: Cashier app displays Manager PIN overlay. Every audited action logs: `staff_id`, `authorizing_staff_id`, `action_type`, `timestamp`, `device_id`, and `reason`.
- **Why It Exists**: Prevents internal employee theft and unauthorized price tampering.

### Rule 107: Customer Credit ("Khata") & Debt Limits
- **Rule**: Unpaid balance checkouts (Store Credit / "Khata") are permitted only for registered customers and must not exceed the customer's `credit_limit`.
- **Enforcement**: Exceeding credit limit blocks checkout unless overridden by a Manager PIN.
- **Why It Exists**: Protects merchants from uncollectible customer debt while offering flexible credit sales.

---

## State Transition Diagram: Bill Status Lifecycle

```
 +--------------+
 |     DRAFT    | (Items added, cart modified)
 +-------+------+
         |
         | (Payment Tendered & Validated)
         v
 +--------------+
 |   FINALIZED  | (Saved in local DB, outbox queued, immutable)
 +-------+------+
         |
         +--------------------------+
         |                          |
         v                          v
 +---------------+          +---------------+
 |    REFUNDED   |          |    VOIDED     |
 | (Linked ledger|          | (Reversing    |
 |  reversal)    |          |  audit log)   |
 +---------------+          +---------------+
```

---

## Operational Edge Cases

1. **Manager Authorizes Void Remotely**: Manager is away from store while cashier needs to void a bill.  
   *Product Rule*: Manager can issue a temporary 4-digit One-Time Passcode (OTP) via WhatsApp/SMS or approve void remotely from Web Dashboard.
2. **Catalog Price Updated During Offline Sale**: Admin updates catalog price on cloud dashboard while cashier is scanning offline at old price.  
   *Product Rule*: Finalized bill honors the price rendered to the cashier at transaction lock. The updated price applies to subsequent cart additions upon sync.

---

## Dependencies
- Permission Matrix (`14-Permissions.md`), Settings Engine (`15-Settings.md`), Customer CRM (`20-Customer-CRM.md`).

---

## Best Practices
1. Never rely on client UI alone for business logic validation; enforce identical rules on backend services.
2. Audit all override actions with rich contextual metadata.

---

## Open Technical Questions
1. **Multi-Currency Handling**: Should Optix support multi-currency tender on a single bill for cross-border tourist merchants?
