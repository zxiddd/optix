#!/bin/bash
# Automated Remote VPS Setup Initiator for Optix POS
# Usage: bash scripts/vps-remote-setup.sh <vps_ip> <user> <password>

VPS_IP="${1:-200.141.7.8}"
VPS_USER="${2:-root}"
VPS_PASS="${3:-Zaiduddin@787}"

echo "=========================================================="
echo "    REMOTE VPS AUTOMATED SETUP INITIATOR ($VPS_IP)        "
echo "=========================================================="

echo "[1/3] Connecting to VPS $VPS_IP and cloning Optix repository..."

ssh -o StrictHostKeyChecking=no "$VPS_USER@$VPS_IP" << 'EOF'
set -e
echo "=== Removing old optix directory if exists ==="
rm -rf /opt/optix

echo "=== Cloning Optix Repository ==="
mkdir -p /opt/optix
git clone https://github.com/zxiddd/optix.git /opt/optix

echo "=== Executing VPS Bootstrap Script ==="
cd /opt/optix
chmod +x scripts/*.sh
bash scripts/bootstrap-vps.sh

echo "=== Deploying Staging Environment (200.141.7.8:3000) ==="
cp .env.staging .env
docker compose -f infra/docker/docker-compose.staging.yml up -d

echo "=== Running Health Check Probe ==="
bash scripts/healthcheck.sh http://localhost:3000/health
EOF

echo "=========================================================="
echo "    REMOTE VPS SETUP & DEPLOYMENT COMPLETED SUCCESSFULLY!  "
echo "=========================================================="
