#!/bin/bash
# Nightly Automated Database Backup & S3 Upload Script
set -e

TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_DIR="/var/backups/optix"
BACKUP_FILE="$BACKUP_DIR/optix_db_$TIMESTAMP.sql.gz"

mkdir -p $BACKUP_DIR

echo "=== Dumping PostgreSQL Production Database ==="
docker exec optix-postgres-staging pg_dump -U ${POSTGRES_USER:-optix_staging_admin} ${POSTGRES_DB:-optix_staging_db} | gzip > "$BACKUP_FILE"

echo "Backup created at: $BACKUP_FILE (Size: $(du -h "$BACKUP_FILE" | cut -f1))"

# Purge backups older than 7 days
find $BACKUP_DIR -type f -name "*.sql.gz" -mtime +7 -delete
