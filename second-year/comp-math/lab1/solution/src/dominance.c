#include "../include/dominance.h"
#include "../include/common.h"
#include <math.h> 
#include <stdbool.h>
#include <stdio.h>

static void swap_rows(double** A, size_t row1, size_t row2) {
    if (row1 == row2) return;
    double* temp = A[row1];
    A[row1] = A[row2];
    A[row2] = temp;
}

static void swap_b(double* b, size_t i, size_t j) {
    if (i == j) return;
    double tmp = b[i];
    b[i] = b[j];
    b[j] = tmp;
}

bool is_strictly_diagonally_dominant(double** A, size_t n) {
    for (size_t i = 0; i < n; i++) {
        double diag = fabs(A[i][i]);
        double off_diag_sum = 0.0;
        for (size_t j = 0; j < n; j++)
            if (j != i) off_diag_sum += fabs(A[i][j]);
        if (diag < off_diag_sum) return false;
    }
    return true;
}

bool try_make_diagonally_dominant(double** A, double* b, size_t n) {
    for (size_t i = 0; i < n; i++) {
        size_t pivot_row = i;
        double max_val = fabs(A[i][i]);

        for (size_t k = i + 1; k < n; k++) {
            double val = fabs(A[k][i]);
            if (val >= max_val) {
                max_val = val;
                pivot_row = k;
            }
        }
        if (pivot_row != i) {
            swap_rows(A, i, pivot_row);
            swap_b(b, i, pivot_row);
        }
    }
    debug(__func__, "new matrix\n");
    for (size_t i = 0; i < n; i++) {
        for (size_t j = 0; j < n; j++) {
            printf("%8.3lf ", A[i][j]);
        }
        printf("\n");
    }
    return is_strictly_diagonally_dominant(A, n);
}
