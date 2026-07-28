#!/bin/bash
# Optix Emergency Rollback Script
# Usage: ./scripts/rollback.sh <rollback_image_tag>

set -e

ROLLBACK_IMAGE="${1:-ghcr.io/zxiddd/optix/backend:latest}"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(dirname "$SCRIPT_DIR")"

echo "=========================================================="
echo "    EMERGENCY ROLLBACK INITIATED: $ROLLBACK_IMAGE         "
echo "=========================================================="

cd "$REPO_ROOT"
docker compose -f infra/docker/docker-compose.production.yml down || true

echo "Restoring previous container image..."
docker pull "$ROLLBACK_IMAGE"
docker compose -f infra/docker/docker-compose.production.yml up -d

echo "Verifying Rollback System Health..."
bash "$SCRIPT_DIR/healthcheck.sh" "http://localhost:3000/health"

echo "=========================================================="
echo "    ROLLBACK COMPLETED SUCCESSFULLY! RESTORED STABLE API.  "
echo "=========================================================="
