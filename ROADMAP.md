# Optix Master Execution Roadmap

Single source of truth for engineering build progress and milestone deliverables.

---

## Milestone Progress

- [x] **Milestone 1: Repository Bootstrap & Foundation Core**
  - *Objective*: A developer can clone the repository, run one command, and have the backend and Android app compiling successfully.
  - *Status*: COMPLETED (v1.0 Baseline Built & Pushed to GitHub)

- [x] **Milestone 2: Authentication & Business Tenant Context**
  - *Objective*: Firebase JWT auth, staff PIN security overlay, multi-tenant `business_id` scoping middleware, and device onboarding hydration (`POST /auth/device-register`).
  - *Status*: COMPLETED

- [x] **Milestone 3: Product Catalog & Pricing Engine**
  - *Objective*: Catalog management, categories, search, and pricing strategies (`FIXED`, `WEIGHT`, `VARIABLE`, `MARKET`).
  - *Status*: COMPLETED

- [ ] **Milestone 4: Register Billing & ESC/POS Thermal Printing**
  - *Objective*: Cart computation (<50ms), split payment tenders, receipt printing, and RJ11 cash drawer solenoid pulse.
  - *Status*: PENDING

- [ ] **Milestone 5: Shift Closing, RBAC & Customer CRM**
  - *Objective*: Shift drawer float reconciliation Z-Reports, 7-role RBAC permissions, and customer Khata credit ledger.
  - *Status*: PENDING

- [ ] **Milestone 6: Specialized Vertical Domain Extensions**
  - *Objective*: Restaurant (KDS/Table split/merge), Chicken Shop (Scale/Yield loss), Medical (Generic salt/Expiry lock).
  - *Status*: PENDING

- [ ] **Milestone 7: Room Outbox Queue & Offline Sync Engine**
  - *Objective*: Room outbox event queue, WorkManager push worker, and central conflict resolution.
  - *Status*: PENDING

- [ ] **Milestone 8: Reports, Analytics & Financial Registers**
  - *Objective*: Financial X/Z-Reports, GST/VAT tax registers, and executive BI analytics dashboards.
  - *Status*: PENDING

- [ ] **Milestone 9: SaaS Subscriptions & Admin Web Portal**
  - *Objective*: Tiered plans, 7-day offline grace period, payment recovery, and Super Admin portal.
  - *Status*: PENDING

- [ ] **Milestone 10: Production Scale & Global Release**
  - *Objective*: Production VPS deployment, multi-outlet chain fleet management, and Play Store release.
  - *Status*: PENDING
