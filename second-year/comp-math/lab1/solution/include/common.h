#ifndef COMMON_H
#define COMMON_H

#include <stdbool.h>
#include <stddef.h>

#define DEBUG(fmt, ...) debug(__func__, fmt)

void err(const char* fmt, ...);
void debug(const char* caller_func_name, const char* fmt, ...);

#endif
