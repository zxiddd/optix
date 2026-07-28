# 02 - Monorepo Directory Architecture & Domain-Driven Design (DDD) [v1.0 FROZEN ARCHITECTURE]

## Purpose
This document defines the official 10/10 production monorepo directory tree, Domain-Driven Design (DDD) module boundaries, application groupings, background workers, shared SDK packages, observability tools, Architecture Decision Records (ADR), and strict dependency governance rules for the **Optix** enterprise platform. This document is **FROZEN (v1.0)** as the authoritative repository blueprint.

---

## Goals
1. Organize all runnable applications under an explicit **`apps/`** container directory (`apps/android`, `apps/backend`, `apps/admin`, `apps/merchant-dashboard`, `apps/website`, `apps/customer-display`, `apps/kds`) following modern workspace standards (Turborepo / pnpm / Nx).
2. Structure every backend domain module (`apps/backend/src/api/v1/modules/`) and Android feature module as a self-contained, domain-driven package containing its own data, domain, presentation/controller, validation, events, and tests.
3. Incorporate explicit architectural subsystems: Shared Domain Events, Background Workers, Database Scripts/Seeds/Views, Observability (Logging, Metrics, Tracing, Health, Alerts, Dashboards), Benchmarks, RFCs, Architecture Decision Records (ADR), and Feature Flags.
4. Maintain a **Modular Monolith** architecture backend to avoid premature microservice over-engineering while guaranteeing clean service boundaries ready for future scale.

---

## Top-Level Monorepo Architecture Blueprint (v1.0)

```
optix/
├── .devcontainer/             # VS Code Dev Container Configuration for Instant Onboarding
├── apps/                      # Runnable Applications & Services Container
│   ├── android/               # Native Android Jetpack Compose POS App
│   ├── backend/               # Node.js / TypeScript Modular Monolith API Core
│   ├── admin/                 # Super Admin Platform Operations Web Portal
│   ├── merchant-dashboard/    # Store Owner Web Analytics & Catalog Portal
│   ├── website/               # Public Marketing, Pricing & Documentation Web App
│   ├── customer-display/      # Real-Time Customer-Facing Secondary Screen App
│   └── kds/                   # Dedicated Kitchen Display System (KDS) Screen App
├── packages/                  # Shared Reusable Monorepo Packages & SDKs
│   ├── shared-types/          # Shared TypeScript Type Definitions
│   ├── api-client/            # Generated API Client SDK
│   ├── printer-sdk/           # Hardware ESC/POS Byte Command Builders (with /docs & /examples)
│   ├── sync-sdk/              # Offline Outbox Serialization Utilities
│   ├── design-tokens/         # Shared Color, Typography & Spatial Tokens
│   └── utils/                 # Cross-Platform Math & Date Utilities
├── plugins/                   # Backend Vertical Domain Plugins
│   ├── plugin-restaurant/     # Restaurant Floorplan & Table Logic
│   ├── plugin-medical/        # Pharmacy Batch Expiry & Schedule H Rules
│   └── plugin-retail/         # Retail Barcode Matrix & Scale Protocol
├── infra/                     # DevOps Infrastructure & Server Configurations
│   ├── nginx/                 # Reverse Proxy & TLS Setup
│   ├── docker/                # Local Development & Container Setup
│   ├── postgres/              # Database Provisioning & Backup Scripts
│   ├── monitoring/            # Prometheus Metrics & Grafana Configs
│   └── ssl/                   # Let's Encrypt Certbot Setup
├── design/                    # Product Design System & Assets
│   ├── colors/                # Color Palette Definitions
│   ├── typography/            # Type Scales & Font Definitions
│   ├── icons/                 # SVG Icon Libraries
│   ├── logos/                 # Brand Assets & Raster Graphics
│   ├── components/            # Design System Specifications
│   └── figma/                 # Figma Component Guidelines
├── config/                    # Environment Configurations
│   ├── dev/                   # Local Developer Environment Variables
│   ├── staging/               # Staging Server Environment Variables
│   └── production/            # Production VPS Server Variables
├── docs/                      # Technical, Product & ADR Documentation Suite
│   ├── adr/                   # Architecture Decision Records (ADR)
│   ├── rfc/                   # Request for Comments (RFC) Architecture Proposals
│   ├── releases/              # Version Release Notes & Migration Guides
│   ├── product/               # Product Requirements & User Flows
│   ├── engineering/           # CTO Execution Specifications
│   ├── api/                   # OpenAPI / Swagger Specifications
│   ├── database/              # Entity Schemas & ER Diagrams
│   ├── design/                # Design Guidelines & UX Haptics
│   ├── business/              # SaaS Tiers, Pricing & Legal Rules
│   └── field-notes/           # Merchant Empirical Observation Logs
├── tests/                     # Cross-Platform QA & Automated Test Suites
│   ├── unit/                  # Unit Test Automation
│   ├── integration/           # Cross-Module Integration Tests
│   ├── ui/                    # Android Compose & Web UI Tests
│   ├── performance/           # Load & Stress Tests (5,000 req/sec)
│   ├── printer/               # Hardware ESC/POS Print Output Specs
│   ├── sync/                  # Network Outage & Conflict Simulations
│   ├── security/              # Vulnerability & OWASP Scans
│   └── e2e/                   # End-to-End User Journey Tests
├── benchmarks/                # Performance Benchmarking Suites
│   ├── billing/               # Cart Compute Benchmark Scripts (<5ms)
│   ├── sync/                  # Outbox Processing Benchmark Scripts
│   ├── database/              # Room & Postgres Write Latency Benchmarks
│   └── printer/               # ESC/POS Byte Buffer Benchmarks
├── examples/                  # Developer Usage Code Examples
│   ├── api/                   # REST API Integration Examples
│   ├── printer/               # Thermal Receipt Formatting Code Samples
│   └── sync/                  # Custom Sync Event Handler Examples
├── assets/                    # Static Production Assets
│   ├── logos/                 # High-Res Application Logos
│   ├── icons/                 # App Launcher Icons
│   ├── sounds/                # Tactile Audio Beeps & Chimes
│   ├── receipt/               # ESC/POS Receipt Layout Templates
│   ├── fonts/                 # Embedded Custom TTF Fonts (Inter/SF Pro)
│   ├── images/                # App Screenshots & Tutorials
│   └── animations/            # Lottie Vector Animations
└── .github/                   # CI/CD Workflows & Automation
    └── workflows/             # Build, Test, Security & Deploy Pipelines
```

---

## Subsystem Internal Architectures

### 1. Backend API & Modules (`apps/backend/src/api/v1/`)

```
apps/backend/
├── database/                  # Database Layer
│   ├── prisma/                # Prisma Schema & Migrations
│   ├── seeds/                 # Initial Catalog & Role Seeder Scripts
│   ├── backups/               # Automated Database Dump Utilities
│   ├── scripts/               # Migration & Vacuum Maintenance Scripts
│   ├── views/                 # PostgreSQL Reporting Views
│   └── functions/             # Stored Procedures & Immutability Triggers
├── workers/                   # Background Async Workers (Out-of-Request Cycle)
│   ├── sync-worker/           # Outbox Batch Processor Worker
│   ├── email-worker/          # Automated Email Notification Worker
│   ├── cleanup-worker/        # Temp Storage & Session Purge Worker
│   ├── backup-worker/         # WAL Backup & Snapshot Worker
│   └── analytics-worker/      # Nightly BI Aggregation Worker
├── observability/             # Telemetry & Monitoring Subsystem
│   ├── logging/               # Winston Structured Logger
│   ├── metrics/               # Prometheus Metric Collectors
│   ├── tracing/               # OpenTelemetry Distributed Tracing
│   ├── health/                # Liveness & Readiness Probe Handlers
│   ├── alerts/                # PagerDuty & Slack Incident Alerts
│   └── dashboards/            # Pre-Built Grafana Dashboard Specs
└── src/
    ├── app.ts                 # Express Server Bootstrapper
    ├── server.ts              # HTTP Server Entry Point
    ├── config/                # Environment Variable Schemas
    ├── middleware/            # Auth, Tenant Scoping, Rate Limiters
    ├── shared/                # Core Shared Events & Utilities
    │   └── events/            # Domain Event Emitters & Bus
    │       ├── BillCreated.ts
    │       ├── BillRefunded.ts
    │       ├── InventoryAdjusted.ts
    │       ├── SubscriptionExpired.ts
    │       └── CustomerCreated.ts
    └── api/
        └── v1/                # API Version 1 Gateway
            └── modules/       # Domain-Driven Business Modules
                ├── auth/      # Auth, JWT, Firebase Middleware
                ├── business/  # Business Profile & Outlet Setup
                ├── feature-flags/ # Remote Feature Flags & A/B Testing
                ├── billing/   # Billing Domain Module
                │   ├── controller/
                │   ├── service/
                │   ├── repository/
                │   ├── dto/
                │   ├── entity/
                │   ├── mapper/
                │   ├── validator/
                │   ├── routes/
                │   ├── events/
                │   └── __tests__/
                ├── products/  # Catalog, Categories & Pricing Strategies
                ├── inventory/ # Stock, Batches, POs & GRNs
                ├── customers/ # Customer Profiles & Khata Ledger
                ├── expenses/  # Operational Expense Tracking
                ├── reports/   # Financial Reports & Tax Registers
                ├── subscription/ # SaaS Tiers, Grace Period & Payments
                ├── sync/      # Outbox Server Processor & Deltas
                └── ai/        # Menu OCR & Demand Forecasting Models
```

---

## Strict Dependency & Governance Rules

1. **Unidirectional Dependency Flow**: Upper layers depend on lower layers. Lower layers (`:core-database`, `shared/`) must NEVER import from upper feature or vertical modules.
2. **No Feature-to-Feature Coupling**: `:feature-billing` must NEVER import directly from `:feature-inventory`. Inter-feature communication executes via Domain Use Cases or Event Emitters.
3. **Architecture Decision Records (ADRs)**: Any modification to core repository structure requires filing an ADR proposal under `docs/adr/`.

---

## Frozen Architecture Sign-Off
- **Status**: FROZEN (v1.0)
- **Tag**: `v1.0-architecture-freeze`
