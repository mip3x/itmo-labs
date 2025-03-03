#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <inttypes.h>

#include "../include/common.h"
#include "../include/io.h"
#include "../include/status.h"
#include "../include/intro_prompt.h"
#include "../include/dominance.h"
#include "../include/solver.h"

#define MAX_ITER 100

int main() {
    print_intro_prompt();
    printf("\n=== Решение СЛАУ методом Гаусса-Зейделя ===\n\n");

    size_t n;
    printf("Введите размер матрицы n: ");
    if (scanf("%zu", &n) != 1 || n == 0) {
        err("Некорректное значение n");
        return 1;
    }

    printf("Выберите источник ввода матрицы:\n");
    printf("1) Стандартный ввод\n");
    printf("2) Файл\n");
    printf("Ваш выбор: ");
    int choice;
    if (scanf("%d", &choice) != 1) {
        err("Ошибка чтения выбора");
        return 1;
    }

    double** A = NULL;
    if (choice == 1) {
        A = read_matrix_stdin(n);
    } else if (choice == 2) {
        char filename[256];
        printf("Введите имя файла для матрицы: ");
        if (scanf("%255s", filename) != 1) {
            err("Ошибка чтения имени файла");
            return 1;
        }
        A = read_matrix_file(filename, n);
    } else {
        err("Некорректный выбор: %d", choice);
        return 1;
    }
    if (!A) {
        err("Не удалось прочитать матрицу");
        return 1;
    }

    printf("\nПрочитанная матрица A:\n");
    for (size_t i = 0; i < n; i++) {
        for (size_t j = 0; j < n; j++) {
            printf("%8.3lf ", A[i][j]);
        }
        printf("\n");
    }

    printf("\nВыберите источник ввода вектора правых частей b:\n");
    printf("1) Стандартный ввод\n");
    printf("2) Файл\n");
    printf("Ваш выбор: ");
    if (scanf("%d", &choice) != 1) {
        err("Ошибка чтения выбора для вектора b");
        free_matrix(A, n);
        return 1;
    }
    double* b = NULL;
    if (choice == 1) {
        b = read_vector_stdin(n);
    } else if (choice == 2) {
        char filename_b[256];
        printf("Введите имя файла для вектора b: ");
        if (scanf("%255s", filename_b) != 1) {
            err("Ошибка чтения имени файла для вектора b");
            free_matrix(A, n);
            return 1;
        }
        b = read_vector_file(filename_b, n);
    } else {
        err("Некорректный выбор для вектора b: %d", choice);
        free_matrix(A, n);
        return 1;
    }
    if (!b) {
        err("Не удалось прочитать вектор правых частей b");
        free_matrix(A, n);
        return 1;
    }

    double epsilon;
    printf("\nВведите точность epsilon: ");
    if (scanf("%lf", &epsilon) != 1 || epsilon <= 0) {
        err("Некорректное значение epsilon");
        free_matrix(A, n);
        free(b);
        return 1;
    }

    if (!is_strictly_diagonally_dominant(A, n)) {
        printf("\nМатрица не обладает диагональным преобладанием.\nПытаемся добиться диагонального преобладания...\n");
        if (!try_make_diagonally_dominant(A, b, n)) {
            printf("Невозможно добиться диагонального преобладания.\n");
            /*free_matrix(A, n);*/
            /*free(b);*/
            /*return 1;*/
        } else {
            printf("Диагональное преобладание достигнуто после перестановок.\n");
            printf("Обновленная матрица A:\n");
            for (size_t i = 0; i < n; i++) {
                for (size_t j = 0; j < n; j++) {
                    printf("%8.3lf ", A[i][j]);
                }
                printf("\n");
            }
        }
    } else {
        printf("\nМатрица обладает диагональным преобладанием.\n");
    }

    double** C = iteration_matrix_norm(A, n);
    double normA = matrix_infinity_norm(C, n);
    printf("\nНорма матрицы A: %lf\n", normA);

    double* x = (double*)calloc(n, sizeof(double));
    double* errors = (double*)calloc(n, sizeof(double));
    if (!x || !errors) {
        err("Ошибка выделения памяти для вектора решения или ошибок");
        free_matrix(A, n);
        free(b);
        free(x);
        free(errors);
        return 1;
    }

    int iter = gauss_seidel(A, b, n, x, errors, epsilon, MAX_ITER);
    if (iter == -1) printf("\nМетод не сошелся за %d итераций.\n", MAX_ITER);
    else printf("\nМетод сошелся за %d итераций.\n", iter);

    printf("\nВектор неизвестных x:\n");
    for (size_t i = 0; i < n; i++)
        printf("x[%zu] = %lf\n", i, x[i]);

    printf("\nВектор погрешностей:\n");
    for (size_t i = 0; i < n; i++)
        printf("sigma[%zu] = %lf\n", i, errors[i]);

    free_matrix(A, n);
    free(b);
    free(x);
    free(errors);

    return SUCCESS;
}

