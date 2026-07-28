# Optix - Enterprise Offline-First POS & Modular Business Operating System

Optix is an offline-first, high-performance native Android Point of Sale (POS) platform and SaaS business operating system for local merchants (restaurants, bakeries, grocery stores, pharmacies, butcher shops, salons).

---

## Technical Architecture Overview

- **Android POS Client (`apps/android/`)**: Native Kotlin 1.9+, Jetpack Compose Material 3, Clean Architecture + MVVM, Hilt Dependency Injection, Encrypted Room SQLite with SQLCipher (AES-256), WorkManager outbox sync engine, ESC/POS thermal printer hardware drivers.
- **Cloud Backend API (`apps/backend/`)**: Node.js 20 LTS, TypeScript 5+, Fastify HTTP engine, Prisma ORM, PostgreSQL 16, Redis cache & BullMQ background worker queue, Winston structured logging.
- **Web Applications**: Super Admin Portal (`apps/admin/`), Merchant Dashboard (`apps/merchant-dashboard/`), Public Website (`apps/website/`).
- **Shared Packages (`packages/`)**: `@optix/shared-types`, `@optix/sync-sdk`, `@optix/escpos-sdk`, `@optix/api-client`.

---

## Quick Start Developer Onboarding (< 15 Minutes)

### Prerequisites
- Node.js 20 LTS+ and npm 10+
- JDK 17+
- Android Studio Hedgehog (2023.1.1) or newer
- Docker Desktop (for local PostgreSQL 16 & Redis 7.2)

### 1. Environment Setup & Monorepo Bootstrap
```bash
# Clone the repository
git clone <repository_url>
cd optix

# Copy environment variable template
cp .env.example .env

# Install monorepo dependencies
npm install

# Start local infrastructure (PostgreSQL 16 & Redis 7.2 containers)
npm run docker:up
```

### 2. Backend Startup & Database Migration
```bash
# Run database schema migrations
npm run db:migrate

# Seed baseline system roles and metadata
npm run db:seed

# Start Fastify backend in watch mode
npm run dev:backend
```

Verify backend health at: `http://localhost:3000/health` (Returns `HTTP 200 OK`).

### 3. Running Native Android POS App
1. Open Android Studio.
2. Select **Open an Existing Project** and navigate to `apps/android/`.
3. Wait for Gradle Sync to complete automatically.
4. Select an Android Emulator (API Level 29+ / Android 10+) or physical device.
5. Click **Run** (`Shift + F10`). The app launches to the `Optix POS Core Bootstrap Ready` screen.

---

## Workspace Build & Test Scripts

| Script Command | Description |
| :--- | :--- |
| `npm run dev:backend` | Starts Fastify backend API in local development watch mode |
| `npm run build:backend` | Compiles backend TypeScript code into production JS (`dist/`) |
| `npm run test:backend` | Executes Jest backend integration and unit test suite |
| `npm run db:migrate` | Runs Prisma database migrations against local PostgreSQL |
| `npm run docker:up` | Starts local PostgreSQL 16 & Redis 7.2 Docker containers |
| `npm run docker:down` | Stops local Docker containers |

---

## Monorepo Directory Structure

```
optix/
├── apps/                      # Runnable Applications
│   ├── android/               # Native Android Jetpack Compose POS App
│   ├── backend/               # Fastify / TypeScript Modular Monolith API
│   ├── admin/                 # Super Admin Web Portal
│   ├── merchant-dashboard/    # Store Owner Web Portal
│   └── website/               # Public Website
├── packages/                  # Shared Reusable Monorepo Packages
│   └── shared-types/          # Shared TypeScript Data Contracts & Interfaces
├── infra/                     # Infrastructure & Docker Configurations
│   └── docker/docker-compose.yml
├── docs/                      # Technical & Product Specification Suite
│   ├── product/               # Product Requirements (00-Vision to 24-AI-Module)
│   ├── engineering/           # CTO Execution Specs (01-13)
│   └── adr/                   # Architecture Decision Records
├── ROADMAP.md                 # Single Source of Truth for Build Milestones
└── README.md                  # Developer Onboarding Guide
```

---

## License
MIT License - Copyright (c) 2026 Optix POS Platform.
