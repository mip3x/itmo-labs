#ifndef BMP_H
#define BMP_H

#include "image.h"
#include <stdint.h>
#include <stdio.h>

#define BMP_SIGNATURE_INT 0x4D42
#define PADDING_MAX_SIZE 4
#define BMP_RESERVED 0
#define DIB_HEADER_SIZE 40
#define DIB_COLOR_PLANES 1
#define DIB_SUPPORTED_BIT_COUNT 24
#define DIB_COMPRESSION 0
#define DIB_RESOLUTION_HORIZONTAL 2835
#define DIB_RESOLUTION_VERTICAL 2835
#define DIB_COLOR_USED 0
#define DIB_COLOR_IMPORTANT 0

#define FOR_BMP_HEADER( FOR_FIELD ) \
        FOR_FIELD( uint16_t,bf_type)\
        FOR_FIELD( uint32_t,b_file_size)\
        FOR_FIELD( uint32_t,bf_reserved)\
        FOR_FIELD( uint32_t,b_off_bits)\
        FOR_FIELD( uint32_t,bi_size)\
        FOR_FIELD( uint32_t,bi_width)\
        FOR_FIELD( uint32_t,bi_height)\
        FOR_FIELD( uint16_t,bi_planes)\
        FOR_FIELD( uint16_t,bi_bit_count)\
        FOR_FIELD( uint32_t,bi_compression)\
        FOR_FIELD( uint32_t,bi_size_image)\
        FOR_FIELD( uint32_t,bi_x_pels_per_meter)\
        FOR_FIELD( uint32_t,bi_y_pels_per_meter)\
        FOR_FIELD( uint32_t,bi_clr_used)\
        FOR_FIELD( uint32_t,bi_clr_important)

#define DECLARE_FIELD( t, n ) t n ;

typedef struct __attribute__((packed)) {
    FOR_BMP_HEADER( DECLARE_FIELD )
} bmp_header;

read_image_status read_bmp(FILE* input_stream, struct image* image);

write_image_status write_bmp(FILE* output_stream, const struct image* image);

#endif
