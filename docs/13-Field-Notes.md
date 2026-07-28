# 13 - Field Notes & Real-World Domain Observations

## Purpose
This document captures empirical field observations, cashier feedback, real-world operational pain points, and store counter environment realities collected directly from local businesses. It ensures every engineering feature in **Optix** directly solves a verified real-world problem.

---

## Overview
Software engineered in sterile office environments often fails on real retail counters. In actual local businesses, cashiers operate with wet or flour-covered hands, internet connections drop during peak revenue hours, paper rolls run out mid-queue, and staff members share terminal PINs if login screens are cumbersome. Optix features are strictly mapped to empirical field observations.

---

## Empirical Field Observations & Product Feature Matrix

| Observation ID | Business Type | Real-World Field Observation / Pain Point | Architectural Feature Solution |
| :--- | :--- | :--- | :--- |
| **FN-001** | Bakery / Cafe | **Severe Internet Blackouts During Peak Morning Rush**: ISP Wi-Fi drops at 8:30 AM. Existing cloud POS systems froze, leaving 15 customers waiting in line. | **Offline-First Room Engine**: 100% local database execution. Checkout completes in <50ms without network interaction. |
| **FN-002** | Chicken Shop | **Greasy, Wet Hands on Touchscreens**: Staff handling raw meat cannot accurately tap small 16dp UI buttons on budget screens. | **Tactile Ergonomics & 48dp Touch Targets**: Large glassmorphic touch targets with 300ms click debouncing. |
| **FN-003** | Medical Store | **Selling Expired Medicines by Mistake**: In a crowded shop, cashier accidentally picked an expired drug batch from the shelf. | **Strict Expiry Lock Enforcement**: POS scans batch barcode and blocks expired stock with high-visibility warning. |
| **FN-004** | Retail Store | **Receipt Paper Jam Mid-Queue**: Thermal printer jams halfway through receipt print, causing cashier to panic and re-process payment. | **Idempotent Transaction Finalization**: Payment is committed once in DB; "Reprint Receipt" button re-dispatches buffer without duplicating bill. |
| **FN-005** | Restaurant | **End-of-Shift Cash Shortages**: Cash in drawer does not match reported sales due to untracked manual drawer openings. | **No-Sale Drawer Audit Logging**: Opening cash drawer without active sale prompts for manager PIN and logs audit event. |
| **FN-006** | Grocery Store | **Daily Price Changes for Commodities**: Fresh produce prices fluctuate every morning. Editing database catalogs daily was too slow. | **Market-Price Dynamic Override**: Items flagged `is_market_rate` prompt unit price entry at cart addition time. |

---

## Real-World Operational Insights

### Field Insight 1: Staff Literacy & High Turnover
- **Observation**: Local stores experience high cashier turnover. New employees receive less than 15 minutes of training before operating the POS.
- **Engineering Directive**: UI must be self-explanatory. Iconography must match physical cash register conventions. Actions must require maximum 2 screen taps to complete checkout.

### Field Insight 2: Hardware Abuse & Power Instability
- **Observation**: POS Android tablets are frequently unplugged, dropped, or suffer sudden power cuts when store generators kick in.
- **Engineering Directive**: Room database write transactions must be atomic and crash-resilient (`WAL` journal mode enabled), preventing database index corruption during sudden power loss.

---

## Operational Edge Cases Identified in Field

1. **Staff Sharing Manager PINs**: Cashiers memorize manager PIN by watching over their shoulder, bypassing approval security.  
   *Countermeasure*: PIN entry screen uses randomized numeric keypads that shuffle key positions after every entry.
2. **Barcode Label Damage**: Scratched barcode on a grocery item fails to scan.  
   *Countermeasure*: POS supports instant partial-text search by item name or quick numeric SKU keypad entry.

---

## Technical Dependencies
- Direct feedback loop from pilot merchant deployments.

---

## Best Practices
1. Benchmark every new POS feature on an actual store counter during peak trading hours before rolling out platform-wide.
2. Maintain an append-only field observation log linked directly to software issue tickets.

---

## Open Technical Questions
1. **Merchant Feedback Telemetry**: Should Optix embed an anonymized crash and UI bottleneck telemetry logger to track high-latency screen transitions in real-world store environments?
