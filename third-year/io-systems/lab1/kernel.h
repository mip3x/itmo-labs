#pragma once

#include "common.h"

#define NCPU 4

struct cpu {
    uint32_t hartid;
};

static inline struct cpu* mycpu(void) {
    struct cpu* cpu;
    __asm__ __volatile__("mv %0, tp" : "=r"(cpu));
    return cpu;
}