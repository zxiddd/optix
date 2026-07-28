# 07 - Android Jetpack Compose UI Construction & Design Token Plan [v1.0 FROZEN ARCHITECTURE]

## Purpose
This document defines the implementation roadmap, visual component hierarchy, design token bindings, adaptive layout engines, screen-by-screen construction blueprints, and accessibility standards for the **Optix Native Android POS UI**.

---

## Goals
1. Implement an Apple-grade, glassmorphic Material 3 Jetpack Compose UI system responding sub-50ms to touch interactions.
2. Build adaptive responsive layouts supporting 10-inch landscape POS consoles (65/35 dual-pane split view) and 6-inch/7-inch handheld devices.
3. Standardize visual component construction using a shared design token library (`core-designsystem`).
4. Enforce strict touch ergonomics (minimum 48dp x 48dp touch targets with haptic tactile feedback).

---

## Technical Dependencies & Technology Stack

- **UI Engine**: Jetpack Compose (Material 3 BOM 2024+), Compose Foundation.
- **Architecture**: MVVM + Clean Architecture (StateFlow presentation state).
- **Design Tokens**: Custom Compose `CompositionLocal` token providers (`OptixTheme`).
- **Image Loading**: Coil 2.6+ (Disk & Memory Cache).
- **Vector Animations**: Lottie Compose 6.4+.

---

## Design System Tokens & Palette Specifications

### 1. Spatial Grid & Touch Scale
- **Base Spatial Unit**: 8dp grid system (`xs: 4dp`, `sm: 8dp`, `md: 16dp`, `lg: 24dp`, `xl: 32dp`).
- **Minimum Touch Target**: 48dp x 48dp for all interactive buttons, cards, and catalog tiles.
- **Corner Radii Scale**: `Small: 8dp`, `Medium: 16dp` (Glass cards), `Large: 24dp` (Payment sheets).

### 2. Color Palette Tokens (Dark & Light Modes)

```kotlin
// Optix Design System Color Palette Tokens
object OptixColors {
    val LightPrimary = Color(0xFF007AFF)      // Apple Electric Blue
    val DarkPrimary = Color(0xFF0A84FF)
    val LightSecondary = Color(0xFF34C759)    // Emerald Green
    val DarkSecondary = Color(0xFF30D158)
    val LightBackground = Color(0xFFF2F2F7)   // Soft Warm Canvas
    val DarkBackground = Color(0xFF000000)    // Pure OLED Black
    val LightGlassSurface = Color(0xB3FFFFFF) // 70% Alpha Translucent White
    val DarkGlassSurface = Color(0xBF1C1C1E)  // 75% Alpha Translucent Surface
    val DestructiveRed = Color(0xFFFF3B30)
}
```

---

## Screen-by-Screen Construction Blueprints

```
+-----------------------------------------------------------------------------------+
|                        10-INCH TABLET POS REGISTER LAYOUT                         |
|                                                                                   |
|  +-------------------------------------------+  +------------------------------+  |
|  | LEFT PANE: CATALOG & CATEGORY GRID (65%)  |  | RIGHT PANE: CART & PAY (35%) |  |
|  |                                           |  |                              |  |
|  | [Category Tabs: Pastry, Coffee, Bread]    |  | Line Items:                  |  |
|  | +-------+ +-------+ +-------+ +-------+   |  | - 2x Croissant      $9.00    |  |
|  | | Product| | Product| | Product| | Product|   |  | - 1x Latte          $4.50    |  |
|  | +-------+ +-------+ +-------+ +-------+   |  |                              |  |
|  |                                           |  | GROSS TOTAL:       $14.85    |  |
|  |                                           |  | [ PAY CASH ]  [ PAY CARD ]   |  |
|  +-------------------------------------------+  +------------------------------+  |
+-----------------------------------------------------------------------------------+
```

### Screen 1: Register Billing Screen (`feature-billing`)
- **Landscape 10" Console**: Dual-pane split view (Catalog Grid 65% left, Cart Column 35% right).
- **Portrait 7" Handheld**: Single-column catalog view with bottom sticky slide-up Cart Bar displaying item count and Gross Total badge.
- **Cart Column Components**: Item list with swipe-to-delete action, quantity increment/decrement steppers, applied discount badge, and primary "PAY CASH" / "PAY CARD" action buttons.

### Screen 2: Payment Tender & Split Billing Sheet
- Slide-over bottom sheet triggered by tapping "PAY".
- Features quick cash denomination buttons ($10, $20, $50, $100), change due calculator, card terminal status indicator, and customer Khata credit toggle.

### Screen 3: Restaurant Floorplan & KDS Screen (`module-restaurant`)
- **Floorplan Grid**: Visual table grid rendering real-time color-coded table states (`VACANT` Green, `OCCUPIED` Orange, `KOT_SENT` Blue, `BILL_PRINTED` Yellow).
- **KDS Display**: Full-screen kitchen order grid rendering incoming orders, preparation timers, item reject buttons, and mark-ready triggers.

### Screen 4: Manager PIN Overlay & Security Modal
- Full-screen translucent glass overlay displaying randomized numeric keypad (shuffles key positions on every entry) to prevent finger-pattern memorization by staff.

---

## Reusable Core UI Component Library (`core-designsystem`)

1. `AppButton`: Tactile primary button with click debouncing (300ms) and loading spinner.
2. `AppCard`: Glassmorphic container with 16dp rounded corners and subtle 1dp border stroke.
3. `AppTextField`: Outlined text input with explicit error labels and trailing clear icon.
4. `AppBottomSheet`: Standardized slide-over bottom sheet wrapper.
5. `AppEmptyState`: Empty catalog placeholder with high-contrast icon and descriptive action button.
6. `AppLoading`: Shimmer skeletal loading placeholder for network/database queries.

---

## Step-by-Step UI Implementation Order

1. **Step 1**: Build `core-designsystem` theme tokens, color palettes, typography scales, and shapes.
2. **Step 2**: Build reusable UI component primitives (`AppButton`, `AppCard`, `AppTextField`, `AppDialog`).
3. **Step 3**: Construct `feature-auth` Manager PIN Overlay screen with randomized keypad.
4. **Step 4**: Construct `feature-billing` Register screen (Dual-pane layout & Cart column).
5. **Step 5**: Construct Payment Tender bottom sheet and Split Billing UI.
6. **Step 6**: Construct `feature-products` Catalog grid and Category tab bar.
7. **Step 7**: Construct `module-restaurant` Floorplan grid and Kitchen KDS screen.

---

## Risks & Mitigation Matrix

| Risk Factor | Impact | Mitigation Strategy |
| :--- | :--- | :--- |
| **Compose Recomposition Lag on Budget Hardware** | High | Use `@Immutable` annotations on Compose UI state classes; pass lambda references (`() -> Unit`). |
| **Accidental Double-Taps During Rush Hours** | Medium | Apply 300ms click debouncing to all Compose button click modifiers. |

---

## Frozen Architecture Sign-Off
- **Status**: FROZEN (v1.0)
- **Tag**: `v1.0-ui-implementation-freeze`
