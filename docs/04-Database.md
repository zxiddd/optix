# 04 - Product Data Model & Entity Specifications

## Purpose
This document defines the high-level product data model, business entities, data relationships, multi-tenant isolation schemas, and flexible pricing strategy abstractions for the **Optix** platform.

---

## Overview
The Optix data model must support multi-tenant SaaS operations, multi-outlet chain hierarchies, granular role permissions, customer credit ledgers, advanced inventory tracking, and specialized vertical modules while maintaining high performance on local Room SQLite databases and central cloud PostgreSQL storage.

---

## Core Domain Entities & Relationships

```
+-----------------------------------------------------------------------------------+
|                              OPTIX PRODUCT DATA MODEL                             |
|                                                                                   |
|  +--------------------+        +---------------------+      +------------------+  |
|  |    businesses      |<-------|      outlets        |<-----|      users       |  |
|  +---------+----------+        +----------+----------+      +------------------+  |
|            |                              |                                       |
|            +---------------+--------------+                                       |
|                            |                                                      |
|  +-------------------------v-------------------------+                            |
|  | categories / products / inventory_batches         |                            |
|  +-------------------------+-------------------------+                            |
|                            |                                                      |
|  +-------------------------v-------------------------+                            |
|  | bills / bill_items / bill_payments / customer_ledger|                            |
|  +---------------------------------------------------+                            |
+-----------------------------------------------------------------------------------+
```

---

## Entity Descriptions & Data Attributes

### 1. `Business` (Tenant Master)
- Core organization holding subscription plan, currency, multi-outlet configurations, and global settings.
- Attributes: `id`, `name`, `business_type`, `currency`, `time_zone`, `subscription_tier_id`, `created_at`.

### 2. `Outlet` (Store Branch)
- Physical store location belonging to a Business.
- Attributes: `id`, `business_id`, `name`, `address`, `phone`, `tax_number`, `is_active`.

### 3. `Product` (Catalog Master)
- Catalog product with extensible pricing strategy enums.
- Attributes:
  - `id`, `business_id`, `category_id`, `sku`, `barcode`, `title`, `description`.
  - `pricing_strategy`: ENUM (`FIXED`, `WEIGHT`, `VARIABLE`, `MARKET`).
  - `unit_price`: Standard price.
  - `cost_price`: Purchase cost for margin calculations.
  - `track_inventory`: Boolean.
  - `current_stock`: Numeric stock level.
  - `reorder_level`: Threshold alert limit.
  - `is_archived`: Boolean (Soft Delete).

### 4. `PricingStrategy` Model
- **FIXED**: Fixed unit price ($4.50).
- **WEIGHT**: Mass unit price ($12.00/kg). Requires scale input.
- **VARIABLE**: Open cashier amount entry at cart addition.
- **MARKET**: Daily floating rate with default fallback price.

### 5. `Customer` (CRM & Credit Ledger)
- Customer profiles for loyalty and credit ("Khata") sales.
- Attributes: `id`, `business_id`, `full_name`, `phone`, `email`, `credit_limit`, `current_balance`, `loyalty_points`.

### 6. `Bill` & `BillItem` (Financial Ledger)
- Immutable sales transaction header and line items.
- Attributes: `id`, `business_id`, `outlet_id`, `invoice_number`, `staff_id`, `customer_id`, `subtotal`, `tax_total`, `discount_total`, `gross_total`, `status` (`DRAFT`, `FINALIZED`, `VOIDED`, `REFUNDED`).

---

## Data Isolation & Multi-Tenancy Strategy

- **Tenant Boundary**: Every database entity includes indexed `business_id` and `outlet_id` foreign keys.
- **Local Room Storage**: Each terminal's SQLite database stores only the data relevant to its assigned `business_id` and `outlet_id`.

---

## Operational Edge Cases

1. **Changing Pricing Strategy on Active Products**: Merchant changes a product from `FIXED` to `MARKET`.  
   *Product Rule*: Historical bill line items preserve the original snapshotted pricing strategy and unit price recorded at transaction lock.

---

## Dependencies
- Universal Business Rules (`01-Business-Rules.md`), Engineering PostgreSQL Schema (`engineering/01-PostgreSQL-Schema.md`).

---

## Best Practices
1. Snapshot product titles, SKUs, unit prices, and tax rates directly inside `bill_items` at checkout to ensure historical receipts never change if the catalog is modified later.
2. Use precise numeric types (`NUMERIC(12,4)`) for all financial calculations.

---

## Open Technical Questions
1. **Global Barcode Catalog**: Should Optix maintain a shared global barcode lookup database to auto-populate product names when scanning retail FMCG items?
