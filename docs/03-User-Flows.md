# 03 - Detailed Product Workflows & User Journeys

## Purpose
This document defines the step-by-step user flows, interaction patterns, state transitions, and exception handling for core product journeys across the **Optix** platform.

---

## Overview
A great POS user interface eliminates friction during high-stress store operations. Optix workflows are engineered to minimize button taps, prevent cashier confusion, handle hardware failures gracefully, and support multi-user store dynamics (e.g., multiple waiters managing tables simultaneously).

---

## Product User Workflows

### Flow 1: Waiter Captain Order Creation & KDS Routing
```
[Waiter Selects Table 5] -> [Selects Items & Modifiers] -> [Taps "Send to Kitchen"]
                                                                   |
                                                                   v
[Table Grid Updates to "KOT PRINTED"] <--- [Kitchen KDS Display Rings / KOT Prints]
```
1. **Table Selection**: Waiter taps Table 5 on the visual floorplan grid (Status: Vacant, Color: Green).
2. **Order Building**: Waiter selects items (e.g., 2x Burger, 1x Coke with modifier "No Ice").
3. **Kitchen Dispatch**: Waiter taps "Send to Kitchen". System dispatches Kitchen Order Ticket (KOT) to Kitchen printer or KDS screen.
4. **State Update**: Table 5 status updates to `OCCUPIED / KOT_SENT` (Color: Orange).

---

### Flow 2: Multi-Waiter Concurrent Table Editing & Conflict UX
1. **Scenario**: Waiter A opens Table 8 on Tablet 1 to add drinks. Simultaneously, Waiter B opens Table 8 on Tablet 2 to add desserts.
2. **Local Locks**: When Waiter A opens Table 8, a temporary local soft lock banner displays on Waiter B's tablet: *"Waiter Alex is updating Table 8"*.
3. **Conflict Resolution**: If both submit additions offline, items are merged additively into Table 8's active order session without overwriting existing line items.

---

### Flow 3: Table Merging & Table Splitting
- **Table Merge Flow**:
  1. Manager selects Table 3, taps "Merge Table", and selects Table 4.
  2. System consolidates all open line items from Table 4 into Table 3.
  3. KOT notification prints in kitchen: *"TABLE MERGE: Table 4 items moved to Table 3"*. Table 4 reverts to `VACANT`.
- **Table Split Flow**:
  1. Waiter opens Table 7 (Gross Total: $120), taps "Split Bill".
  2. Selects items for Guest A ($50) and Guest B ($70), or selects "Split Evenly by 4 Guests".
  3. System generates sub-bills for independent payment processing.

---

### Flow 4: Kitchen Order Rejection & Item Ready Notification (KDS)
1. **Item Rejection**: Kitchen chef taps an item on the KDS screen and selects "Out of Stock / Reject".
2. **Waiter Alert**: KDS sends audio alert and banner pop-up to Waiter's POS terminal: *"Kitchen Rejected: Salmon Steak on Table 12 (Out of Stock)"*.
3. **Order Adjustment**: Waiter visits table, suggests alternative item, and updates table order.
4. **Item Ready**: Chef taps "Mark Ready". Table grid displays green notification badge: *"Table 12 Ready for Serving"*.

---

### Flow 5: Hardware Printer Failover & Auto Re-Routing UX
```
[Print Job Dispatched] -> [Printer Paper End / Bluetooth Drop Detected]
                                     |
                                     v
                  [POS Displays Alert Sheet: "Printer 1 Offline"]
                                     |
         +---------------------------+---------------------------+
         |                                                       |
         v                                                       v
[Select "Retry Print"]                         [Select "Re-route to Bar Printer"]
```
1. **Printer Disconnection**: Cashier finalizes sale, but Counter Thermal Printer is out of paper.
2. **Alert Sheet**: POS displays non-blocking slide-over sheet: *"Receipt Printer 1 Offline - Paper Out"*.
3. **Failover Options**: Cashier can select:
   - **Retry Print**: After replacing paper roll.
   - **Re-route**: Print to backup Bar printer.
   - **Digital Receipt**: Send WhatsApp / SMS receipt directly to customer's phone.

---

### Flow 6: High-Speed Cashier Checkout & Quick Cash Payment
1. Cashier scans item EAN barcode or taps item tile. Item appends to cart (<5ms).
2. Cashier taps "Pay", selects "Quick Cash $50".
3. System calculates Change Due ($50 - $34.50 = $15.50), finalizes bill in local DB, kicks cash drawer, and resets register screen for next customer.

---

## Operational Edge Cases

1. **Waiters Attempting to Split a Paid Bill**: Cashier accidentally split a bill that was already finalized.  
   *Product Rule*: Finalized bills cannot be split. Split workflows must execute during the `DRAFT / OPEN_TABLE` lifecycle stage prior to final tender.

---

## Dependencies
- Business Modules (`09-Business-Modules.md`), Printer Subsystem (`11-Printer.md`), UI Tokens (`08-UI-System.md`).

---

## Best Practices
1. Ensure all table state transitions update visual color coding instantly across terminals.
2. Render clear visual progress indicators during KOT printing and split billing steps.

---

## Open Technical Questions
1. **Self-Service Customer QR Ordering**: Should restaurant guests be allowed to scan a table QR code on their smartphone to build draft carts directly on the waiter's POS terminal?
