#pragma once

#include <stdbool.h>

#define CMP_BUFFER_SIZE (4096 * 2)

enum cmp_result {
  CMP_EQ,
  CMP_DIFF,
  CMP_ERROR,
};

