# 20 - Customer Relationship Management (CRM) & Store Credit ("Khata") Specification

## Purpose
This document defines the customer relationship management (CRM) architecture, store credit ledger ("Khata"), customer debt tracking, loyalty reward point rules, and digital communication features for the **Optix** platform.

---

## Overview
Retaining loyal customers and managing local store credit accounts ("Khata") are critical for neighborhood merchants. Optix provides a native customer CRM embedded directly inside the billing workflow, allowing cashiers to look up customers by phone number, check outstanding credit balances, award loyalty points, and dispatch digital WhatsApp receipts.

---

## Core CRM Features & Modules

### Module 1: Customer Directory & Profile Management
- Search customer by phone number, full name, or barcode member card.
- Attributes: `id`, `full_name`, `phone`, `email`, `address`, `birthday`, `anniversary`, `credit_limit`, `current_credit_balance`, `loyalty_points`, `notes`.

### Module 2: Store Credit ("Khata") & Debt Ledger
- **Workflow**:
  1. Cashier selects customer "Robert Smith" (Current Credit Balance: $45.00).
  2. Cart gross total is $30.00. Cashier selects payment tender: "Store Credit / Khata".
  3. System validates `current_credit_balance + new_bill ($75.00) <= credit_limit ($100.00)`.
  4. Bill finalizes; customer's `current_credit_balance` updates to $75.00.
- **Credit Settlement**: Customer visits store to clear debt. Cashier taps "Settle Debt", receives $50 Cash, reducing balance to $25.00.

```
       CUSTOMER CREDIT ("KHATA") LEDGER FLOW
+-------------------------------------------------------+
| Customer: Robert Smith                                |
| Phone: +1 555-0142                                    |
| Credit Limit: $100.00                                 |
+-------------------------------------------------------+
| Date        | Transaction Description  | Amount | Bal |
| 2026-07-20  | Bill #DEV01-INV-902      | +$45.00| $45 |
| 2026-07-29  | Bill #DEV01-INV-1042     | +$30.00| $75 |
| 2026-07-29  | Debt Settlement (Cash)   | -$50.00| $25 |
+-------------------------------------------------------+
```

### Module 3: Loyalty Reward Point Engine
- **Accrual Rule**: Earn 1 Loyalty Point for every $10 spent.
- **Redemption Rule**: 10 Loyalty Points = $1.00 store discount at checkout.
- Cashier can redeem points directly on cart total with 1 tap.

### Module 4: Digital WhatsApp Receipts & Notifications
- Replaces or supplements paper receipts by sending formatted PDF receipts or text bill summaries directly to customer's WhatsApp number.
- Automated Birthday / Anniversary greeting messages with discount vouchers.

---

## Operational Edge Cases

1. **Customer Exceeds Credit Limit Offline**: Cashier processes credit sale while offline, but customer's credit limit was exceeded on another terminal earlier today.  
   *Product Rule*: Local POS evaluates cached credit balance; if limit is exceeded upon cloud sync, system flags account with high-priority debt alert for merchant follow-up.

---

## Dependencies
- Business Rules (`01-Business-Rules.md`), Database Schemas (`04-Database.md`), Receipts (`11-Printer.md`).

---

## Best Practices
1. Allow quick customer creation during checkout with just a Phone Number and Name in under 5 seconds.
2. Provide automated WhatsApp payment reminder buttons for overdue customer credit accounts.

---

## Open Technical Questions
1. **SMS / WhatsApp Provider Integration**: Should Optix provide built-in WhatsApp Business API messaging or allow merchants to connect their own Twilio / WhatsApp accounts?
