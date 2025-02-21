#include <inttypes.h>
#include <stdarg.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "image.h"
#include "io.h"
#include "list.h"
#include "status.h"
#include "transformation.h"

transformation_map transformation_list[] = {
    {"none",  none},
    {"fliph", flip_h},
    {"flipv", flip_v},
    {"cw90",  cw90},
    {"ccw90", ccw90},
};

_Noreturn void err(const uint8_t err_code, const char* msg, ...) {
    va_list args;
    va_start(args, msg);
    fprintf(stderr, "(%" PRIu8 ") Error: ", err_code);
    vfprintf(stderr, msg, args); // NOLINT
    fprintf(stderr, "\n");
    va_end(args);
    exit(err_code);
}

void print_usage(void) {
    fprintf(stderr, "Usage: ./image-transform <source-image> <transformed-image> <tranformation>\n\n");
    fprintf(stderr, "The last parameter specifies the transformation can take values strictly from the list:\n");
    fprintf(stderr, "none - do nothing, the original image is saved\n");
    fprintf(stderr, "cw90 - rotate the image 90 degrees clockwise\n");
    fprintf(stderr, "ccw90 - rotate the image 90 degrees counterclockwise\n");
    fprintf(stderr, "fliph - flip the image horizontally\n");
    fprintf(stderr, "flipv - flip the image vertically\n\n");
}

void handle_status(status op_status, struct list* resources_to_free) {
    if (op_status.value.code != 0) {
        if (resources_to_free) list_destroy(resources_to_free);
        err(op_status.value.code, get_description_by_status(op_status));
    }
}

struct image (*get_transform_function(const char* operation))(struct image const source) {
    if (!operation) return NULL;

    const size_t count = sizeof(transformation_list) / sizeof(transformation_list[0]);

    for (size_t i = 0; i < count; i++) {
        if (strcmp(transformation_list[i].name, operation) == 0) return transformation_list[i].func;
    }

    return NULL;
}

status load_image(const char* path, struct image* image) {
    status load_status;
    file image_file = open_file(path, FILE_READ_BYTES_MODE);

    load_status = (status) {.type = STATUS_TYPE_IO, .value.io = image_file.io_status};

    if (load_status.value.code != 0){
        close_file(image_file.stream);
        return load_status;
    }

    read_image_status read_image_status = read_image(image_file.stream, image);
    load_status.type = STATUS_TYPE_READ_IMAGE;
    load_status.value.read_image = read_image_status;

    if (load_status.value.code != 0) {
        close_file(image_file.stream);
        return load_status;
    }

    io_status close_image_status = close_file(image_file.stream);
    load_status.type = STATUS_TYPE_IO;
    load_status.value.io = close_image_status;
    if (load_status.value.code != 0) return load_status;

    return load_status;
}

status save_image(const char* path, const struct image* image) {
    status op_status = {0};

    file output_file = open_file(path, FILE_WRITE_BYTES_MODE);
    op_status.type = STATUS_TYPE_IO;
    op_status.value.io = output_file.io_status;

    if (op_status.value.code != 0) {
        close_file(output_file.stream);
        return op_status; 
    }

    write_image_status write_status = write_image(output_file.stream, image);
    op_status.type = STATUS_TYPE_WRITE_IMAGE;
    op_status.value.write_image = write_status;

    if (op_status.value.code != 0) {
        close_file(output_file.stream);
        return op_status;
    }

    io_status close_status = close_file(output_file.stream);
    op_status.type = STATUS_TYPE_IO;
    op_status.value.io = close_status;

    return op_status;
}

