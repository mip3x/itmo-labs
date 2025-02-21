#ifndef UTIL_H
#define UTIL_H

#include "status.h"
#include <stdint.h>

_Noreturn void err(const uint8_t err_code, const char* msg, ...);

void print_usage(void);

void handle_status(status op_status, void* resource_to_free);

status load_image(const char* path, struct image* image);

status save_image(const char* path, const struct image* image);

#endif
