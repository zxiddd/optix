#!/bin/bash
# Create Unprivileged Deployment User 'optix'
set -e

echo "=== Creating Deployment User 'optix' ==="
if ! id -u optix >/dev/null 2>&1; then
    sudo useradd -m -s /bin/bash optix
    sudo usermod -aG sudo optix
    echo "User 'optix' created successfully."
fi

# Add optix user to docker group
if getent group docker >/dev/null; then
    sudo usermod -aG docker optix
fi

# Create application deployment directory
sudo mkdir -p /opt/optix
sudo chown -R optix:optix /opt/optix
