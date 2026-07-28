# 10 - GitHub Secrets & Environment Variables Security Guide

## Purpose
This document defines every GitHub Action Secret, environment variable, and credential requirement for configuring the automated CI/CD deployment pipeline for **Optix POS**.

---

## Required GitHub Repository Secrets

Configure the following secrets in your GitHub repository under **Settings > Secrets and variables > Actions**:

| Secret Name | Description | Value for Your Setup |
| :--- | :--- | :--- |
| `VPS_HOST` | Target Ubuntu 24.04 LTS VPS IP Address | `200.141.7.8` |
| `VPS_USER` | Deployment SSH User | `root` |
| `VPS_PASSWORD` | VPS SSH Authentication Password | `Zaiduddin@787` |
| `STAGING_DATABASE_URL` | Staging PostgreSQL connection string | `postgresql://optix_staging_admin:pass@localhost:5432/optix_staging_db` |
| `STAGING_REDIS_URL` | Staging Redis connection string | `redis://localhost:6379` |
| `STAGING_JWT_SECRET` | 32-byte secret key for signing staging JWTs | `optix_staging_jwt_secret_9022_key_32bytes` |
| `DATABASE_URL` | Production PostgreSQL connection string | `postgresql://optix_prod_admin:pass@postgres-prod:5432/optix_production_db` |
| `REDIS_URL` | Production Redis connection string | `redis://redis-prod:6379` |
| `JWT_SECRET` | Production 32-byte secret key for JWTs | `SECURE_PROD_JWT_SECRET_32_BYTES_MINIMUM` |
| `FIREBASE_PROJECT_ID` | Firebase Project Identifier | `optix-pos-prod` |
| `FIREBASE_CLIENT_EMAIL`| Firebase Service Account Email | `firebase-adminsdk@optix-pos-prod.iam.gserviceaccount.com` |
| `FIREBASE_PRIVATE_KEY` | Firebase Service Account RSA Key | `"-----BEGIN PRIVATE KEY-----\n..."` |

---

## How to Setup Secrets via GitHub CLI (`gh`)

```bash
gh secret set VPS_HOST --body "200.141.7.8"
gh secret set VPS_USER --body "root"
gh secret set VPS_PASSWORD --body "Zaiduddin@787"
```
