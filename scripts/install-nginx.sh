#!/bin/bash
# Install & Configure Nginx Reverse Proxy for Optix VPS (200.141.7.8)
set -e

echo "=== Installing Nginx & Certbot ==="
sudo apt install -y nginx certbot python3-certbot-nginx

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(dirname "$SCRIPT_DIR")"

echo "=== Deploying Nginx Configuration for VPS IP (200.141.7.8) ==="
sudo cp "$REPO_ROOT/infra/nginx/optix-vps-ip.conf" /etc/nginx/sites-available/optix-vps-ip.conf
sudo ln -sf /etc/nginx/sites-available/optix-vps-ip.conf /etc/nginx/sites-enabled/default

echo "=== Testing Nginx Configuration Syntax ==="
sudo nginx -t

echo "=== Reloading Nginx Service ==="
sudo systemctl enable nginx
sudo systemctl reload nginx
