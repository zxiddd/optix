# 17 - Executive Business Analytics & Insights Specification

## Purpose
This document defines the business intelligence (BI) engine, executive dashboards, performance metrics, sales forecasting algorithms, and trend analytics across the **Optix** platform.

---

## Overview
While standard reports present raw operational numbers, executive analytics translate data into actionable business decisions. Store owners use Optix Analytics to identify peak revenue hours, eliminate slow-moving stock, monitor true profit margins, and optimize staffing schedules.

---

## Core Analytics Dashboards & Metrics

### Metric 1: Real-Time Sales & Revenue Comparison
- **Visuals**: Line charts comparing today's hourly revenue trajectory against yesterday and the same day last week.
- **Key Indicators**: Gross Revenue, Net Profit, Total Orders, Average Basket Value.

```
       TODAY VS LAST WEEK HOURLY SALES TRAJECTORY
  $ |                                   * (Today: $1,420)
  R |                                 *   
  E |                   *--*--*     *     + (Last Week: $1,180)
  V |         *--*    *         * +
    |  *----+      +             +
  0 +-------------------------------------------------> TIME
      8AM   10AM   12PM   2PM   4PM   6PM   8PM
```

### Metric 2: True Profit Margin & Cost Analysis
- **Formula**:
  $$\text{Gross Profit} = \sum \left( \text{Line Item Selling Price} - \text{Line Item Cost Price} \right) - \text{Direct Expenses}$$
- **Insights**: Displays true gross margin percentage per category and item, highlighting high-margin vs low-margin products.

### Metric 3: Peak Hourly & Weekly Sales Heatmaps
- **Visuals**: 7x24 grid heatmap highlighting store rush hours.
- **Actionable Insight**: Recommends optimal staff shift scheduling during high-volume windows (e.g., Friday 6:00 PM - 9:00 PM) to reduce customer queue wait times.

### Metric 4: Customer Retention & Repeat Ratio
- **Key Metrics**:
  - Percentage of sales from repeat customers vs new walk-in customers.
  - Average customer visit frequency (e.g., 2.4 visits/month).
  - Customer Lifetime Value (CLV) distribution.

### Metric 5: Dead Stock & Capital Tie-Up Analytics
- **Insights**: Flags products with zero sales in the last 30/60/90 days, calculating total capital locked in unsold inventory.

---

## Operational Edge Cases

1. **Missing Cost Prices on Products**: Store owner did not enter purchase cost prices for items.  
   *Product Rule*: Analytics renders revenue metrics cleanly while displaying an "Action Required: Add Cost Prices to View Profit Margins" banner.

---

## Dependencies
- Product Data Model (`04-Database.md`), Reports (`16-Reports.md`), Expenses (`22-Expenses.md`).

---

## Best Practices
1. Present complex analytics using intuitive visual charts (Bar charts, Donut charts, Heatmaps) rather than dense data tables.
2. Pre-aggregate analytics metrics on cloud databases to ensure instantaneous web dashboard loading.

---

## Open Technical Questions
1. **Benchmarking Against Local Competitors**: Should Optix provide anonymized regional industry benchmark analytics (e.g., "Your bakery's average ticket size is 15% higher than similar local bakeries")?
