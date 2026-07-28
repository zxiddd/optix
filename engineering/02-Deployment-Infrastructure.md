# 02 - Ubuntu 24.04 LTS VPS, Nginx & PM2 Deployment Infrastructure

## Purpose
This document provides exact server deployment scripts, Nginx reverse proxy configuration files, PM2 ecosystem configurations, firewall setups, and Let's Encrypt SSL Certbot automation scripts for hosting the **Optix** backend core on Ubuntu 24.04 LTS.

---

## 1. Ubuntu 24.04 LTS VPS Provisioning Script

```bash
#!/bin/bash
# Ubuntu 24.04 LTS Server Bootstrap Script for Optix Backend
set -e

echo "=== Updating System Packages ==="
sudo apt update && sudo apt upgrade -y

echo "=== Installing Dependencies & Node.js 20 LTS ==="
sudo apt install -y curl git ufw nginx postgresql postgresql-contrib certbot python3-certbot-nginx

curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash -
sudo apt install -y nodejs

echo "=== Installing PM2 Global Process Manager ==="
sudo npm install -g pm2

echo "=== Configuring UFW Firewall ==="
sudo ufw allow 22/tcp
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw --force enable
```

---

## 2. Nginx Reverse Proxy Configuration (`/etc/nginx/sites-available/optix-api`)

```nginx
# Nginx Reverse Proxy Configuration for Optix REST API & Sync Engine
upstream optix_backend_cluster {
    server 127.0.0.1:3000 max_fails=3 fail_timeout=10s;
    server 127.0.0.1:3001 max_fails=3 fail_timeout=10s;
    keepalive 64;
}

server {
    listen 80;
    server_name api.optixpos.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name api.optixpos.com;

    ssl_certificate /etc/letsencrypt/live/api.optixpos.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/api.optixpos.com/privkey.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;

    # Rate Limiting Zone
    limit_req_zone $binary_remote_addr zone=api_limit:10m rate=30r/s;
    limit_req zone=api_limit burst=50 nodelay;

    client_max_body_size 15M;

    location / {
        proxy_pass http://optix_backend_cluster;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_read_timeout 90s;
    }
}
```

---

## 3. PM2 Ecosystem File (`ecosystem.config.js`)

```javascript
// PM2 Ecosystem Process Manager Configuration
module.exports = {
  apps: [
    {
      name: 'optix-api-3000',
      script: './dist/server.js',
      instances: 1,
      exec_mode: 'fork',
      env: {
        PORT: 3000,
        NODE_ENV: 'production'
      }
    },
    {
      name: 'optix-api-3001',
      script: './dist/server.js',
      instances: 1,
      exec_mode: 'fork',
      env: {
        PORT: 3001,
        NODE_ENV: 'production'
      }
    }
  ]
};
```
