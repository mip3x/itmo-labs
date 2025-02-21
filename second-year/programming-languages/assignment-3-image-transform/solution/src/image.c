#include "bmp.h"
#include "image.h"
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

const image_handler handlers[] = {
    { IMAGE_TYPE_BMP, read_bmp, write_bmp },
};

const size_t handlers_count = sizeof(handlers) / sizeof(handlers[0]);

image_type get_image_type(FILE *input_stream) {
    if (!input_stream) return IMAGE_TYPE_UNKNOWN;

    unsigned char buffer[8];
    size_t bytes_read = fread(buffer, 1, sizeof(buffer), input_stream);
    fseek(input_stream, 0, SEEK_SET);

    if (bytes_read >= 8 && memcmp(buffer, PNG_SIGNATURE, sizeof(PNG_SIGNATURE) - 1) == 0) return IMAGE_TYPE_PNG;
    else if (bytes_read >= 3 && memcmp(buffer, JPEG_SIGNATURE, sizeof(JPEG_SIGNATURE) - 1) == 0) return IMAGE_TYPE_JPEG;
    else if (bytes_read >= 2 && memcmp(buffer, BMP_SIGNATURE_STR, sizeof(BMP_SIGNATURE_STR) - 1) == 0) return IMAGE_TYPE_BMP;

    return IMAGE_TYPE_UNKNOWN;
}

read_image_status read_image(FILE* input_stream, struct image* image) {
    if (!input_stream || !image) return READ_UNEXPECTED_ERROR;

    image->type = get_image_type(input_stream);
    if (image->type == IMAGE_TYPE_UNKNOWN) return READ_INVALID_HEADER;

    for (size_t i = 0; i < handlers_count; i++) {
        if (handlers[i].type == image->type) return handlers[i].reader(input_stream, image);
    }

    return READ_UNSUPPORTED_HEADER;
}

write_image_status write_image(FILE* output_stream, const struct image* image) {
    if (!output_stream || !image) return WRITE_UNEXPECTED_ERROR;

    for (size_t i = 0; i < handlers_count; i++) {
        if (handlers[i].type == image->type) return handlers[i].writer(output_stream, image);
    }

    return WRITE_UNEXPECTED_ERROR;
}

struct image create_image(uint64_t width, uint64_t height) {
    struct pixel* data_pointer = malloc(sizeof(struct pixel) * width * height);
    return (struct image){ .width = width, .height = height, .data = data_pointer };
}
