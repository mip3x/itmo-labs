#ifndef TRANSFORMATION_H
#define TRANSFORMATION_H

#include "image.h"

struct image none(struct image const source);
struct image flip_h(struct image const source);
struct image flip_v(struct image const source);
struct image cw90(struct image const source);
struct image ccw90(struct image const source);

typedef struct {
    const char* name;
    struct image (*func)(struct image const source);
} transformation_map;

extern transformation_map transformation_list[];

struct image (*get_transform_function(const char* operation))(struct image const source);

#endif
