# 21 - Advanced Inventory Management, Purchase Orders & Supplier Specification

## Purpose
This document defines the advanced inventory management architecture, Purchase Order (PO) workflows, supplier management, stock inwarding, inter-branch transfers, wastage logging, and physical stock count audit protocols for the **Optix** platform.

---

## Overview
Retail stores, grocery outlets, and pharmacies lose thousands of dollars annually to unaccounted shrinkage, damaged stock, expired goods, and inaccurate purchase orders. Optix expands beyond simple stock counting into a full-scale inventory control system.

---

## Inventory Subsystems & Workflows

```
+-----------------------------------------------------------------------------------+
|                        ADVANCED INVENTORY WORKFLOW                                |
|                                                                                   |
|  +--------------------+   +---------------------+   +--------------------------+  |
|  | Supplier Directory |-->| Purchase Order (PO) |-->| Stock Inwarding (GRN)    |  |
|  | Manage Vendors     |   | Draft & Send to Vendor  | Increases Available Stock|  |
|  +--------------------+   +---------------------+   +--------------------------+  |
|                                                                 |                 |
|                                                                 v                 |
|  +--------------------+   +---------------------+   +--------------------------+  |
|  | Inter-Branch Transfer| | Wastage / Damage Log|   | Physical Stock Audit     |  |
|  | Move Stock Between |   | Record Broken/Expired | | Reconcile Physical vs DB|  |
|  | Outlets            |   | Stock               |   | Stock Counts             |  |
|  +--------------------+   +---------------------+   +--------------------------+  |
+-----------------------------------------------------------------------------------+
```

---

## Subsystem Specifications

### Subsystem 1: Supplier Directory & Catalog Mapping
- Manage suppliers/vendors: `id`, `company_name`, `contact_person`, `phone`, `email`, `tax_id`, `payment_terms`.
- Map products to specific suppliers with supplier item codes and cost prices.

### Subsystem 2: Purchase Orders (PO) & Goods Received Notes (GRN)
- **Workflow**:
  1. Store manager creates PO for Supplier "Apex Distributors" requesting 100 units of Butter.
  2. PO status: `DRAFT` -> `SENT_TO_SUPPLIER` -> `PARTIALLY_RECEIVED` -> `COMPLETED`.
  3. Upon physical delivery, manager performs **Stock Inwarding (GRN)**: enters received quantities, cost price adjustments, and batch expiry dates.
  4. System automatically increases product `current_stock` and updates item cost price (Weighted Average Costing).

### Subsystem 3: Inter-Branch Stock Transfers (Multi-Outlet)
- Transfer stock from Outlet A (Main Warehouse) to Outlet B (Downtown Store).
- Status flow: `TRANSFER_REQUESTED` -> `IN_TRANSIT` -> `RECEIVED_AT_DESTINATION`.
- Stock deducts from Outlet A upon dispatch and adds to Outlet B upon destination confirmation.

### Subsystem 4: Wastage, Damage & Expired Returns
- Record un-sellable stock loss: `product_id`, `quantity`, `reason` (`DAMAGED_IN_STORE`, `EXPIRED`, `THEFT_SHRINKAGE`, `RECIPE_WASTE`).
- Decreases stock count and posts expense record to store P&L ledger.

### Subsystem 5: Physical Stock Count Audit
- Manager performs periodic stock audit using handheld barcode scanner or tablet audit screen.
- Scans physical shelf items. System compares **Scanned Physical Count** vs **Database Stock Count**, rendering variance report (+/- Units and $ Dollar Value Variance).
- Upon manager PIN approval, system updates database stock counts to match physical reality.

---

## Operational Edge Cases

1. **Goods Received Discrepancy**: PO requested 100 units, but supplier delivered only 80 units due to stockout.  
   *Product Rule*: System accepts partial GRN (80 units), updates inventory by +80, and marks PO status as `PARTIALLY_RECEIVED` with remaining 20 units backordered.

---

## Dependencies
- Product Schemas (`04-Database.md`), Reports (`16-Reports.md`), Expenses (`22-Expenses.md`).

---

## Best Practices
1. Calculate cost of goods sold (COGS) using Weighted Average Costing (WAC) to handle price fluctuations across shipments.
2. Require manager PIN authorization for all manual stock audit write-offs.

---

## Open Technical Questions
1. **Supplier Electronic EDI Integration**: Should Optix support automated EDI / EDIFACT electronic purchase order transmissions for enterprise chain suppliers?
