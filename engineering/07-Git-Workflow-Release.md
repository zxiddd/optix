# 07 - Git Branching, CI/CD Pipeline & Play Store Release Strategy

## Purpose
This document defines the Git branching strategy (Git Flow), Semantic Versioning (SemVer), GitHub Actions CI/CD automation pipelines, database migration release procedures, and Google Play Store release tracks for the **Optix** platform.

---

## 1. Git Branching Model (Git Flow Specification)

```
  main (Production Releases - v1.0.0, v1.1.0)
   ^
   | (Release Branch Merge)
  release/v1.1.0
   ^
   | (PR Merge)
  develop (Integration Branch)
   ^
   +--- feature/kds-module
   +--- feature/medical-batch-expiry
   +--- hotfix/printer-bluetooth-reconnect
```

- **`main`**: Production code. Every commit tagged with SemVer version (`v1.0.0`, `v1.1.0`).
- **`develop`**: Primary integration branch for active sprint code.
- **`feature/*`**: Isolated feature branches created off `develop`.
- **`hotfix/*`**: Emergency production bugfix branches created off `main`.

---

## 2. GitHub Actions CI/CD Pipeline (`.github/workflows/ci.yml`)

```yaml
name: Optix Continuous Integration & Deployment Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ develop ]

jobs:
  android-build-test:
    name: Build & Test Android POS Client
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
      - name: Run Android Unit Tests
        run: ./gradlew testDebugUnitTest
      - name: Build Android Release APK & Bundle
        run: ./gradlew assembleRelease bundleRelease

  backend-build-test:
    name: Build & Test Node.js API Backend
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Setup Node.js 20
        uses: actions/setup-node@v4
        with:
          node-version: '20'
      - name: Install Dependencies
        run: npm ci
      - name: Run ESLint & TypeScript Compiler
        run: npm run lint && npx tsc --noEmit
      - name: Run Jest Integration Tests
        run: npm test
```

---

## 3. Google Play Store Release Track Hierarchy

1. **Internal Test Track**: Automated nightly builds pushed to internal engineering team for rapid feature validation.
2. **Closed Beta Track**: Pushed to 50 pilot merchant stores for real-world counter testing.
3. **Staged Production Rollout**:
   - Day 1: 10% of production stores.
   - Day 2: 25% of production stores.
   - Day 3: 50% of production stores.
   - Day 5: 100% full production release.
