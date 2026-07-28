# 24 - Artificial Intelligence Architecture & Feature Specifications

## Purpose
This document defines the Artificial Intelligence (AI) architecture, machine learning models, on-device vs cloud inference division, prompt engineering pipelines, and smart automation capabilities across the **Optix** platform.

---

## Overview
AI in Optix is not a gimmick; it is engineered to eliminate tedious manual work for store owners. It automates catalog setup, predicts stockouts before they happen, detects employee fraud, synthesizes complex analytics into plain text, and enables hands-free voice billing.

---

## AI Architecture Topology

```
+-----------------------------------------------------------------------------------+
|                              OPTIX AI ARCHITECTURE                                |
|                                                                                   |
|  +-----------------------------------------------------------------------------+  |
|  | ON-DEVICE AI ENGINE (Android TFLite / ML Kit)                               |  |
|  | - Camera Menu & Receipt OCR Scanning (Zero Latency)                         |  |
|  | - Basic Voice Command Recognition                                           |  |
|  +--------------------------------------+--------------------------------------+  |
|                                         | Async Sync / Cloud Escalation
|                               +---------v----------+                              |
|                               | CLOUD AI CORE      |                              |
|                               | (LLM + Time Series)|                              |
|                               +---------+----------+                              |
|                                         |                                         |
|         +-------------------------------+-------------------------------+         |
|         |                               |                               |         |
|  +------v-------+                +------v-------+                +------v-------+  |
|  | Inventory    |                | Anomaly &    |                | Executive    |  |
|  | Forecasting  |                | Fraud Detection|              | Voice & Text |  |
|  | (Time-Series)|                | (Pattern ML) |                | Synthesizer  |  |
|  +--------------+                +--------------+                +--------------+  |
+-----------------------------------------------------------------------------------+
```

---

## Core AI Capabilities & Specifications

### Feature 1: AI Menu & Invoice OCR Scanner
- **Capabilities**: Merchant points Android tablet camera at a printed physical restaurant paper menu or supplier paper invoice.
- **Processing**: On-device Google ML Kit OCR extracts text layout, parses item titles, categories, variants, and unit prices into structured JSON, populating the product catalog in under 5 seconds.

### Feature 2: AI Inventory Forecasting & Purchase Orders
- **Model**: Time-series demand forecasting model (Prophet / ARIMA / LSTM) running on cloud servers.
- **Output**: Analyzes 90-day sales velocity, seasonal trends, day-of-week spikes, and supplier lead times, automatically generating draft Purchase Orders:
  - *"Recommendation: Order 40kg Flour from Apex Supplies by Thursday to avoid weekend stockout."*

### Feature 3: AI Fraud & Void Anomaly Detection
- **Model**: Unsupervised anomaly detection model evaluating cashier behavioral metrics.
- **Triggers**: Flags abnormal patterns:
  - Cashier A voids 4x more bills than store average.
  - Cashier B opens cash drawer without sale 12 times during shift.
- **Output**: Generates high-priority security alert report for Store Owner.

### Feature 4: AI Executive Report Synthesizer
- **Capabilities**: Converts complex financial data into plain natural language daily executive briefings sent to Owner's WhatsApp at closing time:
  - *"Good evening Metro Bakery! Today's net sales reached $1,840 (+12% vs last Wednesday). Top seller was Butter Croissant (120 units). Note: Milk stock is low (3 units remaining)."*

### Feature 5: AI Voice Billing Assistant
- **Capabilities**: Hands-free voice billing for butcher shops and high-speed counters:
  - Cashier speaks: *"Add 1.5 kg Mutton Curry Cut and 2 Cokes"*.
  - POS speech-to-text engine parses intent and appends items to active cart instantly.

---

## Operational Edge Cases

1. **AI OCR Misinterprets Price on Damaged Menu**: Blurred print causes OCR to read $12.00 as $1.00.  
   *Product Rule*: AI scanner displays interactive confirmation screen highlighting parsed items for quick merchant verification before saving catalog entries.

---

## Dependencies
- Android ML Kit, TensorFlow Lite, Cloud LLM Gateway APIs, Time-Series ML Pipelines.

---

## Best Practices
1. Design all AI features as **human-in-the-loop recommendations** requiring explicit merchant approval.
2. Fall back gracefully to standard UI workflows if AI services are offline or disabled.

---

## Open Technical Questions
1. **On-Device LLM Fine-Tuning**: Should compact 3B-parameter LLMs (e.g., Gemma 2B) be executed locally on high-end Android POS hardware for offline voice parsing?
