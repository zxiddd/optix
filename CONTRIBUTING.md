# Contributing to Optix POS Platform

Thank you for contributing to Optix! To maintain high code quality and architectural integrity, please adhere to the following standards:

---

## Git Workflow & Branching Rules
- **`main`**: Production code only. Direct pushes strictly locked.
- **`develop`**: Primary integration branch.
- **`feature/<name>`**: Feature branches created off `develop`.
- **`bugfix/<name>`**: Bugfix branches created off `develop`.

---

## Commit Message Conventions
Commit messages must follow the [Conventional Commits](https://www.conventionalcommits.org/) specification:

```
feat(billing): add split payment tender support
fix(sync): resolve outbox event deduplication logic
docs(readme): update local developer quickstart guide
test(cart): add Banker's Rounding unit tests
```

Types: `feat`, `fix`, `docs`, `style`, `refactor`, `perf`, `test`, `chore`.

---

## Pull Request (PR) Checklist
Before requesting review:
- [ ] Code compiles without errors or warnings.
- [ ] All unit and integration tests pass cleanly.
- [ ] Code formatted according to `ktlint` (Android) and `ESLint` (Backend).
- [ ] Minimum 2 peer senior engineering approvals required before merging into `develop`.
