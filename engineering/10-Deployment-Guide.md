# 10 - Ubuntu 24.04 LTS VPS Deployment & Infrastructure Guide [v1.0 FROZEN ARCHITECTURE]

## Purpose
This document provides step-by-step deployment instructions, server provisioning scripts, Nginx reverse proxy configurations, PM2 cluster orchestration, Let's Encrypt SSL Certbot automation, database migration execution, and zero-downtime reload protocols for hosting the **Optix Production Infrastructure** on Ubuntu 24.04 LTS.

---

## Goals
1. Provide a single-command server bootstrap script provisioning Node.js 20 LTS, PostgreSQL 16, Redis 7.2, pgBouncer, Nginx, and PM2 on a fresh Ubuntu 24.04 LTS VPS.
2. Configure Nginx with HTTP/2 and TLS 1.3 encryption for `api.optixpos.com`.
3. Enable zero-downtime production code reloads using PM2 process manager (`pm2 reload`).
4. Establish automated nightly PostgreSQL backup scripts uploading database dumps to offsite S3 buckets.

---

## Production Server Environment Topology

```
+-----------------------------------------------------------------------------------+
|                        UBUNTU 24.04 LTS PRODUCTION VPS                            |
|                                                                                   |
|  +-----------------------------------------------------------------------------+  |
|  | Nginx 1.24+ Reverse Proxy (TLS 1.3 / Let's Encrypt SSL / Port 443)            |  |
|  +--------------------------------------+--------------------------------------+  |
|                                         |                                         |
|         +-------------------------------+-------------------------------+         |
|         |                               |                               |         |
|  +------v-------+                +------v-------+                +------v-------+  |
|  | PM2 Process  |                | PM2 Process  |                | PM2 Worker   |  |
|  | Instance 3000|                | Instance 3001|                | (BullMQ Queue)|  |
|  +------+-------+                +------+-------+                +------+-------+  |
|         |                               |                               |         |
|         +-------------------------------+-------------------------------+         |
|                                         |                                         |
|                        +----------------+----------------+                        |
|                        |                                 |                        |
|              +---------v----------+            +---------v----------+             |
|              | Redis 7.2 Broker   |            | pgBouncer Pooler   |             |
|              | (Cache / Queue)    |            | (Port 6432)        |             |
|              +--------------------+            +---------+----------+             |
|                                                          |                        |
|                                                +---------v----------+             |
|                                                | PostgreSQL 16 DB   |             |
|                                                +--------------------+             |
+-----------------------------------------------------------------------------------+
```

---

## Step-by-Step Production Server Deployment Protocol

### Step 1: Server Provisioning & Bootstrap Script (`scripts/bootstrap-vps.sh`)

Execute on fresh Ubuntu 24.04 LTS VPS instance:

```bash
#!/bin/bash
# Optix VPS Server Bootstrap Script (Ubuntu 24.04 LTS)
set -e

echo "=== Updating OS Packages ==="
sudo apt update && sudo apt upgrade -y

echo "=== Installing Essential Dependencies ==="
sudo apt install -y curl git ufw nginx postgresql postgresql-contrib redis-server certbot python3-certbot-nginx pgbouncer

echo "=== Installing Node.js 20 LTS ==="
curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash -
sudo apt install -y nodejs

echo "=== Installing PM2 Global Process Manager ==="
sudo npm install -g pm2

echo "=== Configuring UFW Firewall ==="
sudo ufw allow 22/tcp
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw --force enable

echo "=== VPS Provisioning Complete ==="
```

---

### Step 2: Nginx Reverse Proxy Configuration (`/etc/nginx/sites-available/optix-api`)

```nginx
upstream optix_api_cluster {
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
        proxy_pass http://optix_api_cluster;
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

### Step 3: Zero-Downtime Production Deployment Script (`scripts/deploy-vps.sh`)

```bash
#!/bin/bash
# Optix Zero-Downtime Production Deployment Script
set -e

echo "=== Pulling Latest Code from Main Branch ==="
git pull origin main

echo "=== Installing Production Dependencies ==="
npm ci --only=production

echo "=== Compiling TypeScript Core ==="
npm run build

echo "=== Running Prisma Database Migrations ==="
npx prisma migrate deploy

echo "=== Seeding Base System Metadata ==="
npx ts-node database/seeds/system-required-seeds.ts

echo "=== Performing Zero-Downtime PM2 Cluster Reload ==="
pm2 reload ecosystem.config.js --update-env

echo "=== Deployment Successfully Completed ==="
```

---

## Backup & Disaster Recovery Verification Script (`scripts/db-backup.sh`)

```bash
#!/bin/bash
# Automated Daily PostgreSQL Database Backup Script
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_DIR="/var/backups/optix"
BACKUP_FILE="$BACKUP_DIR/optix_db_$TIMESTAMP.sql.gz"

mkdir -p $BACKUP_DIR
pg_dump -U optix_admin optix_production | gzip > $BACKUP_FILE

# Upload to encrypted S3 storage bucket
aws s3 cp $BACKUP_FILE s3://optix-database-backups/daily/

# Purge local backups older than 7 days
find $BACKUP_DIR -type f -name "*.sql.gz" -mtime +7 -delete
```

---

## Frozen Architecture Sign-Off
- **Status**: FROZEN (v1.0)
- **Tag**: `v1.0-deployment-guide-freeze`
