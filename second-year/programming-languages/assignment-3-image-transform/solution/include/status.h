#ifndef STATUS_H
#define STATUS_H

#include "image.h"
#include "io.h"
#include <stdint.h>

typedef enum {
    STATUS_TYPE_IO,
    STATUS_TYPE_READ_IMAGE,
    STATUS_TYPE_TRANSFORM_IMAGE,
    STATUS_TYPE_WRITE_IMAGE
} status_type;

typedef union {
    io_status io;
    read_image_status read_image;
    transform_image_status transform_image;
    write_image_status write_image;
    uint8_t code;
} status_value;

typedef struct {
    status_type type;
    status_value value;
} status;

const char* get_description_by_status(status status);

#endif
