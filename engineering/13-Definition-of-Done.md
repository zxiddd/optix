# 13 - Global Definition of Done (DoD) & Acceptance Quality Standards [v1.0 FROZEN ARCHITECTURE]

## Purpose
This document defines the official, non-negotiable **Definition of Done (DoD)** criteria for features, bug fixes, database migrations, pull requests, and milestone releases across the **Optix** platform.

---

## Goals
1. Establish a single source of quality truth for product managers, software engineers, and QA testers.
2. Prevent incomplete, poorly tested, or un-documented code from entering the `develop` or `main` branches.
3. Guarantee that every feature meets performance SLAs, offline-first behavior rules, security standards, and accessibility criteria before release.

---

## Master Definition of Done (DoD) Checklists

### 1. Feature Level Definition of Done
A user story or feature issue is considered **DONE** only when:
- [x] **Functional Compliance**: Implements 100% of functional requirements and user flows specified in `docs/product/`.
- [x] **Offline-First Resilience**: Verified to operate 100% disconnected from network without freezing UI or losing transaction data.
- [x] **Automated Test Coverage**: Unit test line coverage exceeds **85%**; integration and Compose UI tests pass cleanly.
- [x] **Performance SLA**: Cart calculations execute in <50ms; API response time <150ms p99.
- [x] **Security & Tenant Scoping**: Multi-tenant `business_id` scoping verified; OWASP security guidelines met.
- [x] **Design Tokens & Accessibility**: UI utilizes standard Compose design tokens with minimum 48dp touch targets.
- [x] **Code Quality**: Passes `ktlint`, `detekt`, and `ESLint` checks with zero warnings or errors.
- [x] **Peer Review**: Approved by at least **2 senior engineers** on GitHub Pull Request.

---

### 2. Bug Fix Definition of Done
A bug fix issue is considered **DONE** only when:
- [x] **Root Cause Verified**: Root cause documented in issue ticket with empirical traceback or log line.
- [x] **Regression Test Added**: A new automated unit or integration test is added specifically asserting against the bug scenario.
- [x] **Zero Side Effects**: Regression test suite passes with zero collateral breakage.

---

### 3. Database Migration Definition of Done
A database schema migration is considered **DONE** only when:
- [x] **Backward Compatibility**: Schema modification tested against older active API versions without breaking runtime contracts.
- [x] **Rollback Verification**: Automated rollback script (`prisma migrate diff` / down script) tested successfully on staging DB.
- [x] **Row-Level Security (RLS)**: Row-Level Security policies applied to new multi-tenant tables.
- [x] **Index Performance**: Composite B-Tree indexes verified against EXPLAIN ANALYZE query execution plans.

---

### 4. Milestone Release Definition of Done
A major project build milestone (Milestones 1 - 10) is considered **DONE** only when:
- [x] **100% Acceptance Criteria Passed**: All milestone feature criteria verified on staging environments.
- [x] **End-to-End User Journey Verified**: Passed automated Playwright / Espresso E2E test runs.
- [x] **Staged Beta Deployment**: Deployed cleanly to Closed Beta track and verified across test POS devices.
- [x] **Documentation Updated**: Architecture Decision Records (ADRs) and release notes updated under `docs/`.

---

## Sign-Off Governance & Release Process

```
[ Developer Opens PR ]
         |
         v
[ GitHub Actions CI Automated Check ] ---> Must pass 100% (Lint, Tests, Security)
         |
         v
[ Peer Review by 2 Senior Engineers ] ---> Must receive 2 explicit Approvals
         |
         v
[ Merge to develop / release branch ] ---> Automated Staging Deployment
         |
         v
[ CTO / Lead Architect Sign-Off ] --------> Production Deploy to main branch
```

---

## Frozen Architecture Sign-Off
- **Status**: FROZEN (v1.0)
- **Tag**: `v1.0-definition-of-done-freeze`
