# 23 - Alert & Notification Subsystem Specification

## Purpose
This document defines the alert and notification subsystem, in-app warning banners, push notification triggers, hardware status indicators, and merchant alert channels across the **Optix** platform.

---

## Overview
Store operations move fast. Critical operational events—such as low inventory stock, thermal paper jams, subscription expiration, or un-synced offline event accumulation—must be communicated immediately to cashiers and store owners without blocking active billing operations.

---

## Notification Categories & Visual Urgency Tiers

```
+-----------------------------------------------------------------------------------+
|                        NOTIFICATION URGENCY MATRIX                                |
|                                                                                   |
|  +--------------------+   +---------------------+   +--------------------------+  |
|  | TIER 1: CRITICAL   |   | TIER 2: WARNING     |   | TIER 3: INFORMATIONAL    |  |
|  | Full Modal Overlay |   | Non-blocking Banner |   | Toast / Badge Indicator  |  |
|  | - Expired Drug Stock|  | - Low Stock Alert   |   | - Sync Complete          |  |
|  | - Hard Sync Failure|   | - Printer Paper Low |   | - Order Ready (KDS)      |  |
|  | - Sub Expired      |   | - Unsynced Outbox   |   | - Customer Loyalty Points|  |
|  +--------------------+   +---------------------+   +--------------------------+  |
+-----------------------------------------------------------------------------------+
```

---

## Notification Triggers & Channels

### 1. In-App POS Banners & Badges
- Top status bar displays real-world indicators:
  - **Green Dot**: Cloud Network Online & Synced.
  - **Yellow Banner**: Offline Mode Active (Unsynced Outbox Count: 42 events).
  - **Red Banner**: Printer Offline / Paper Out.

### 2. Push Notifications (Mobile & Web)
- Sent to Merchant Owner's mobile phone via Firebase Cloud Messaging (FCM):
  - *Low Stock Alert*: "Butter Croissant stock is at 2 units (Below re-order level 5)."
  - *Cash Drawer Shift Shortage*: "Shift 2 closed with $15.00 cash shortage by Cashier John."
  - *High-Value Void Alert*: "Bill #DEV01-INV-940 ($120.00) voided by Manager Alex."

### 3. WhatsApp & Email Digest Alerts
- Automated WhatsApp alerts for daily store sales summaries at 10:00 PM.
- Subscription expiration warnings at 7 days, 3 days, and 1 day prior to renewal.

---

## Operational Edge Cases

1. **Massive Flood of Notifications During Peak Rush**: 50 items hit low-stock threshold simultaneously during lunch rush.  
   *Product Rule*: System aggregates individual low-stock alerts into a single summary notification ("50 items reached re-order level") to prevent visual UI clutter.

---

## Dependencies
- Android System Notifications, Firebase Cloud Messaging (FCM), WhatsApp API.

---

## Best Practices
1. Ensure operational warnings (like printer offline) never erase uncommitted cart state.
2. Provide simple notification preference toggles in Store Settings.

---

## Open Technical Questions
1. **Offline Alert Queuing**: Should non-critical push notifications generated while device is offline be queued locally and batched upon reconnect?
