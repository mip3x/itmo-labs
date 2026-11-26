#!/bin/sh
set -eu

owner=$(id -un)

if ! ip link show br-lab2 >/dev/null 2>&1; then
  sudo ip link add br-lab2 type bridge
fi
sudo ip link set br-lab2 up

for tap in tap-lab2-u tap-lab2-w; do
  if ! ip link show "$tap" >/dev/null 2>&1; then
    sudo ip tuntap add dev "$tap" mode tap user "$owner"
  fi
  sudo ip link set "$tap" master br-lab2
  sudo ip link set "$tap" up
done
