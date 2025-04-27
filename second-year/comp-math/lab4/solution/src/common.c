#include <errno.h>
#include <stdarg.h>
#include <stdio.h>
#include <string.h>

#include "../include/common.h"

void err(const char* fmt, ...) {
    int     errno_save;
    va_list ap;

    errno_save = errno;
    va_start(ap, fmt);
    fprintf(stderr, "[ERROR] ");
    vfprintf(stderr, fmt, ap);
    fprintf(stderr, "\n");
    fflush(stderr);

    if (errno_save != 0) {
        fprintf(stderr, "(errno = %d) : %s\n", errno_save, strerror(errno_save));
        fprintf(stderr, "\n");
        fflush(stderr);
    }

    va_end(ap);
}

void debug(const char* caller_func_name, const char* fmt, ...) {
    va_list ap;

    va_start(ap, fmt);
    fprintf(stdout, "[INFO (%s)] ", caller_func_name);
    vfprintf(stdout, fmt, ap);
    fprintf(stdout, "\n");
    fflush(stdout);

    va_end(ap);
}
