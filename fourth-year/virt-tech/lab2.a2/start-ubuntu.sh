#!/bin/sh
set -eu
sh "$(dirname "$0")/setup-network.sh"
exec qemu-system-x86_64 \
  -name Ubuntu-Lab2 \
  -enable-kvm -cpu host -m 4096 -smp 2 \
  -drive file=/var/lib/virt-tech-lab2-a2/ubuntu.qcow2,format=qcow2,if=ide,index=0 \
  -device ich9-ahci,id=sata \
  -drive file=/var/lib/virt-tech-lab2-a2/data.qcow2,format=qcow2,if=none,id=data \
  -device ide-hd,drive=data,bus=sata.0 \
  -netdev user,id=net0,hostfwd=tcp:127.0.0.1:1800-:22 \
  -device e1000,netdev=net0 \
  -netdev tap,id=lan,ifname=tap-lab2-u,script=no,downscript=no \
  -device e1000,netdev=lan,mac=02:00:00:88:00:02 \
  -vga std -display gtk \
  -device qemu-xhci -device usb-tablet
