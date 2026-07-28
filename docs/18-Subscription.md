# 18 - SaaS Subscription Engine & Plan Lifecycle Specification

## Purpose
This document defines the multi-tenant SaaS subscription engine, pricing plans, billing tiers, feature flag gating, subscription state machines, offline grace period policies, and payment recovery flows for the **Optix** platform.

---

## Overview
Optix is a commercial SaaS platform. The subscription engine must manage subscription states seamlessly across thousands of merchants without disrupting offline checkout operations during transient payment failures or expired grace periods.

---

## Subscription Tier Definitions

| Subscription Plan | Target Merchant | Price (USD) | Outlets Limit | Devices / Outlets | Staff Users | Daily Bills Limit | AI Features Included |
| :--- | :--- | :---: | :---: | :---: | :---: | :---: | :---: |
| **Free Starter Trial** | New Store Setup | $0 / month | 1 Outlet | 1 POS Device | 2 Staff | 10 Bills / Day | Basic Menu OCR |
| **Standard Pro** | Single Store SMB | $29 / month | 1 Outlet | 3 Devices | 5 Staff | Unlimited | Full AI Suite |
| **Multi-Store Chain** | Multi-Branch Chain | $79 / month | Up to 5 Outlets| 10 Devices | 25 Staff | Unlimited | Full AI + Multi-Outlet |
| **Enterprise Fleet** | Large Franchise | Custom | Unlimited | Unlimited | Unlimited | Unlimited | Custom Dedicated AI |

---

## Subscription State Machine & Lifecycle

```
 +------------------+
 |    FREE_TRIAL    | (14 Days / 10 Bills per day cap)
 +--------+---------+
          |
          v (Merchant Upgrades)
 +------------------+           +------------------+
 |   ACTIVE_PAID    |<--------->|  AUTORENEW_PEND  |
 +--------+---------+           +------------------+
          |
          | (Payment Fails or Renewal Date Passes)
          v
 +------------------+
 |   GRACE_PERIOD   | (7 Days / Full Billing Allowed, Warning Banner Displayed)
 +--------+---------+
          |
          | (Grace Period Expires without Payment)
          v
 +------------------+
 | EXPIRED_READONLY | (Billing Locked / Read-Only History Access)
 +------------------+
```

---

## Offline Subscription Grace Period Rules

1. **The 7-Day Offline Grace Principle**: If an active paid subscription expires while the Android POS terminal is operating offline without internet connectivity:
   - The POS terminal **allows full cashier billing operations** for up to **7 consecutive days** or **500 bills**.
   - The POS displays a subtle yellow status bar warning: *"Subscription renewal pending. Connect to internet to sync plan status."*
2. **Hard Block Upon Expiry**: If the 7-day offline grace period expires without network re-validation:
   - Cart checkout button locks with message: *"Subscription Expired - Please renew your plan to resume billing."*
   - Historical records, reports, and data export features remain accessible in read-only mode.

---

## Payment Failure & Recovery Flows

1. **Automated Retry Schedule**: System attempts payment collection on credit card at Day 0, Day 3, and Day 6 of Grace Period.
2. **Notification Warnings**: Triggers in-app pop-ups, WhatsApp alerts, and emails to the Merchant Owner with direct payment recovery links.
3. **Instant Activation**: Upon successful payment, cloud server pushes immediate subscription activation token via background push notification to all merchant POS devices.

---

## Operational Edge Cases

1. **Merchant Exceeds Device Limit for Plan**: Merchant on Pro Plan (3 devices limit) attempts to activate a 4th Android tablet.  
   *Product Rule*: Activation screen displays limit notification: *"Device limit reached (3/3). Upgrade to Multi-Store Plan to add more terminals."*

---

## Dependencies
- Admin Portal (`19-Admin-Portal.md`), Settings (`15-Settings.md`), API Endpoints (`05-API.md`).

---

## Best Practices
1. Store cryptographically signed subscription state tokens locally in Android KeyStore to prevent unauthorized local system clock tampering.
2. Provide seamless 1-click subscription upgrade buttons inside the POS app and Web Portal.

---

## Open Technical Questions
1. **Regional Local Payment Gateways**: Which regional payment gateways (Stripe, Razorpay, MercadoPago, UPI Subscriptions) should be integrated for automated recurring billing?
