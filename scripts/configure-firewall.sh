#!/bin/bash
# Configure UFW Firewall & Fail2Ban Protection
set -e

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
