#!/bin/bash
# Master VPS Automated Provisioning Script for Optix POS (Ubuntu 24.04 LTS) - Idempotent
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "=========================================================="
echo "      OPTIX POS VPS AUTOMATED PROVISIONING BOOTSTRAP       "
echo "=========================================================="

echo "[1/8] Updating System Packages & Configuring Swap..."
sudo apt update -y

# Create 2GB Swap file idempotently
if [ ! -f /swapfile ]; then
    echo "Creating 2GB swap file..."
    sudo fallocate -l 2G /swapfile
    sudo chmod 600 /swapfile
    sudo mkswap /swapfile
    sudo swapon /swapfile
    if ! grep -q '/swapfile' /etc/fstab; then
        echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab
    fi
else
    echo "Swap file /swapfile already exists. Skipping."
fi

echo "[2/8] Running User & Permission Setup..."
bash "$SCRIPT_DIR/setup-user.sh"

echo "[3/8] Installing Docker & Docker Compose..."
bash "$SCRIPT_DIR/install-docker.sh"

echo "[4/8] Installing & Configuring Nginx Reverse Proxy..."
bash "$SCRIPT_DIR/install-nginx.sh"

echo "[5/8] Configuring UFW Firewall & Fail2Ban..."
bash "$SCRIPT_DIR/configure-firewall.sh"

echo "[6/8] Configuring Docker Networks & Log Rotation..."
bash "$SCRIPT_DIR/setup-docker.sh"

echo "[7/8] Setting Up Backup & Monitoring Tasks..."
bash "$SCRIPT_DIR/install-monitoring.sh"

echo "=========================================================="
echo "    VPS PROVISIONING COMPLETED SUCCESSFULLY!             "
echo "=========================================================="
