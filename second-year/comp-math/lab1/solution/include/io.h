#ifndef IO_H
#define IO_H

#include <stdlib.h>

char* read_file(const char* file_path, size_t* file_size);

double** allocate_matrix(size_t n);
void free_matrix(double** matrix, size_t n);

double** read_matrix_stdin(size_t n);
double** read_matrix_file(const char* file_path, size_t n);

double* read_vector_stdin(size_t n);
double* read_vector_file(const char* file_path, size_t n);

#endif
