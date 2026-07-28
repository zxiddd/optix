# 09 - Business Domain Vertical Modules

## Purpose
This document specifies the specialized business vertical modules built on the **Optix** core engine. It details workflows, domain models, custom UI screens, and business logic for 6 target verticals: Restaurant, Chicken Shop, Bakery, Medical, Retail, and Salon.

---

## Vertical Specifications

### Module 1: Restaurant & Food Service POS
- **Key Workflows**:
  - **Visual Floorplan Grid**: Configurable table layouts across dining sections (Main Dining, Terrace, VIP Rooms). Real-time table states: `VACANT` (Green), `OCCUPIED` (Orange), `KOT_SENT` (Blue), `BILL_PRINTED` (Yellow).
  - **Waiter Captain App Mode**: Handheld Android app for waiters to record orders at table side, send KOTs to kitchen, and request bill prints.
  - **Table Merge & Split**: Merge multiple tables into single order session; split bills by item, seat, or equal customer split.
  - **Kitchen Display System (KDS)**: Android KDS touchscreen in kitchen displaying incoming orders, preparation timers, item reject options, and mark-ready triggers.
  - **Order Types**: Dine-In, Parcel / Takeaway, Direct Delivery, Delivery Aggregators (DoorDash/UberEats/Zomato).

---

### Module 2: Chicken Shop & Fresh Meat Store
- **Key Workflows**:
  - **Yield Loss Calculation**: Tracks live-to-dressed weight ratios (e.g., Live Chicken weight 2.0kg @ yield 70% = ~1.4kg dressed meat).
  - **Daily Floating Market Rates**: Prompts store owner every morning to set base rates per kg for live chicken, dressed chicken, mutton, and seafood.
  - **Cut Types & Custom Processing**: Options for Curry Cut, Biryani Cut, Boneless, Mince, Lollipop.
  - **Cleaning & Packing Charges**: Configurable line-item surcharges for specialized skinning, cleaning, or vacuum packaging.

---

### Module 3: Bakery & Confectionery
- **Key Workflows**:
  - **Bill of Materials (BOM) & Recipe Production**: Deducts raw flour, butter, sugar, and yeast when production batches (e.g., 50 loaves of Sourdough) are logged.
  - **Custom Cake Bookings**: Manages advance orders for custom birthday/wedding cakes, capturing cake design notes, photo attachments, delivery dates, and advance cash deposits.
  - **Expiry Discount Alerts**: Prompts cashier at 6:00 PM to apply 30% discount on fresh cream pastries expiring today.

---

### Module 4: Medical Store & Pharmacy
- **Key Workflows**:
  - **Generic Salt / Active Ingredient Lookup**: Searching "Paracetamol" displays all available brand variants (Crocin, Calpol, Dolo 650) sorted by stock and price.
  - **Batch & Expiry Date Locking**: Scans item batch barcode. If batch expiry date is within 30 days, displays warning; if expired, strictly locks checkout.
  - **Schedule H / H1 Prescription Register**: Logs prescribing doctor name, patient name, phone, and prescription photo for regulated drug sales.
  - **Batch Recall Management**: Instantly flags and locks all inventory units matching a manufacturer recall batch number across all outlets.

---

### Module 5: Retail Store & Supermarket
- **Key Workflows**:
  - **High-Speed Barcode Checkout**: Continuous scanning mode allowing rapid item entry with zero modal interruptions.
  - **Variant & SKU Matrix**: Multi-attribute matrix for apparel/retail (Size: S/M/L/XL, Color: Red/Blue/Black).
  - **Price-Embedded Barcode Support**: Parses GS1/Price-Verifier barcodes embedding item ID and measured weight/price directly.

---

### Module 6: Salon & Wellness Center
- **Key Workflows**:
  - **Stylist Appointment Grid**: Visual timeline view of beauticians/stylists and booking slots.
  - **Multi-Stylist Service Billing**: Assigns different stylists to individual service line items on a single customer invoice.
  - **Automated Commission Calculations**: Computes tiered percentage commissions for stylists based on completed service revenue.

---

## Operational Edge Cases

1. **Medical Batch Recall During Active Sale**: Admin issues batch recall for "Batch #B-9022" while cashier is scanning items.  
   *Product Rule*: POS immediately blocks checkout of items belonging to Batch #B-9022, prompting cashier to replace the item.

---

## Dependencies
- Universal Business Rules (`01-Business-Rules.md`), Permissions Matrix (`14-Permissions.md`), User Flows (`03-User-Flows.md`).

---

## Best Practices
1. Build vertical modules as lightweight plugin extensions on top of core billing and inventory tables.
2. Provide simple toggle switches in store settings to enable/disable vertical feature sets.

---

## Open Technical Questions
1. **Third-Party Delivery API Integration**: Should Optix support direct two-way webhook integrations with online delivery aggregators to print delivery orders directly to kitchen KDS screens?
