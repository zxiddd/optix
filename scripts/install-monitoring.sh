#!/bin/bash
# Install Monitoring Systemd Timers & Disk Alert Monitors
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "=== Scheduling Automated Daily Database Backup Cron Job ==="
(crontab -l 2>/dev/null | grep -v "backup.sh"; echo "0 2 * * * bash $SCRIPT_DIR/backup.sh >> /var/log/optix-backup.log 2>&1") | crontab -

echo "=== System Monitoring & Backup Scheduled Tasks Configured ==="
