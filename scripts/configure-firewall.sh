#!/bin/bash
# Configure UFW Firewall & Fail2Ban Protection - Idempotent
set -e

if ! command -v ufw >/dev/null 2>&1 || ! command -v fail2ban-client >/dev/null 2>&1; then
    echo "=== Installing UFW & Fail2Ban ==="
    sudo apt update
    sudo apt install -y ufw fail2ban
fi

echo "=== Configuring UFW Firewall Rules ==="
sudo ufw default deny incoming
sudo ufw default allow outgoing
sudo ufw allow 22/tcp comment 'SSH'
sudo ufw allow 80/tcp comment 'HTTP Nginx'
sudo ufw allow 443/tcp comment 'HTTPS Nginx'
sudo ufw --force enable

echo "=== Configuring Fail2Ban Protection ==="
sudo systemctl enable fail2ban
sudo systemctl restart fail2ban

echo "UFW Firewall Status:"
sudo ufw status verbose
