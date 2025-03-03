#ifndef SOLVER_H
#define SOLVER_H

#include <stddef.h>

double matrix_infinity_norm(double** A, size_t n);

int gauss_seidel(double** A, double* b, size_t n, double* x, double* errors, double epsilon, int max_iter);

double** iteration_matrix_norm(double** A, size_t n);

#endif
