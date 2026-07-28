#!/bin/bash
# Install Docker CE & Docker Compose V2 on Ubuntu 24.04 LTS - Idempotent Script
set -e

if command -v docker >/dev/null 2>&1 && docker compose version >/dev/null 2>&1; then
    echo "Docker is already installed ($(docker --version)). Skipping repository setup."
    exit 0
fi

echo "=== Installing Official Docker CE Repository ==="
sudo install -m 0755 -d /etc/apt/keyrings

if [ ! -f /etc/apt/keyrings/docker.asc ]; then
    sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
    sudo chmod a+r /etc/apt/keyrings/docker.asc
fi

# Prevent duplicate repository sources entries
if [ ! -f /etc/apt/sources.list.d/docker.list ] && [ ! -f /etc/apt/sources.list.d/docker.sources ]; then
    echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
fi

sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

echo "=== Enabling & Starting Docker Systemd Service ==="
sudo systemctl enable docker
sudo systemctl start docker

echo "Docker Version: $(docker --version)"
echo "Docker Compose Version: $(docker compose version)"
