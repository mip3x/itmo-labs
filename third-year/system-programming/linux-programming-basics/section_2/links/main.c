#define _GNU_SOURCE

#include <stddef.h>
#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>

int main(int c, const char **v) {
    char buf[100];
    size_t size = 0;

    if (c != 2) {
        printf("\nPlease call\n\t%s filename\n", v[0]);
        return 1;
    }

    int fd = open(v[1], O_RDONLY | O_PATH);
    perror("fd");
    printf("fd = %d\n", fd);
    size = read(fd, buf, 100);

    printf("size = %ld\n", size);
}
