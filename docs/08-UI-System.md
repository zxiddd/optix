# 08 - Design System, UI Tokens & Adaptive Layout Specification

## Purpose
This document defines the visual design system, glassmorphic UI aesthetics, component hierarchy, adaptive layout grids, accessibility standards, and design tokens for the **Optix** Android application.

---

## Overview
Optix UI is engineered to provide an Apple-grade, fluid, premium visual experience tailored for fast-paced commercial counters. Cashiers operate POS displays under harsh conditions: low lighting in dimly lit bars, bright sunlight in outdoor markets, and high-frequency repetitive finger tapping. The design system emphasizes visual clarity, large tactile touch targets (minimum 48dp), soft depth hierarchy (glassmorphism & subtle blurred layers), and immediate motion feedback.

---

## Design System Tokens

### 1. Color System (Dark & Light Mode Tokens)

```
                       COLOR PALETTE TOKENS
+-----------------------+-----------------------+-----------------------+
|  Token Name           | Light Mode Hex        | Dark Mode Hex         |
+-----------------------+-----------------------+-----------------------+
| Primary Accent        | #007AFF (Apple Blue)  | #0A84FF (Electric Blue)|
| Secondary Accent      | #34C759 (Emerald)     | #30D158 (Vibrant Green)|
| Destructive / Alert   | #FF3B30 (Crimson)     | #FF453A (Coral Red)   |
| Background Base       | #F2F2F7 (Warm Gray)   | #000000 (Pure Black)  |
| Glass Surface         | #FFFFFF (70% Alpha)   | #1C1C1E (75% Alpha)   |
| Glass Border          | #E5E5EA (50% Alpha)   | #38383A (60% Alpha)   |
| Text Primary          | #1C1C1E               | #FFFFFF               |
| Text Secondary        | #8E8E93               | #98989D               |
+-----------------------+-----------------------+-----------------------+
```

### 2. Spatial Grid & Touch Target Scale
- **Base Unit**: 8dp spatial grid system.
- **Spacing Scale**: `xs` (4dp), `sm` (8dp), `md` (16dp), `lg` (24dp), `xl` (32dp), `xxl` (48dp).
- **Minimum Touch Target**: Every interactive button, grid tile, and item card must measure at least 48dp x 48dp to eliminate miss-taps during rush hours.

### 3. Typography Scale (Inter / SF Pro Display Standard)
- **Display Large**: 34sp, SemiBold (Main Cart Amount, Checkout Total)
- **Title Large**: 22sp, Bold (Category Titles, Modal Headers)
- **Body Large**: 16sp, Medium (Product Tile Names, Line Item Details)
- **Label Small**: 12sp, Regular (SKU Badges, Secondary Meta Text)

### 4. Glassmorphism & Elevation Layering
- **Surface Elevation Level 1 (Background)**: Solid base canvas (`#F2F2F7` / `#000000`).
- **Surface Elevation Level 2 (Category / Product Cards)**: Glass translucent surface with 16dp rounded corners (`CornerRadius.Medium = 16.dp`), subtle blur backplate, 1dp border stroke (`#E5E5EA`).
- **Surface Elevation Level 3 (Floating Payment Bar)**: High-z-index elevated sticky bar with 24dp top corner radius and heavy background blur.

---

## Adaptive Layout Grids (Multi-Device Responsive Rules)

```
+-----------------------------------------------------------------------------------+
|                        10-INCH TABLET POS LAYOUT (SPLIT VIEW)                     |
|                                                                                   |
|  +-------------------------------------------+  +------------------------------+  |
|  | LEFT PANE: CATALOG & CATEGORY GRID (65%)  |  | RIGHT PANE: CART & PAY (35%) |  |
|  |                                           |  |                              |  |
|  | [Category Tabs: Pastry, Coffee, Bread]    |  | Line Items:                  |  |
|  | +-------+ +-------+ +-------+ +-------+   |  | - 2x Croissant      $9.00    |  |
|  | | Product| | Product| | Product| | Product|   |  | - 1x Latte          $4.50    |  |
|  | +-------+ +-------+ +-------+ +-------+   |  |                              |  |
|  | +-------+ +-------+ +-------+ +-------+   |  | Subtotal:          $13.50    |  |
|  | | Product| | Product| | Product| | Product|   |  | Tax:                $1.35    |  |
|  | +-------+ +-------+ +-------+ +-------+   |  | GROSS TOTAL:       $14.85    |  |
|  |                                           |  | [ PAY CASH ]  [ PAY CARD ]   |  |
|  +-------------------------------------------+  +------------------------------+  |
+-----------------------------------------------------------------------------------+
```

- **10-Inch Tablet Layout (Landscape)**: Permanent split-screen layout (Catalog Grid 65% width, Cart & Checkout Column 35% width).
- **7-Inch Handheld Layout (Portrait)**: Single column with bottom sticky slide-up Cart Bar showing item count and Gross Total badge. Tapping Cart Bar expands full-screen checkout overlay.

---

## Accessibility & Micro-Interactions

1. **Haptic Feedback Tokens**: High-speed payment success triggers `HapticFeedbackType.LongPress`; product addition triggers crisp light tick `HapticFeedbackType.TextHandleMove`.
2. **Color Contrast SLA**: All text-to-background combinations achieve WCAG 2.1 AA compliance (minimum contrast ratio 4.5:1).
3. **High Contrast Mode**: Supports Android system toggle for high-contrast accessibility borders around glass card components.

---

## Operational Edge Cases

1. **Greasy Screen Multi-Touch Artifacts**: Cashier's hands have flour or oil, causing accidental double taps on product cards.  
   *Mitigation*: Debounce UI button click events by 300ms on Jetpack Compose click modifiers.
2. **Extremely Long Product Names**: Product titled "Organic Sugar-Free Gluten-Free Whole Wheat Sourdough Bread".  
   *Mitigation*: Item grid tiles constrain text to 2 lines max with `TextOverflow.Ellipsis`, rendering full name in cart line items.

---

## Technical Dependencies
- Jetpack Compose Material 3 (`androidx.compose.material3`), Compose Foundation, Google Fonts (Inter / Roboto).

---

## Best Practices
1. Define all colors, typography sizes, shapes, and elevations as reusable Compose `CompositionLocal` design tokens.
2. Avoid hardcoding static pixel measurements; calculate layout dimensions using relative dp/sp units and responsive layout weight modifiers.

---

## Open Technical Questions
1. **Custom Font Performance**: Should we embed the Inter font TTF assets locally in the APK package to prevent network latency delays during initial app hydration?
