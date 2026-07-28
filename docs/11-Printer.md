# 11 - ESC/POS Hardware Printing Subsystem & Driver Architecture

## Purpose
This document defines the architecture, transport abstractions, byte-level formatting engines, hardware connection managers, and queue retry handling for the **Optix** thermal printing subsystem.

---

## Overview
Receipt printing is the physical confirmation of a financial transaction in local business operations. A failed or hanging print job blocks the checkout register line. Optix incorporates a native ESC/POS bytecode generation engine supporting Bluetooth (SPP/BLE), Wi-Fi/Ethernet (TCP Socket port 9100), and USB OTG connections, formatted dynamically for 58mm (32-column) and 80mm (48-column) thermal paper rolls.

---

## Printing Subsystem Architecture

```
+-----------------------------------------------------------------------------------+
|                           PRINTING SUBSYSTEM LAYOUT                               |
|                                                                                   |
|  [ Bill Finalized ] -> [ Print Job Event ] -> [ Receipt Template Formatter ]      |
|                                                                |                  |
|                                                                v                  |
|  +-----------------------------------------------------------------------------+  |
|  |                 Native ESC/POS Bytecode Builder Engine                      |  |
|  |  - Character Encoding (CP850 / UTF-8)                                       |  |
|  |  - Raster Logo Converter (GS v 0)                                           |  |
|  |  - QR Code Generator Bytes (GS ( k)                                         |  |
|  |  - Cash Drawer Solenoid Pulse (ESC p 0 25 250)                              |  |
|  +--------------------------------------+--------------------------------------+  |
|                                         |                                         |
|                               +---------v----------+                              |
|                               | Transport Router   |                              |
|                               +---------+----------+                              |
|                                         |                                         |
|         +-------------------------------+-------------------------------+         |
|         |                               |                               |         |
|  +------v-------+                +------v-------+                +------v-------+  |
|  | Bluetooth Driver|             | Wi-Fi / LAN Driver|           | USB OTG Driver|  |
|  | (SPP / BLE Socket)|           | (TCP Port 9100) |           | (Android USB) |  |
|  +------+-------+                +------+-------+                +------+-------+  |
|         |                               |                               |         |
|         v                               v                               v         |
|  [ Thermal Printer ]           [ Kitchen Printer ]             [ Counter Printer ]|
+-----------------------------------------------------------------------------------+
```

---

## Bytecode Command Specifications (ESC/POS)

### Core ESC/POS Byte Sequences
- **Initialize Printer**: `0x1B, 0x40` (`ESC @`)
- **Select Cut Mode (Full/Partial Cut)**: `0x1D, 0x56, 0x41, 0x00` (`GS V A 0`)
- **Cash Drawer Kick Pulse (Pin 2)**: `0x1B, 0x70, 0x00, 0x19, 0xFA` (`ESC p 0 25 250`)
- **Text Alignment**:
  - Left: `0x1B, 0x61, 0x00`
  - Center: `0x1B, 0x61, 0x01`
  - Right: `0x1B, 0x61, 0x02`
- **Text Formatting**:
  - Bold ON: `0x1B, 0x45, 0x01` / Bold OFF: `0x1B, 0x45, 0x00`
  - Double Width / Double Height: `0x1D, 0x21, 0x11`

---

## Receipt Layout Specifications

### 58mm Paper Roll Layout (32 Column Grid)
```
--------------------------------
      METRO BAKERY & CAFE       
    123 Commercial Street       
       Tel: +1 555-0199         
--------------------------------
Receipt: DEV01-INV-1042         
Date: 2026-07-29 08:30          
Staff: Cashier John             
--------------------------------
QTY ITEM                 AMOUNT 
--------------------------------
2x  Butter Croissant      $9.00 
1x  Caffe Latte           $4.50 
--------------------------------
SUBTOTAL:                $13.50 
TAX (10%):                $1.35 
--------------------------------
TOTAL:                   $14.85 
--------------------------------
      Thank You For Visiting!   
--------------------------------
```

---

## Hardware Management & Transport Discovery

1. **Bluetooth Discovery Manager**: Scans paired Bluetooth SPP (`00001101-0000-1000-8000-00805F9B34FB`) devices and BLE printers, managing persistent connection sockets.
2. **TCP Socket Manager**: Maintains connection pool to kitchen IP printers over port 9100 with 2.5-second socket timeout settings.
3. **USB Hardware Manager**: Utilizes Android `UsbManager` framework to claim vendor interfaces for direct USB-to-Thermal printer cables.

---

## Operational Edge Cases

1. **Thermal Printer Out of Paper Mid-Receipt**: Printer halts print job halfway through invoice.  
   *Recovery Protocol*: Printer driver detects status byte (`0x10, 0x04, 0x02` - Paper End). POS UI triggers "Printer Paper Out" warning dialog with "Retry Print" and "Re-route to Kitchen Printer" buttons. Finalized bill data remains safe in Room DB.
2. **Bluetooth Connection Drop Before Cash Drawer Pulse**: Cashier completes cash sale, but Bluetooth socket disconnects before cash drawer pulse command transmits.  
   *Recovery Protocol*: App displays "Cash Drawer Unlocked Fail - Tap to Retry Open Drawer", allowing manual pulse dispatch.

---

## Technical Dependencies
- Native Kotlin Byte Buffers, Android Bluetooth RFCOMM Sockets, Android USB Host API, Java Net Sockets.

---

## Best Practices
1. Convert bitmap logo images to monochrome 1-bit raster buffers (`GS v 0`) before dispatching to prevent printer memory buffer overflow.
2. Always wrap socket I/O writes inside background coroutine dispatchers with strict timeout bounds.

---

## Open Technical Questions
1. **Multi-Language UTF-8 Printing**: Should the ESC/POS engine incorporate dynamic canvas-to-bitmap rendering for non-Latin script printing on budget thermal printers lacking native UTF-8 font ROMs?
