#include <errno.h>
#include <sys/wait.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#include "common.h"
#include "ipc.h"
#include "process.h"

#define OK 0
#define ERR_WRONG_ARGS 1
#define ERR_FOPEN 2
#define ERR_PIPE 3
#define ERR_FORK 4
#define ERR_WAIT 5

#define WRITE_MODE "w"

void print_usage_msg(const char* binary_file_name) {
    fprintf(stderr, "Usage: %s -p X\nX - number of child processes\n", binary_file_name);
}

int main(int argc, char *argv[]) {
    unsigned int child_processes = 0;

    if (argc != 3) {
        print_usage_msg(argv[0]);
        return ERR_WRONG_ARGS;
    }

    if (strcmp(argv[1], "-p") == 0) {
        child_processes = atoi(argv[2]);
        printf("Got %u child processes\n", child_processes);
    } else {
        print_usage_msg(argv[0]);
        return ERR_WRONG_ARGS;
    }

    unsigned int total_processes = child_processes + 1;
    printf("Total processes: %u\n", total_processes);

    // create parent process
    Process process = {
        .id = PARENT_ID,
        .total_processes = total_processes
    };

    // fill in process channels
    for (uint8_t from = 0; from < total_processes; from++) {
        for (uint8_t to = 0; to < total_processes; to++) {
            process.pipes[from][to][0] = -1;
            process.pipes[from][to][1] = -1;
        }
    }

    // open pipe log
    FILE *pipe_log = fopen(pipes_log, WRITE_MODE);
    if (pipe_log == NULL) {
        fprintf(stderr, "fopen pipes.log\n");
        return ERR_FOPEN;
    }

    for (uint8_t from = 0; from < total_processes; from++) {
        for (uint8_t to = 0; to < total_processes; to++) {
            if (from == to)
                continue;

            if (pipe(process.pipes[from][to]) == -1) {
                fprintf(stderr, "pipe syscall err\n");
                fclose(pipe_log);
                return ERR_PIPE;
            }

            fprintf(pipe_log, "%u -> %u: read=%d write=%d\n",
                from, to,
                process.pipes[from][to][0],
                process.pipes[from][to][1]
            );
        }
    }
    fclose(pipe_log);

    // flush output so not to save it in child processes
    fflush(stdout);

    for (uint8_t id = 1; id < total_processes; id++) {
        pid_t pid = fork();

        if (pid == -1) {
            fprintf(stderr, "fork syscall err\n");
            return ERR_FORK;
        }

        if (pid == 0) {
            // child
            process.id = (local_id)id;
            break;
        }
        // parent just spins further
    }

    // close redundant pipe ends
    // if you are writer -> no need to read from this descriptor
    // if you are receiver -> no need to write to this descriptor
    for (uint8_t from = 0; from < total_processes; from++) {
        for (uint8_t to = 0; to < total_processes; to++) {
            if (from == to)
                continue;

            if (process.id != to) {
                close(process.pipes[from][to][0]);
                process.pipes[from][to][0] = -1;
            }

            if (process.id != from) {
                close(process.pipes[from][to][1]);
                process.pipes[from][to][1] = -1;
            }
        }
    }

    printf("Local ID=%d, PID=%ld, parent PID=%ld\n",
        process.id, (long)getpid(), (long)getppid()
    );

    if (process.id == PARENT_ID) {
        for (uint8_t i = 0; i < child_processes; i++) {
            pid_t result;

            do {
                // wait for child to finish
                result = wait(NULL);
            } while (result == -1 && errno == EINTR);

            if (result == -1) {
                fprintf(stderr, "wait syscall err\n");
                return ERR_WAIT;
            }
        }
    }

    return OK;
}