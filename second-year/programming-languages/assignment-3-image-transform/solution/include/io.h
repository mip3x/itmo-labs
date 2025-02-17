#ifndef IO_H
#define IO_H

#include <stdio.h>

#define FILE_READ_BYTES_MODE "rb"
#define FILE_WRITE_BYTES_MODE "wb"

typedef struct {
    const char* original_image_path;
    const char* transformed_image_path;
    const char* transform_operation;
    struct image (*func)(struct image const source);
} input;

typedef enum {
    IO_OK = 0,
    IO_ENOENT = 2,
    IO_EUNKNOWN = 5,
    IO_E2BIG = 7,
    IO_E2SMALL = 8,
    IO_EACCESS = 13,
    IO_ECLOSE,
    IO_EUNKNOWN_OP,
} io_status;

typedef struct {
    FILE* stream;
    io_status io_status;
} file;

io_status process_input(int argc, char** argv, input* input);

file open_file(const char* file_name, const char* mode);

io_status close_file(FILE* output_stream);

#endif
