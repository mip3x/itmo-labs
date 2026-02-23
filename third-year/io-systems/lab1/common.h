#pragma once

typedef unsigned char uint8_t;
typedef unsigned int uint32_t;
typedef uint32_t size_t;

struct sbiret {
    long error;
    long value;
};

struct sbiret sbi_call(long arg0, long arg1, long arg2, long arg3, long arg4,
                       long arg5, long fid, long eid);

#define va_list  __builtin_va_list
#define va_start __builtin_va_start
#define va_end   __builtin_va_end
#define va_arg   __builtin_va_arg

void printf(const char *fmt, ...);
void putchar(char ch);
long getchar();
void print_newline();
void puts(const char *string);
void *memset(void *buf, char c, size_t n);

#define NL_CHAR '\n'

#define EXTENSION_CONSOLE_PUTCHAR 0x01
#define EXTENSION_CONSOLE_GETCHAR 0x02

#define EXTENSION_HART_MGMT 0x48534D
#define F_HART_GET_STATUS 0x2
#define F_HART_STOP 0x1

#define EXTENSION_SYSTEM_SHUTDOWN 0x08

#define BASE_EXTENSION 0x10
#define F_SBI_GET_IMPL_VERSION 0x2