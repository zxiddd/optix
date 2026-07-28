#!/bin/bash
# Install & Configure Nginx Reverse Proxy for Optix VPS (200.141.7.8) - Idempotent
set -e

if ! command -v nginx >/dev/null 2>&1; then
    echo "=== Installing Nginx & Certbot ==="
    sudo apt update
    sudo apt install -y nginx certbot python3-certbot-nginx
else
    echo "Nginx is already installed ($(nginx -v 2>&1)). Skipping package installation."
fi

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(dirname "$SCRIPT_DIR")"

echo "=== Deploying Global Rate Limit Configuration into http {} context ==="
sudo cp "$REPO_ROOT/infra/nginx/rate_limit.conf" /etc/nginx/conf.d/rate_limit.conf

echo "=== Deploying Nginx Server Configuration for VPS IP (200.141.7.8) ==="
sudo cp "$REPO_ROOT/infra/nginx/optix-vps-ip.conf" /etc/nginx/sites-available/optix-vps-ip.conf
sudo ln -sf /etc/nginx/sites-available/optix-vps-ip.conf /etc/nginx/sites-enabled/default

echo "=== Validating Nginx Configuration Syntax ==="
sudo nginx -t

echo "=== Reloading Nginx Service ==="
sudo systemctl enable nginx
sudo systemctl reload nginx
