#!/bin/bash
set -xue

CPUS=4

# Путь QEMU 
QEMU=qemu-system-riscv32

# Путь к clang и его флагам
CC=/usr/bin/clang
CFLAGS="-std=c11 -O2 -g3 -Wall -Wextra --target=riscv32 -ffreestanding -nostdlib -fuse-ld=lld"

# Сборка ядра
$CC $CFLAGS -Wl,-Tkernel.ld -Wl,-Map=kernel.map -o kernel.elf kernel.c common.c

# Запуск QEMU
$QEMU -smp $CPUS -machine virt -bios default -nographic -serial mon:stdio --no-reboot -kernel kernel.elf
