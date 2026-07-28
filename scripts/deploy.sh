#!/bin/bash
# Optix Staging & Production Deployment Script with Automated Rollback
# Usage: ./scripts/deploy.sh <image_tag>

set -e

IMAGE_TAG="${1:-latest}"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(dirname "$SCRIPT_DIR")"

echo "=========================================================="
echo "      OPTIX PRODUCTION AUTOMATED DEPLOYMENT RUNNER        "
echo "=========================================================="

# Save current container image tag for rollback
PREVIOUS_IMAGE=$(docker inspect --format='{{.Config.Image}}' optix-backend-prod-1 2>/dev/null || echo "")

echo "[1/4] Pulling Target Docker Image: $IMAGE_TAG..."
docker pull "$IMAGE_TAG" || true

echo "[2/4] Executing Database Migrations..."
docker run --rm --net=host -e DATABASE_URL="$DATABASE_URL" "$IMAGE_TAG" npx prisma migrate deploy || true

echo "[3/4] Restarting Container Cluster..."
cd "$REPO_ROOT"
docker compose -f infra/docker/docker-compose.production.yml up -d --remove-orphans

echo "[4/4] Executing Post-Deployment Health Check Probe..."
if bash "$SCRIPT_DIR/healthcheck.sh" "http://localhost:3000/health"; then
    echo "=========================================================="
    echo "    DEPLOYMENT SUCCESSFUL! API IS HEALTHY AND LIVE.      "
    echo "=========================================================="
    exit 0
else
    echo "=========================================================="
    echo "    HEALTH CHECK FAILED! TRIGGERING AUTOMATIC ROLLBACK... "
    echo "=========================================================="
    if [ -n "$PREVIOUS_IMAGE" ]; then
        bash "$SCRIPT_DIR/rollback.sh" "$PREVIOUS_IMAGE"
    fi
    exit 1
fi
