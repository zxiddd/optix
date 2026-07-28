#!/bin/bash
# Optix VPS Deployment & Verification Audit Script
# Run this script on the VPS to build, deploy, migrate, and verify https://api.optixapp.in/health

set -e

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$REPO_ROOT"

echo "=========================================================="
echo "   OPTIX POS VPS DEPLOYMENT & HEALTH VERIFICATION AUDIT   "
echo "=========================================================="

echo "[Task 1/15] Pulling latest repository code..."
git pull origin main

echo "[Task 2/15] Building Docker images locally without cache..."
docker compose -f infra/docker/docker-compose.staging.yml build --no-cache

echo "[Task 3/15] Starting Docker Compose staging stack..."
docker compose -f infra/docker/docker-compose.staging.yml up -d --remove-orphans

echo "[Task 4/15] Waiting 10s for PostgreSQL and Redis health checks..."
sleep 10

echo "[Task 5/15] Executing Prisma database migrations..."
docker exec optix-backend-staging npx prisma migrate deploy || true

echo "[Task 6/15] Executing database seeding..."
docker exec optix-backend-staging npx tsx database/seeds/system-required-seeds.ts || true

echo "[Task 7/15] Validating Nginx configuration..."
sudo cp infra/nginx/rate_limit.conf /etc/nginx/conf.d/rate_limit.conf
sudo cp infra/nginx/optix-domain.conf /etc/nginx/sites-available/optix-domain.conf
sudo ln -sf /etc/nginx/sites-available/optix-domain.conf /etc/nginx/sites-enabled/default
sudo nginx -t

echo "[Task 8/15] Reloading Nginx service..."
sudo systemctl reload nginx

echo "=========================================================="
echo "                DEPLOYMENT VERIFICATION REPORT            "
echo "=========================================================="

echo "--- 1. Docker Containers Status (docker compose ps) ---"
docker compose -f infra/docker/docker-compose.staging.yml ps

echo -e "\n--- 2. Backend Container Logs (Last 20 lines) ---"
docker logs --tail 20 optix-backend-staging

echo -e "\n--- 3. Local Backend Container Health (http://localhost:3000/health) ---"
curl -i http://localhost:3000/health || echo "FAILED"

echo -e "\n--- 4. Public Domain HTTPS Health (https://api.optixapp.in/health) ---"
curl -i https://api.optixapp.in/health || echo "FAILED"

echo "=========================================================="
echo "    AUDIT COMPLETED: VERIFY HTTP 200 STATUS ABOVE         "
echo "=========================================================="
