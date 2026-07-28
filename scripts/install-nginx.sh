#!/bin/bash
# Install & Configure Nginx Reverse Proxy & Let's Encrypt SSL for api.optixapp.in - Idempotent
set -e

DOMAIN="api.optixapp.in"
ADMIN_EMAIL="admin@optixapp.in"

if ! command -v nginx >/dev/null 2>&1; then
    echo "=== Installing Nginx & Certbot ==="
    sudo apt update
    sudo apt install -y nginx certbot python3-certbot-nginx
else
    echo "Nginx is already installed ($(nginx -v 2>&1))."
fi

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(dirname "$SCRIPT_DIR")"

echo "=== Deploying Global Rate Limit Configuration into http {} context ==="
sudo cp "$REPO_ROOT/infra/nginx/rate_limit.conf" /etc/nginx/conf.d/rate_limit.conf

# Check if SSL certificate already exists
if [ -f "/etc/letsencrypt/live/$DOMAIN/fullchain.pem" ]; then
    echo "=== SSL Certificate Found for $DOMAIN. Deploying Full HTTPS Configuration ==="
    sudo cp "$REPO_ROOT/infra/nginx/optix-domain.conf" /etc/nginx/sites-available/optix-domain.conf
    sudo ln -sf /etc/nginx/sites-available/optix-domain.conf /etc/nginx/sites-enabled/default
else
    echo "=== Initializing HTTP Bootstrap Configuration for Certbot Validation ==="
    sudo mkdir -p /var/www/html
    sudo cp "$REPO_ROOT/infra/nginx/optix-domain-http-bootstrap.conf" /etc/nginx/sites-available/optix-domain.conf
    sudo ln -sf /etc/nginx/sites-available/optix-domain.conf /etc/nginx/sites-enabled/default

    echo "=== Validating HTTP Nginx Configuration Syntax ==="
    sudo nginx -t
    sudo systemctl reload nginx

    echo "=== Obtaining Let's Encrypt SSL Certificate for $DOMAIN ==="
    sudo certbot --nginx -d "$DOMAIN" -d "optixapp.in" --non-interactive --agree-tos -m "$ADMIN_EMAIL" --redirect || true

    if [ -f "/etc/letsencrypt/live/$DOMAIN/fullchain.pem" ]; then
        echo "=== Certificate Obtained Successfully. Switching to Full HTTPS Configuration ==="
        sudo cp "$REPO_ROOT/infra/nginx/optix-domain.conf" /etc/nginx/sites-available/optix-domain.conf
        sudo systemctl reload nginx
    else
        echo "WARNING: Let's Encrypt SSL issuance requires DNS propagation to complete. Running HTTP mode."
    fi
fi

echo "=== Validating Final Nginx Configuration Syntax ==="
sudo nginx -t

echo "=== Enabling & Reloading Nginx Service ==="
sudo systemctl enable nginx
sudo systemctl reload nginx
