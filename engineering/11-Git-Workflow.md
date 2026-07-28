# 11 - Git Flow Branching Strategy & PR Code Review Standards [v1.0 FROZEN ARCHITECTURE]

## Purpose
This document defines the Git branching model (Git Flow), Conventional Commit specifications, Pull Request (PR) review policies, Semantic Versioning (SemVer) rules, and release tagging protocols for the **Optix Monorepo**.

---

## Goals
1. Establish a disciplined, deterministic version control workflow preventing breaking changes from reaching production.
2. Standardize commit messages using **Conventional Commits** to enable automated changelog generation and SemVer release tagging.
3. Require mandatory peer code review and automated CI validation checks on every Pull Request.

---

## Git Flow Branching Model

```
  main (Production Branch - Tagged Releases v1.0.0, v1.1.0)
   ^
   | (Release PR Merge)
  release/v1.1.0 (Release Preparation Branch)
   ^
   | (PR Merge with squash)
  develop (Primary Integration Branch)
   ^
   +--- feature/billing-split-payment
   +--- feature/medical-generic-lookup
   +--- hotfix/printer-bluetooth-disconnect
```

### Branch Types & Naming Rules
- `main`: Production-ready code only. Direct commits strictly locked.
- `develop`: Primary integration branch for active sprint development.
- `feature/<feature-name>`: Short-lived feature branches created off `develop` (e.g., `feature/kds-routing`).
- `bugfix/<bug-name>`: Non-critical bugfix branches created off `develop`.
- `hotfix/<patch-name>`: Emergency production patch branches created off `main`.
- `release/<version>`: Release candidate preparation branch (e.g., `release/v1.2.0`).

---

## Conventional Commit Standard

Commit messages must follow the format: `<type>(<scope>): <short summary>`

```
feat(billing): add split payment support for Cash + Card tenders
fix(sync): resolve race condition in outbox worker payload serialization
docs(api): update RFC 7807 error schema examples
test(cart): add unit test for weighable product Banker's Rounding
refactor(database): convert primary keys to UUIDv7 format
```

- **Types**: `feat`, `fix`, `docs`, `style`, `refactor`, `perf`, `test`, `chore`, `ci`, `build`.

---

## Pull Request (PR) & Code Review Guidelines

1. **PR Template Requirements**:
   - Link to issue ticket or feature specification document.
   - Summary of changes and technical implementation rationale.
   - Automated test output screenshot or verification evidence.
2. **Review & Merge Checklist**:
   - [x] Minimum **2 peer engineering approvals** required.
   - [x] All GitHub Actions CI checks (`ci-android.yml`, `ci-backend.yml`) must pass cleanly.
   - [x] Code coverage must not decrease.
   - [x] PRs merged into `develop` using **Squash and Merge** to maintain a clean linear commit history.

---

## Semantic Versioning (SemVer 2.0.0)

Versions formatted as `MAJOR.MINOR.PATCH` (e.g., `v1.2.4`):
- **MAJOR**: Breaking changes or core database schema overhauls.
- **MINOR**: New backward-compatible business modules or feature additions.
- **PATCH**: Backward-compatible bug fixes and security hotfixes.

---

## Step-by-Step Implementation Sequence

1. **Step 1**: Configure GitHub repository branch protection rules on `main` and `develop` branches.
2. **Step 2**: Install `commitlint` and `husky` git commit hooks enforcing Conventional Commits locally.
3. **Step 3**: Configure GitHub Actions CI workflow checking commit formatting and test execution on every PR push.

---

## Frozen Architecture Sign-Off
- **Status**: FROZEN (v1.0)
- **Tag**: `v1.0-git-workflow-freeze`
