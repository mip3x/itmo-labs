#include <math.h>
#include "../include/io.h"
#include "../include/solver.h"
#include "../include/common.h"
#include <stdio.h>

double** iteration_matrix_norm(double** A, size_t n) {
    double** C = allocate_matrix(n);
    if (!C) {
        err("Ошибка выделения памяти\n");
        return NULL;
    }

    for (size_t i = 0; i < n; i++) {
        double aii = A[i][i];
        if (fabs(aii) <= 0) {
            err("Нулевой или слишком маленький диагональный элемент A[%zu][%zu]\n", i, i);
            free_matrix(C, n);
            return NULL;
        }

        for (size_t j = 0; j < n; j++) {
            if (j == i) C[i][j] = 0.0;
            else C[i][j] = -A[i][j] / aii;
        }
    }

        debug(__func__, "нормализованная матрица:\n");
        for (size_t i = 0; i < n; i++) {
                for (size_t j = 0; j < n; j++) {
                    printf("%8.3lf ", C[i][j]);
                }
                printf("\n");
            }
    return C;
}

double matrix_infinity_norm(double** A, size_t n) {
    double norm = 0.0;
    for (size_t i = 0; i < n; i++) {
        double row_sum = 0.0;
        for (size_t j = 0; j < n; j++)
            row_sum += fabs(A[i][j]);
        if (row_sum > norm)
            norm = row_sum;
    }
    return norm;
}

int gauss_seidel(double** A, double* b, size_t n, double* x, double* errors, double epsilon, int max_iter) {
    int iterations = 0;
    int converged = 0;
    while (iterations < max_iter) {
        double max_error = 0.0;
        for (size_t i = 0; i < n; i++) {
            double old_x = x[i];
            double sum = 0.0;
            for (size_t j = 0; j < n; j++) {
                if (j != i) sum += A[i][j] * x[j];
            }
            x[i] = (b[i] - sum) / A[i][i];
            errors[i] = fabs(x[i] - old_x);
            if (errors[i] > max_error)
                max_error = errors[i];
        }
        iterations++;
        if (max_error < epsilon) {
            converged = 1;
            break;
        }
    }
    return converged ? iterations : -1;
}
