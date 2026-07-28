#!/bin/bash
# PostgreSQL Database Restoration Script
# Usage: ./scripts/restore.sh <path_to_backup_file.sql.gz>

set -e

BACKUP_FILE="$1"

if [ -z "$BACKUP_FILE" ] || [ ! -f "$BACKUP_FILE" ]; then
    echo "ERROR: Please provide a valid backup archive file."
    echo "Usage: ./scripts/restore.sh /var/backups/optix/optix_db_20260729_020000.sql.gz"
    exit 1
fi

echo "=== Restoring PostgreSQL Database from $BACKUP_FILE ==="
gunzip -c "$BACKUP_FILE" | docker exec -i optix-postgres-staging psql -U ${POSTGRES_USER:-optix_staging_admin} -d ${POSTGRES_DB:-optix_staging_db}

echo "Database restoration completed successfully!"
