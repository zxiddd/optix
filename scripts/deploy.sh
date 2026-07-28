#!/bin/bash
# Optix Staging & Production Deployment Script with Automated Rollback
set -e

IMAGE_TAG="${1:-latest}"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(dirname "$SCRIPT_DIR")"

echo "=========================================================="
echo "      OPTIX PRODUCTION AUTOMATED DEPLOYMENT RUNNER        "
echo "=========================================================="

cd "$REPO_ROOT"

echo "[1/4] Starting Docker Container Infrastructure..."
if [ -f "infra/docker/docker-compose.staging.yml" ]; then
    docker compose -f infra/docker/docker-compose.staging.yml up -d --remove-orphans
else
    docker compose -f infra/docker/docker-compose.production.yml up -d --remove-orphans
fi

echo "[2/4] Executing Database Schema Migrations & Optional Seeding..."
docker exec optix-backend-staging npx prisma migrate deploy --schema=apps/backend/prisma/schema.prisma || true

docker exec optix-backend-staging sh -c '
  if [ -f "apps/backend/database/seeds/system-required-seeds.ts" ]; then
    npx tsx apps/backend/database/seeds/system-required-seeds.ts
  elif [ -f "database/seeds/system-required-seeds.ts" ]; then
    npx tsx database/seeds/system-required-seeds.ts
  else
    echo "Seed file not found. Skipping optional database seeding."
  fi
' || true

echo "[3/4] Executing Post-Deployment Health Check Probe..."
if bash "$SCRIPT_DIR/healthcheck.sh" "http://localhost:3000/health"; then
    echo "=========================================================="
    echo "    DEPLOYMENT SUCCESSFUL! API IS HEALTHY AND LIVE.      "
    echo "=========================================================="
    exit 0
else
    echo "=========================================================="
    echo "    HEALTH CHECK FAILED! TRIGGERING AUTOMATIC ROLLBACK... "
    echo "=========================================================="
    bash "$SCRIPT_DIR/rollback.sh" "$IMAGE_TAG"
    exit 1
fi
