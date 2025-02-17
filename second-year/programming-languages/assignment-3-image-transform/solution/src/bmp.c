#include "bmp.h"
#include "image.h"
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>

static uint32_t calculate_padding(uint64_t width) {
    return (PADDING_MAX_SIZE - ((sizeof(struct pixel) * width) % PADDING_MAX_SIZE) % PADDING_MAX_SIZE);
}

static read_image_status read_bmp_header(FILE* input_stream, struct image* image) {
    bmp_header header;
    if (fread(&header, sizeof(bmp_header), 1, input_stream) != 1) return READ_INVALID_HEADER;

    image->width = header.bi_width;
    image->height = header.bi_height;

    if (header.bi_bit_count != DIB_SUPPORTED_BIT_COUNT) return READ_INVALID_BITS;

    return READ_OK;
}

static read_image_status read_bmp_body(FILE* input_stream, struct image* image) {
    uint32_t padding = calculate_padding(image->width);
    uint32_t row_size = sizeof(struct pixel) * image->width;
    uint32_t total_size_wo_padding = row_size * image->height;

    image->data = malloc(total_size_wo_padding);
    if (!image->data) return READ_ENOMEM;

    for (uint64_t line = 0; line < image->height; line++) {
        if (fread(image->data + image->width * line, sizeof(struct pixel), image->width, input_stream) != image->width)
            return READ_INVALID_BODY;

        if (fseek(input_stream, (long)padding, SEEK_CUR) == -1) return READ_INVALID_BODY;
    }

    return READ_OK;
}

read_image_status read_bmp(FILE* input_stream, struct image* image) {
    if (!input_stream || !image) return READ_UNEXPECTED_ERROR;

    read_image_status read_header_status = read_bmp_header(input_stream, image);
    if (read_header_status != READ_OK) return read_header_status;

    read_image_status read_body_status = read_bmp_body(input_stream, image);
    if (read_body_status != READ_OK) return read_body_status;

    return READ_OK;
}

static write_image_status write_bmp_header(FILE* output_stream, const struct image* image) {
    uint32_t padding = calculate_padding(image->width);
    uint32_t row_size = sizeof(struct pixel) * image->width;
    uint32_t total_size_with_padding = (row_size + padding) * image->height;

    bmp_header header = {
        .bf_type = BMP_SIGNATURE_INT,
        .b_file_size = sizeof(bmp_header) + total_size_with_padding,
        .bf_reserved = BMP_RESERVED,
        .b_off_bits = sizeof(bmp_header),
        .bi_size = DIB_HEADER_SIZE,
        .bi_width = image->width,
        .bi_height = image->height,
        .bi_planes = DIB_COLOR_PLANES,
        .bi_bit_count = DIB_SUPPORTED_BIT_COUNT,
        .bi_compression = DIB_COMPRESSION,
        .bi_size_image = total_size_with_padding,
        .bi_x_pels_per_meter = DIB_RESOLUTION_HORIZONTAL,
        .bi_y_pels_per_meter = DIB_RESOLUTION_VERTICAL,
        .bi_clr_used = DIB_COLOR_USED,
        .bi_clr_important = DIB_COLOR_IMPORTANT
    };

    if (fwrite(&header, sizeof(bmp_header), 1, output_stream) != 1) return WRITE_HEADER_ERROR;

    return WRITE_OK;
}

static write_image_status write_bmp_body(FILE* output_stream, const struct image* image) {
    uint32_t padding = calculate_padding(image->width);

    for (uint64_t line = 0; line < image->height; line++) {
        if (fwrite(image->data + image->width * line, sizeof(struct pixel), image->width, output_stream) != image->width) 
            return WRITE_BODY_ERROR;

        if (fwrite("\0\0\0", 1, padding, output_stream) != padding) return WRITE_BODY_ERROR;
    }

    return WRITE_OK;
}

write_image_status write_bmp(FILE* output_stream, const struct image* image) {
    if (!output_stream || !image) return WRITE_UNEXPECTED_ERROR;

    write_image_status write_header_status = write_bmp_header(output_stream, image);
    if (write_header_status != WRITE_OK) return write_header_status;

    write_image_status write_body_status = write_bmp_body(output_stream, image);
    if (write_body_status != WRITE_OK) return write_body_status;

    return WRITE_OK;
}
