# 22 - Store Expense Management & P&L Integration Specification

## Purpose
This document defines the expense tracking architecture, expense categories, receipt photo attachments, recurring schedules, vendor payouts, and net profit ledger integration for the **Optix** platform.

---

## Overview
A business cannot calculate true net profit by looking at gross sales alone. Store owners incur daily operational expenses: property rent, staff salaries, electricity bills, internet subscriptions, supplier cash payouts, and maintenance repairs. Optix embeds an expense tracking module into the POS and cloud dashboard.

---

## Expense Categories & Workflow

### Expense Categories
1. **Occupancy & Rent**: Store rent, property taxes, maintenance fees.
2. **Payroll & Salaries**: Staff monthly salaries, hourly wages, staff advance cash.
3. **Utilities**: Electricity, water, gas, high-speed internet, phone bills.
4. **Vendor Payouts**: Cash paid directly from register drawer to local suppliers.
5. **Store Maintenance**: Equipment repair, cleaning supplies, pest control.
6. **Marketing & Ads**: Social media ads, local flyer printing, promotional banners.
7. **Miscellaneous**: Minor petty cash expenses.

---

## Expense Data Entry & Cash Drawer Integration

```
                 EXPENSE LOGGING & DRAWER FLOW
[Manager / Cashier Taps "Add Expense"]
                 |
                 v
[Select Category: "Utilities" -> Enter Amount: $45.00]
                 |
                 v
[Select Payment Source: "Cash Drawer" vs "Bank Account"]
                 |
                 +-----------------------+-----------------------+
                 |                                               |
                 v                                               v
     [Paid from Cash Drawer]                         [Paid from Bank / Card]
  - Deducts $45.00 from Expected                 - Recorded in Expense Ledger
    End-of-Shift Cash Float                        - Zero Cash Float Impact
  - Kicks Cash Drawer Solenoid
```

---

## Recurring Expense Schedules

Merchants can set up automated recurring expense templates:
- **Monthly Rent**: $1,500 on 1st of every month.
- **Internet Bill**: $80 on 15th of every month.
- System automatically posts recurring expenses to the store P&L ledger on scheduled dates.

---

## Operational Edge Cases

1. **Cashier Pays Vendor $50 Cash from Register Drawer**: Cashier takes cash out of drawer to pay a local supplier for emergency milk delivery.  
   *Product Rule*: Cashier logs "Vendor Payout Expense ($50.00)" on POS. System records expense, attributes cashier ID, and deducts $50 from expected drawer cash count at shift end.

---

## Dependencies
- Reports & Shift Closing (`16-Reports.md`), Analytics (`17-Analytics.md`), Permissions (`14-Permissions.md`).

---

## Best Practices
1. Allow cashiers to snap a photo of paper expense receipts using the tablet's camera, attaching the photo directly to the expense record.
2. Require Manager PIN authorization for cash drawer payouts exceeding $20.00.

---

## Open Technical Questions
1. **Bank Account API Sync**: Should Optix integrate with open-banking APIs (Plaid / Yodlee) to auto-import business bank account debit expenses?
