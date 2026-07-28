# 12 - Product Development Roadmap & AI Architecture Strategy

## Purpose
This document defines the multi-phase product roadmap, feature milestones, version release timelines, and artificial intelligence integration strategy for the **Optix** platform across its development lifecycle (MVP to Enterprise).

---

## Overview
The Optix platform expands systematically: from an rock-solid offline-first core POS (MVP), to specialized business vertical modules (V2), to multi-outlet management (V2.5), to an AI-driven predictive business operating system (V3 & Enterprise). Every release maintains 100% backward compatibility with the core Room SQLite offline outbox engine.

---

## Strategic Roadmap Phases

```
+-----------------------------------------------------------------------------------+
|                            OPTIX RELEASE TIMELINE                                 |
|                                                                                   |
|  +-------------------+   +--------------------+   +----------------------------+  |
|  | PHASE MVP         |   | PHASE V2.0         |   | PHASE V2.5                 |  |
|  | - Core Billing    |-->| - 6 Vertical Mods  |-->| - Multi-Outlet Dashboard   |  |
|  | - Room Offline    |   | - KDS & Floorplan  |   | - Advanced CRM & Loyalty   |  |
|  | - WorkManager Sync|   | - ESC/POS Profiles |   | - Digital Scale Serial     |  |
|  +-------------------+   +--------------------+   +----------------------------+  |
|                                                                 |                 |
|                                                                 v                 |
|  +--------------------------------------------+   +----------------------------+  |
|  | PHASE ENTERPRISE                           |   | PHASE V3.0 (AI CORE)       |  |
|  | - Custom Hardware SDK                      |<--| - AI Inventory Forecast    |  |
|  | - Franchise Multi-Tenant Management        |   | - AI Fraud/Anomaly Detection|  |
|  | - ERP Gateway Integration                  |   | - AI Menu OCR Cataloging   |  |
|  +--------------------------------------------+   +----------------------------+  |
+-----------------------------------------------------------------------------------+
```

---

## Phase Breakdown & Functional Scope

### Phase MVP: Core Offline POS Engine (Months 1 - 4)
- Native Android Jetpack Compose app, Room DB, WorkManager sync.
- Node.js PostgreSQL central server, Firebase Auth, Nginx PM2 stack.
- Core billing, item catalog, category grid, cash/card tenders.
- ESC/POS thermal printing over Bluetooth & USB.

### Phase V2.0: Vertical Business Modules (Months 5 - 8)
- Specialized modules: Restaurant (KDS + Table Grid), Medical (Batch/Expiry), Chicken Shop (Weight Scale + Yield Loss), Bakery (Recipes), Retail (Barcode Matrix), Salon (Stylist Slots).
- Role-based Manager PIN override authorization.

### Phase V2.5: Multi-Outlet & Advanced CRM (Months 9 - 12)
- Multi-store central dashboard for chain owners.
- Customer loyalty point engine, digital WhatsApp receipt sharing.
- Inventory transfer requests between business branches.

### Phase V3.0: AI-Powered Autonomous Business Engine (Months 13 - 18)
- **AI Feature 1: Inventory Forecasting AI**: Time-series predictive ML models analyzing historical velocity to automatically generate optimized purchase orders before stock-outs occur.
- **AI Feature 2: Fraud & Void Anomaly Detection AI**: Machine learning anomaly detection identifying suspicious void patterns, unauthorized discounts, or cash drawer open events per staff ID.
- **AI Feature 3: Smart OCR Menu & Invoice Scanner**: On-device camera scanning of physical paper menus or supplier paper invoices, converting raw text into populated catalog entities.
- **AI Feature 4: Automated Marketing Campaign AI**: Generates personalized WhatsApp/SMS retention offers for dormant customers based on past purchase frequency.

### Phase Enterprise: Franchise & ERP Ecosystem (Months 19+)
- Franchise royalty calculation engines, SAP/NetSuite ERP connector APIs.

---

## Critical Edge Cases & Risk Mitigation

1. **AI Model Offline Execution Constraints**: Running heavy predictive ML models on budget ARM64 Android POS terminals causes CPU throttling.  
   *Mitigation*: Heavy AI model training and inference execute asynchronously on central Node.js cloud servers; generated purchase order recommendations are synced down to POS terminals during background delta pulls.

---

## Technical Dependencies
- TensorFlow Lite (Android Edge AI), Node.js Python/ONNX ML Service Pipelines, Firebase Cloud Messaging (FCM).

---

## Best Practices
1. Ensure all AI features act as advisory recommendations with human-in-the-loop manager approval buttons.
2. Maintain strict feature flag toggles for every roadmap release stage.

---

## Open Technical Questions
1. **On-Device vs Cloud AI Execution**: Should simple OCR catalog scanning be executed directly on-device using ML Kit or relayed to cloud vision APIs?
