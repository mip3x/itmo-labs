#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/select.h>
#include <unistd.h>
#include <fcntl.h>
#include <stdbool.h>

int main() {
    int fds[2];
    char buf[4096];

    int i, rc, maxfd, result = 0;

    fd_set watch_set;
    fd_set read_set;

    if ((fds[0] = open("in1", O_RDONLY | O_NONBLOCK)) < 0) {
        perror("open in1");
        return 1;
    }

    if ((fds[1] = open("in2", O_RDONLY | O_NONBLOCK)) < 0) {
        perror("open in2");
        return 1;
    }

    FD_ZERO(&watch_set);
    FD_SET(fds[0], &watch_set);
    FD_SET(fds[1], &watch_set);

    maxfd = fds[0] > fds[1] ? fds[0] : fds[1];

    while (FD_ISSET(fds[0], &watch_set) || FD_ISSET(fds[1], &watch_set)) {
        read_set = watch_set;
        if (select(maxfd + 1, &read_set, NULL, NULL, NULL) < 0) {
            perror("select");
            return 1;
        }

        for (i = 0; i < 2; i++) {
            if (FD_ISSET(fds[i], &read_set)) {
                rc = read(fds[i], buf, sizeof(buf) - 1);
                if (rc < 0) {
                    perror("read");
                    return 1;
                } else if (!rc) {
                    close(fds[i]);
                    FD_CLR(fds[i], &watch_set);
                } else {
                    buf[rc] = '\0';

                    int j = 0;
                    for (j = 0; j < rc; j++) {
                        char c = buf[j];
                        if (c >= '0' && c <= '9') {
                            result += (int)(c - '0');
                        }
                        /*printf("%c", c);*/
                    }
                }
            }
        }
    }
    printf("%d\n", result);

    return 0;
}
