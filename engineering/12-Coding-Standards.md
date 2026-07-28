# 12 - Enterprise Coding Standards & Style Guide [v1.0 FROZEN ARCHITECTURE]

## Purpose
This document defines the official coding standards, architectural principles, naming conventions, error handling rules, logging requirements, dependency injection patterns, and static code analysis standards for the **Optix** monorepo across Kotlin, TypeScript, Jetpack Compose, Room, Fastify, and Prisma.

---

## Goals
1. Guarantee high code readability, maintainability, and consistency across a multi-developer engineering team.
2. Eliminate common programming pitfalls (e.g., implicit `any` types in TypeScript, main-thread blocking in Kotlin, unhandled async promise rejections).
3. Automate code formatting enforcement using `ktlint`, `detekt`, and `ESLint` pre-commit hooks.

---

## Language & Technology Coding Standards

### 1. General Naming Conventions

| Entity | Language | Convention | Example |
| :--- | :--- | :--- | :--- |
| **Classes & Interfaces** | Kotlin / TypeScript | PascalCase | `CalculateCartUseCase`, `BillingService` |
| **Methods & Functions** | Kotlin / TypeScript | camelCase | `calculateCartTotal()`, `findProductByBarcode()` |
| **Variables & Properties** | Kotlin / TypeScript | camelCase | `grossTotal`, `currentStock` |
| **Constants** | Kotlin / TypeScript | UPPER_SNAKE_CASE | `MAX_RETRY_COUNT`, `DEFAULT_PAGE_SIZE` |
| **Directories** | All | kebab-case | `feature-billing`, `shared-types` |
| **Database Tables** | PostgreSQL | plural_snake_case | `bills`, `bill_items`, `inventory_batches` |

---

### 2. Kotlin & Jetpack Compose Standards
- **Strict Coroutine Context Scoping**: Database reads/writes, bitmap manipulations, and network requests must explicitly specify `withContext(Dispatchers.IO)`. Never execute I/O on `Dispatchers.Main`.
- **Stateless Composables**: Keep Compose UI components strictly stateless by hoisting UI state up to ViewModel wrappers.
- **State Immutability**: UI State classes must use `@Immutable` or `@Stable` annotations to prevent unnecessary Compose recomposition loops.
- **Click Debouncing**: Wrap interactive click modifiers in debounced handlers (300ms) to prevent double-tap glitches during rush hours.

---

### 3. TypeScript & Node.js/Fastify Standards
- **Zero `any` Types**: The use of `any` is strictly banned. Enable `noImplicitAny` and `strictNullChecks` in `tsconfig.json`.
- **Zod Schema Compilation**: Every HTTP request body must be validated using a Zod schema compiled into Fastify's JSON Schema validator compiler.
- **Async Error Boundaries**: Wrap async route handlers inside Fastify error boundaries. Unhandled promise rejections are forbidden.
- **Prisma Repository Isolation**: Raw `prisma.product.findMany()` calls are restricted to repository classes; controllers and services must interact with repository abstractions.

---

### 4. Logging & Observability Standards
- **Raw Print Statements Banned**: `console.log()`, `println()`, and `printStackTrace()` are strictly prohibited in production code.
- **Structured JSON Logging**:
  - Android: Use `Timber` logger with explicit tag wrappers.
  - Backend: Use `Winston` structured JSON logger requiring `correlation_id`, `tenant_id`, `level`, and `timestamp`.

---

## Automated Static Code Analysis Tools

1. **Android**:
   - `ktlint`: Enforces official Kotlin code formatting styles.
   - `detekt`: Analyzes code complexity, long parameter lists, and potential bug hazards.
2. **Backend**:
   - `ESLint`: Configured with `@typescript-eslint/recommended` rules.
   - `Prettier`: Formats TypeScript files automatically on git pre-commit hook (`husky`).

---

## Frozen Architecture Sign-Off
- **Status**: FROZEN (v1.0)
- **Tag**: `v1.0-coding-standards-freeze`
