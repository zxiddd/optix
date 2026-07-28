# 16 - Product Reports & Export Engine Specification

## Purpose
This document defines the reporting specifications, shift closing summaries, tax registers, product velocity audits, export formats, and print layouts for the **Optix** platform.

---

## Overview
Store owners and accountants rely on accurate reporting to audit cash drawers, file monthly tax returns (GST/VAT), track staff productivity, and analyze inventory performance. Optix generates reports locally on Android terminals and on the central cloud dashboard.

---

## Core System Reports

### Report 1: Daily Sales & Financial Summary (X-Report / Z-Report)
- **Purpose**: Summarizes store financial activity for a specific calendar day or operational shift.
- **Key Metrics Included**:
  - Gross Sales, Total Discounts, Net Sales, Tax Total, Net Revenue.
  - Tender Breakdown: Cash Total, Credit Card Total, Digital QR/UPI Total, Customer Store Credit Total.
  - Total Bills Count, Average Bill Value (Basket Size), Voided Bills Count, Refund Total.
- **Export / Print**: Printable to 80mm/58mm thermal printers as standard **Z-Report** at end of day.

### Report 2: Shift Closing & Cash Drawer Reconciliation
- **Purpose**: Audits cash drawer float variance when a cashier closes a shift.
- **Key Metrics Included**:
  - Opening Cash Float + Cash Sales Collected - Cash Refunds Issued = Expected Cash in Drawer.
  - Actual Physical Cash Counted by Cashier.
  - Cash Over / Short Variance ($ Over / $ Short).
- **Audit Action**: Variances exceeding merchant threshold trigger automated notification alert to Owner.

### Report 3: Tax Ledger & Compliance Register (GST / VAT)
- **Purpose**: Generates itemized tax breakdown for accountant tax filings.
- **Key Metrics Included**:
  - Taxable Sales Subtotal, Exempt Sales Subtotal.
  - Tax Split Breakdown (e.g., Output CGST 9% + Output SGST 9% or VAT 20%).
  - Tax Collected per Category and Tax Exempt Category totals.

### Report 4: Product Sales Velocity & ABC Analysis
- **Purpose**: Classifies inventory items based on revenue contribution and unit volume.
- **Classification Categories**:
  - **Category A (Top Performers)**: Top 20% of products driving 80% of revenue.
  - **Category B (Moderate Performers)**: Mid-tier moving stock.
  - **Category C (Slow-Moving / Dead Stock)**: Low velocity items clogging inventory capital.

### Report 5: Staff Audit & Action Log
- **Purpose**: Tracks staff productivity and security override events.
- **Key Metrics Included**:
  - Sales total generated per staff member.
  - Voids, price overrides, manual discounts, and no-sale drawer openings authorized by staff ID.

---

## Report Export Formats & Sharing Channels

```
+-----------------------------------------------------------------------------------+
|                           OPTIX REPORT EXPORT ENGINE                              |
|                                                                                   |
|  +--------------------+   +---------------------+   +--------------------------+  |
|  |  Thermal Print Z   |   | PDF Formal Export   |   | CSV / Excel Spreadsheet  |  |
|  | (58mm/80mm ESC/POS)|   | (Branded Store Header)| | (Accountant Data Dump)    |  |
|  +--------------------+   +---------------------+   +--------------------------+  |
|                                                                                   |
|  +-----------------------------------------------------------------------------+  |
|  | Direct WhatsApp / Email Export (Sends PDF / Text Summary directly to Owner) |  |
|  +-----------------------------------------------------------------------------+  |
+-----------------------------------------------------------------------------------+
```

---

## Operational Edge Cases

1. **Shift Closed While Device Offline**: Cashier completes shift closing Z-Report while internet is offline.  
   *Product Rule*: Z-Report generates locally from Room DB records, prints on thermal printer, and queues for cloud sync.

---

## Dependencies
- Database Schemas (`04-Database.md`), Permissions Matrix (`14-Permissions.md`), Settings (`15-Settings.md`).

---

## Best Practices
1. Render reports asynchronously off the main UI thread to prevent screen freezes on large datasets.
2. Provide simple date-range filters (Today, Yesterday, This Week, Custom Date Range).

---

## Open Technical Questions
1. **Automated Scheduled Email Reports**: Should the cloud backend automatically send daily Z-Report PDF summaries to the Owner's email at midnight?
