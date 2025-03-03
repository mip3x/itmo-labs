#ifndef DOMINANCE_H
#define DOMINANCE_H

#include <stdbool.h>
#include <stddef.h>

bool is_strictly_diagonally_dominant(double** A, size_t n);

bool try_make_diagonally_dominant(double** A, double* b, size_t n);

#endif
