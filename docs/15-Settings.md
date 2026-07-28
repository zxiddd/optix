# 15 - Comprehensive Store & POS Settings Specification

## Purpose
This document defines the configuration schema, store settings, tax rules, receipt layout options, hardware preferences, and system toggles across the **Optix** platform.

---

## Overview
Every business operates differently. A high-speed bakery requires instant auto-print receipt rules; a full-service restaurant requires table floorplan layout settings; a pharmacy requires strict drug expiry blocks. Optix incorporates over 40 granular setting toggles grouped into logical configuration categories.

---

## Settings Categorization & Matrix

### Category 1: Business Profile & Outlet Settings
- `SET-001 (business_name)`: Legal registered name of business.
- `SET-002 (tax_registration_number)`: Tax ID / GSTIN / VAT registration number.
- `SET-003 (currency_code)`: Primary currency symbol and ISO code (USD $, EUR €, INR ₹, GBP £).
- `SET-004 (time_zone)`: Store operating time zone.
- `SET-005 (business_type)`: Selected vertical plugin (`RESTAURANT`, `CHICKEN_SHOP`, `BAKERY`, `MEDICAL`, `RETAIL`, `SALON`).

### Category 2: Tax & Pricing Settings
- `SET-006 (tax_calculation_mode)`: `INCLUSIVE` (Tax included in price) vs `EXCLUSIVE` (Tax added at cart subtotal).
- `SET-007 (default_tax_rate)`: Primary tax percentage (e.g., 5.0%, 10.0%, 18.0%).
- `SET-008 (multi_tier_tax_enabled)`: Toggle for split tax rates (e.g., CGST + SGST split).
- `SET-009 (price_rounding_mode)`: Rounding policy (`NO_ROUNDING`, `ROUND_NEAREST_WHOLE`, `ROUND_5_CENTS`).
- `SET-010 (allow_manual_price_override)`: Allow cashiers to alter product unit price in cart (Requires Manager PIN if disabled).

### Category 3: Receipt & Printing Settings
- `SET-011 (receipt_paper_width)`: `58MM` (32 column) vs `80MM` (48 column).
- `SET-012 (auto_print_on_checkout)`: Automatically dispatch print job upon payment finalization.
- `SET-013 (receipt_header_logo_enabled)`: Print monochrome raster logo image at receipt top.
- `SET-014 (receipt_header_text)`: Custom welcome text line (e.g., "Welcome to Metro Bakery").
- `SET-015 (receipt_footer_text)`: Custom closing message (e.g., "Thank you! Visit again.").
- `SET-016 (print_customer_details)`: Include customer name and phone on receipt header.
- `SET-017 (print_qr_code_payment)`: Print digital UPI/QR payment code at bottom of receipt.
- `SET-018 (cash_drawer_auto_open)`: Kick cash drawer pulse automatically on cash payments.

### Category 4: Inventory & Stock Settings
- `SET-019 (negative_inventory_mode)`: `STRICT_BLOCK` (Prevent out-of-stock sale) vs `ALLOW_WITH_ALERT` (Allow override with alert).
- `SET-020 (low_stock_threshold_default)`: Default stock count for re-order warning alerts (Default: 5 units).
- `SET-021 (auto_deduct_recipe_bom)`: Deduct raw ingredient stock automatically upon finished product sale.
- `SET-022 (batch_expiry_warning_days)`: Days before expiry date to trigger pharmacy stock warning (Default: 30 days).

### Category 5: Hardware & Peripheral Integration
- `SET-023 (primary_printer_connection_type)`: `BLUETOOTH`, `LAN_TCP`, `USB_OTG`.
- `SET-024 (primary_printer_address)`: MAC Address or TCP IP (`192.168.1.100:9100`).
- `SET-025 (weight_scale_com_port)`: Serial COM port / Baud rate for digital weight scale (Default: 9600 baud).
- `SET-026 (barcode_scanner_prefix_suffix)`: Configure scanner Carriage Return (CR/LF) handling.

### Category 6: UI, Ergonomics & Accessibility
- `SET-027 (app_theme_mode)`: `DARK_MODE`, `LIGHT_MODE`, `SYSTEM_DEFAULT`.
- `SET-028 (haptic_feedback_enabled)`: Trigger tactile vibration on button taps and scans.
- `SET-029 (cart_item_click_action)`: Action on tapping catalog tile (`INCREMENT_QTY` vs `OPEN_ITEM_MODAL`).
- `SET-030 (button_click_debounce_ms)`: Debounce time to prevent double-tap errors (Default: 300ms).

### Category 7: Security, Backup & Offline Sync
- `SET-031 (manager_pin_length)`: `4_DIGIT` vs `6_DIGIT` PIN requirement.
- `SET-032 (randomize_pin_keypad)`: Shuffle numeric keypad positions on PIN entry overlay.
- `SET-033 (auto_logout_idle_minutes)`: Automatically log out cashier after idle inactivity (Default: 15 minutes).
- `SET-034 (sync_over_cellular_data)`: Allow background WorkManager sync over 4G/5G mobile networks.
- `SET-035 (local_db_backup_frequency)`: Schedule encrypted Room DB snapshots (`DAILY`, `WEEKLY`).

---

## Operational Edge Cases

1. **Merchant Changes Currency Symbol Mid-Operation**: Merchant changes store currency from USD to EUR.  
   *Product Rule*: Historical bills preserve their original currency symbol recorded at checkout time. Only new carts adopt the updated currency setting.

---

## Dependencies
- Universal Business Rules (`01-Business-Rules.md`), Database Schemas (`04-Database.md`).

---

## Best Practices
1. Group settings into intuitive visual sections with search capability in the POS settings screen.
2. Cache store settings locally in Room SQLite so settings evaluation requires zero network I/O.

---

## Open Technical Questions
1. **Cloud Remote Settings Sync**: Should changes made to store settings on the cloud web dashboard apply instantly to active POS terminals via WebSocket push?
