#!/bin/bash
# Configure SSH Key Authentication & Hardening
set -e

USER_HOME="/home/optix"
if [ "$USER" = "root" ]; then
    USER_HOME="/root"
fi

mkdir -p "$USER_HOME/.ssh"
chmod 700 "$USER_HOME/.ssh"

if [ -f "$USER_HOME/.ssh/authorized_keys" ]; then
    chmod 600 "$USER_HOME/.ssh/authorized_keys"
    echo "SSH authorized_keys configured successfully."
fi

# SSH Daemon Hardening Options
sudo sed -i 's/#PermitRootLogin yes/PermitRootLogin prohibit-password/' /etc/ssh/sshd_config
sudo sed -i 's/#PasswordAuthentication yes/PasswordAuthentication yes/' /etc/ssh/sshd_config
sudo systemctl restart ssh || sudo systemctl restart sshd
