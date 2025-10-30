#include <signal.h>
#include <stdio.h>
#include <stdlib.h>

static int sigusr1_count = 0;
static int sigusr2_count = 0;

static void sig_handler(int signo) {
    switch (signo) {
        case SIGUSR1:
            sigusr1_count++;
            break;
        case SIGUSR2:
            sigusr2_count++;
            break;
        case SIGTERM:
            printf("%d %d\n", sigusr1_count, sigusr2_count);
            exit(0);
    }
}

int main() {
    struct sigaction psa;
    psa.sa_handler = sig_handler;

    sigaction(SIGUSR1, &psa, NULL);
    sigaction(SIGUSR2, &psa, NULL);
    sigaction(SIGTERM, &psa, NULL);

    for (;;) {}

    return 0;
}
