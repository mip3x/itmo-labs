#include <unistd.h>
#include <stdio.h>

int main() {
    pid_t pid = getpid();
    printf("%d\n", pid);

    daemon(0, 0);
    sleep(1000);
}
