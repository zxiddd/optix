#!/bin/bash
# Automated Remote VPS Setup Initiator for Optix POS (api.optixapp.in)
# Usage: bash scripts/vps-remote-setup.sh <vps_ip_or_domain> <user> <password>

VPS_TARGET="${1:-api.optixapp.in}"
VPS_USER="${2:-root}"
VPS_PASS="${3:-Zaiduddin@787}"

echo "=========================================================="
echo "    REMOTE VPS AUTOMATED SETUP INITIATOR ($VPS_TARGET)    "
echo "=========================================================="

echo "[1/3] Connecting to VPS $VPS_TARGET and updating Optix repository..."

ssh -o StrictHostKeyChecking=no "$VPS_USER@$VPS_TARGET" << 'EOF'
set -e
echo "=== Cloning / Updating Optix Repository ==="
if [ ! -d /opt/optix ]; then
    mkdir -p /opt/optix
    git clone https://github.com/zxiddd/optix.git /opt/optix
else
    cd /opt/optix
    git pull origin main
fi

echo "=== Executing VPS Bootstrap & Nginx SSL Setup Script ==="
cd /opt/optix
chmod +x scripts/*.sh
bash scripts/bootstrap-vps.sh

echo "=== Deploying Staging Environment (api.optixapp.in) ==="
cp .env.staging .env
docker compose -f infra/docker/docker-compose.staging.yml up -d

echo "=== Running Health Check Probe ==="
bash scripts/healthcheck.sh http://localhost:3000/health
EOF

echo "=========================================================="
echo "    REMOTE VPS SETUP & DEPLOYMENT COMPLETED SUCCESSFULLY!  "
echo "=========================================================="
