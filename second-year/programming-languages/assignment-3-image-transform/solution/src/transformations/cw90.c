#include "image.h"
#include "transformation.h"
#include <stdint.h>

struct image cw90(struct image const source) {
    struct image result = create_image(source.height, source.width);
    if (!result.data) return result;

    for (uint64_t y = 0; y < source.height; y++) {
        for (uint64_t x = 0; x < source.width; x++) {
            result.data[(source.width - 1 - x) * result.width + y] = source.data[y * source.width + x];
        }
    }

    return result;
}
