#include <stdlib.h>
#include <stdio.h>
#include "../include/common.h"
#include "../include/io.h"

static size_t get_opened_file_size(FILE* file) {
    fseek(file, 0, SEEK_END);
    long file_size = ftell(file);
    fseek(file, 0, SEEK_SET);

    return (size_t)file_size;
}

char* read_file(const char* restrict file_path, size_t* file_size) {
    FILE* file = fopen(file_path, "r");
    if (file == NULL) {
        err("Проблема при открытии файла");
        return NULL;
    }

    *file_size = get_opened_file_size(file);
    char* file_content = malloc(*file_size + 1);

    if (file_content == NULL) {
        err("Проблема выделения памяти для содержимого файла");
        fclose(file);
        return NULL;
    }

    fread(file_content, 1, *file_size, file);
    file_content[*file_size] = '\0';

    fclose(file);
    return file_content;
}

double** allocate_matrix(size_t n) {
    double** matrix = (double**)malloc(n * sizeof(double*));
    if (!matrix) {
        err("Проблема для выделения памяти для строк матрицы");
        return NULL;
    }

    for (size_t i = 0; i < n; i++) {
        matrix[i] = (double*)malloc(n * sizeof(double));
        if (!matrix[i]) {
            err("Проблема для выделения строки матрицы № %zu", i);
            for (size_t k = 0; k < i; k++) {
                free(matrix[k]);
            }
            free(matrix);
            return NULL;
        }
    }
    return matrix;
}

void free_matrix(double** matrix, size_t n) {
    if (!matrix) return;
    for (size_t i = 0; i < n; i++) {
        free(matrix[i]);
    }
    free(matrix);
}

double** read_matrix_stdin(size_t n) {
    double** matrix = allocate_matrix(n);
    if (!matrix) {
        err("Невозможно выделить место для матрицы");
        return NULL;
    }

    printf("Введите матрицу (%zu строк, по %zu чисел в каждой):\n", n, n);
    for (size_t i = 0; i < n; i++) {
        for (size_t j = 0; j < n; j++) {
            if (scanf("%lf", &matrix[i][j]) != 1) {
                err("Ошибка чтения элемента [%zu][%zu] из stdin", i, j);
                free_matrix(matrix, n);
                return NULL;
            }
        }
    }

    return matrix;
}

double** read_matrix_file(const char* file_path, size_t n) {
    FILE* f = fopen(file_path, "r");
    if (!f) {
        err("Ошибка открытия файл '%s' для чтения матрицы", file_path);
        return NULL;
    }

    double** matrix = allocate_matrix(n);
    if (!matrix) {
        err("Невозможно выделить память под матрицу");
        fclose(f);
        return NULL;
    }

    for (size_t i = 0; i < n; i++) {
        for (size_t j = 0; j < n; j++) {
            if (fscanf(f, "%lf", &matrix[i][j]) != 1) {
                err("Ошибка чтения элемента [%zu][%zu] из файла '%s'", i, j, file_path);
                free_matrix(matrix, n);
                fclose(f);
                return NULL;
            }
        }
    }

    fclose(f);
    return matrix;
}

double* read_vector_stdin(size_t n) {
    double* vec = (double*)malloc(n * sizeof(double));
    if (!vec) {
        err("Ошибка выделения памяти для вектора");
        return NULL;
    }
    printf("Введите вектор правых частей (b) из %zu элементов:\n", n);
    for (size_t i = 0; i < n; i++) {
        if (scanf("%lf", &vec[i]) != 1) {
            err("Ошибка чтения элемента b[%zu] из stdin", i);
            free(vec);
            return NULL;
        }
    }
    return vec;
}

double* read_vector_file(const char* file_path, size_t n) {
    FILE* f = fopen(file_path, "r");
    if (!f) {
        err("Ошибка открытия файла '%s' для чтения вектора b", file_path);
        return NULL;
    }
    double* vec = (double*)malloc(n * sizeof(double));
    if (!vec) {
        err("Ошибка выделения памяти для вектора b");
        fclose(f);
        return NULL;
    }
    for (size_t i = 0; i < n; i++) {
        if (fscanf(f, "%lf", &vec[i]) != 1) {
            err("Ошибка чтения элемента b[%zu] из файла '%s'", i, file_path);
            free(vec);
            fclose(f);
            return NULL;
        }
    }
    fclose(f);
    return vec;
}
