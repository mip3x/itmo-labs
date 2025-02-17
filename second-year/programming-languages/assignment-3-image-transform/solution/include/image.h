#ifndef IMAGE_H
#define IMAGE_H

#include <inttypes.h>
#include <stdint.h>
#include <stdio.h>

#define PNG_SIGNATURE "\x89PNG\r\n\x1A\n"
#define JPEG_SIGNATURE "\xFF\xD8\xFF"
#define BMP_SIGNATURE_STR "BM"

typedef enum {
    READ_OK = 0,
    READ_INVALID_SIGNATURE,
    READ_ENOMEM,
    READ_INVALID_BITS,
    READ_INVALID_HEADER,
    READ_INVALID_BODY,
    READ_UNSUPPORTED_HEADER,
    READ_UNEXPECTED_ERROR
    /* коды других ошибок  */
} read_image_status;

typedef enum {
    TRANSFORM_OK = 0,
    TRANSFORM_ERROR 
} transform_image_status;

typedef enum {
    WRITE_OK = 0,
    WRITE_HEADER_ERROR,
    WRITE_BODY_ERROR,
    WRITE_UNEXPECTED_ERROR
} write_image_status;

typedef enum {
    IMAGE_TYPE_BMP,
    IMAGE_TYPE_JPEG,
    IMAGE_TYPE_PNG,
    IMAGE_TYPE_UNKNOWN
} image_type;

struct pixel { 
    uint8_t b, g, r;
};

struct image {
    uint64_t width, height;
    struct pixel* data;
    image_type type;
};

typedef read_image_status (*image_reader)(FILE* input_stream, struct image* image);
typedef write_image_status (*image_writer)(FILE* output_stream, const struct image* image);

typedef struct {
    image_type type;
    image_reader reader;
    image_writer writer;
} image_handler;

extern const image_handler handlers[];
extern const size_t handlers_count;

image_type get_image_type(FILE* input_stream);

read_image_status read_image(FILE* input_stream, struct image* image);

write_image_status write_image(FILE* output_stream, const struct image* image);

struct image create_image(uint64_t width, uint64_t height);

#endif
