#include "io.h"
#include "transformation.h"
#include "util.h"
#include <errno.h>
#include <stdio.h>

io_status process_input(int argc, char** argv, input* input) {
    if (argc != 4) print_usage();
    if (argc < 4) return IO_E2SMALL;
    if (argc > 4) return IO_E2BIG;

    input->original_image_path = argv[1];
    input->transformed_image_path = argv[2];
    input->transform_operation = argv[3];

    input->func = get_transform_function(input->transform_operation);
    if (!input->func) return IO_EUNKNOWN_OP;

    return IO_OK;
}

file open_file(const char *file_name, const char *mode) {
    file opened_file = { .stream = NULL, .io_status = IO_OK };
    opened_file.stream = fopen(file_name, mode);

    if (!opened_file.stream) {
        switch (errno) {
            case ENOENT:
                opened_file.io_status = IO_ENOENT;
                break;
            case EACCES:
                opened_file.io_status = IO_EACCESS;
                break;
            default:
                opened_file.io_status = IO_EUNKNOWN;
                break;
        }
    }

    return opened_file;
}

io_status close_file(FILE* output_stream) {
    if (output_stream) {
        if (fclose(output_stream) == EOF) return IO_ECLOSE;
    }
    return IO_OK;
}
