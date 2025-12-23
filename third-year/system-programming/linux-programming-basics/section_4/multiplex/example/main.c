#include <stdio.h>
#include <sys/select.h>
#include <unistd.h>
#include <fcntl.h>

void read_and_report(int fd){
    char buf[100];

    printf("FD %d is ready to read\n", fd);
    int bytes = read(fd, buf, 100);

    buf[bytes] = 0;
    printf("Get %d bytes from %d : %s\n", bytes, fd, buf);
}

int main() {
    int f1 = open("./f1.fifo", O_RDWR);
    int f2 = open("./f2.fifo", O_RDWR);

    if (! (f1 && f2) ) {
        printf("Error with opening pipes\n");
        return 0;
    }

    printf("Descriptors: %d %d\n", f1, f2);

    fd_set read_set;

    while (1) {
        FD_ZERO(&read_set);
        FD_SET(f1, &read_set);
        FD_SET(f2, &read_set);

        int result = select(f2 + 1, &read_set, NULL, NULL, NULL);

        if (result) {
            if (FD_ISSET(f1, &read_set))
                read_and_report(f1);
            if (FD_ISSET(f2, &read_set))
                read_and_report(f2);

            printf("select returned %d\n", result);
        }
    }

    return 0;
}
