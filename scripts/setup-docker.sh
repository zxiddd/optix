#!/bin/bash
# Configure Docker Networks & Log Rotation Daemon
set -e

echo "=== Creating Persistent Docker Networks ==="
docker network create optix-staging-network || true
docker network create optix-prod-network || true

echo "=== Configuring Docker Daemon Log Rotation (/etc/docker/daemon.json) ==="
sudo mkdir -p /etc/docker
cat <<EOF | sudo tee /etc/docker/daemon.json
{
  "log-driver": "json-file",
  "log-opts": {
    "max-size": "50m",
    "max-file": "5"
  }
}
EOF

sudo systemctl restart docker
