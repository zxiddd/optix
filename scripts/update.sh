#!/bin/bash
# VPS System & Package Security Update Script
set -e

echo "=== Updating System Package Index ==="
sudo apt update

echo "=== Upgrading Security & Package Patches ==="
sudo apt upgrade -y

echo "=== Removing Unnecessary Dependencies ==="
sudo apt autoremove -y

echo "=== System Package Update Complete ==="
