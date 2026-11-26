#!/bin/sh
set -eu
sh "$(dirname "$0")/setup-network.sh"
exec qemu-system-x86_64 \
  -name Windows-Lab2 \
  -enable-kvm -cpu host -m 2048 -smp 2 \
  -drive file=/var/lib/virt-tech-lab2-a2/windows.qcow2,format=qcow2,if=ide \
  -netdev user,id=net0,hostfwd=tcp:127.0.0.1:13389-:3389 \
  -device e1000,netdev=net0 \
  -netdev tap,id=lan,ifname=tap-lab2-w,script=no,downscript=no \
  -device e1000,netdev=lan,mac=02:00:00:88:00:03 \
  -vga std -display gtk \
  -device qemu-xhci -device usb-tablet
