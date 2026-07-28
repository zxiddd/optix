#!/bin/bash
# Optix API Health Check Probe Script
# Usage: ./scripts/healthcheck.sh <url>

TARGET_URL="${1:-http://localhost:3000/health}"
MAX_RETRIES=10
RETRY_INTERVAL=3

echo "=== Probing Health Endpoint: $TARGET_URL ==="

for ((i=1; i<=MAX_RETRIES; i++)); do
    HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" "$TARGET_URL" || true)
    if [ "$HTTP_STATUS" = "200" ]; then
        echo "[SUCCESS] Health probe passed! HTTP 200 OK (Attempt $i/$MAX_RETRIES)"
        exit 0
    fi
    echo "[WAIT] Attempt $i/$MAX_RETRIES: HTTP Status $HTTP_STATUS - Retrying in ${RETRY_INTERVAL}s..."
    sleep $RETRY_INTERVAL
done

echo "[FAILURE] Health probe failed after $MAX_RETRIES attempts."
exit 1
