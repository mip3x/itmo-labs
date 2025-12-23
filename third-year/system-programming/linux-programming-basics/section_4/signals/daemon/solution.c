#include <stdlib.h>
#include <unistd.h>
#include <stdio.h>
#include <signal.h>

static void sig_handler(int signo) {
    switch (signo) {
        case SIGURG:
            exit(0);
    }
}

int main() {
    struct sigaction psa;
    psa.sa_handler = sig_handler;

    sigaction(SIGURG, &psa, NULL);

    pid_t pid = getpid();
    printf("%d\n", pid);

    daemon(0, 0);
    sleep(1000);
}
