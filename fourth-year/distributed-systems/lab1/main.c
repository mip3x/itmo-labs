#include <errno.h>
#include <sys/wait.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#include "common.h"
#include "ipc.h"
#include "process.h"
#include "pa1.h"

#define OK 0
#define ERR_WRONG_ARGS 1
#define ERR_FOPEN 2
#define ERR_PIPE 3
#define ERR_FORK 4
#define ERR_WAIT 5
#define ERR_SEND_MULTICAST 6
#define ERR_RECEIVE_FROM_CHILDREN 7
#define ERR_FINISH_WORK_CHILD 8

#define WRITE_MODE "w"

static void log_event(FILE *file, const char *text) {
    fputs(text, stdout);
    fputs(text, file);
}

static int receive_from_children(Process *process, MessageType type) {
    Message msg;

    for (local_id from = 1; from < process->total_processes; from++) {
        if (from == process->id)
            continue;

        if (receive(process, from, &msg) != 0 || msg.s_header.s_type != type)
            return -1;
    }

    return 0;
}

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
    } else {
        print_usage_msg(argv[0]);
        return ERR_WRONG_ARGS;
    }

    unsigned int total_processes = child_processes + 1;

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

    FILE *event_log = fopen(events_log, WRITE_MODE);
    if (event_log == NULL) {
        fprintf(stderr, "fopen events.log\n");
        return ERR_FOPEN;
    }
    // switching off bufferization
    // so writes will not be going through cache
    // they will go direct to file & terminal
    setbuf(event_log, NULL);
    setbuf(stdout, NULL);

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

    Message msg = {0};
    char text[256];

    msg.s_header.s_magic = MESSAGE_MAGIC;
    msg.s_header.s_local_time = 0;

    // send START MSG from children
    if (process.id != PARENT_ID) {
        int length = snprintf(msg.s_payload, MAX_PAYLOAD_LEN,
                          log_started_fmt,
                          (int)process.id,
                          (int)getpid(),
                          (int)getppid());
                        
        if (length < 0 || length >= MAX_PAYLOAD_LEN)
            return 1;

        msg.s_header.s_type = STARTED;
        msg.s_header.s_payload_len = (uint16_t)length;

        log_event(event_log, msg.s_payload);

        if (send_multicast(&process, &msg) != 0)
            return ERR_SEND_MULTICAST;
    }

    // child receives all other START MSGS
    if (receive_from_children(&process, STARTED) != 0)
        return ERR_RECEIVE_FROM_CHILDREN;

    // child sends DONE msg to all other procs
    if (process.id != PARENT_ID) {
        snprintf(text, sizeof(text),
                log_received_all_started_fmt, (int)process.id);
        log_event(event_log, text);

        int length = snprintf(msg.s_payload, MAX_PAYLOAD_LEN,
                            log_done_fmt, (int)process.id);

        if (length < 0 || length >= MAX_PAYLOAD_LEN)
            return ERR_FINISH_WORK_CHILD;

        msg.s_header.s_type = DONE;
        msg.s_header.s_payload_len = (uint16_t)length;

        log_event(event_log, msg.s_payload);

        if (send_multicast(&process, &msg) != 0)
            return ERR_SEND_MULTICAST;
    }

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

    // clear all resources
    for (uint8_t from = 0; from < total_processes; from++) {
        for (uint8_t to = 0; to < total_processes; to++) {
            for (int end = 0; end < 2; ++end) {
                if (process.pipes[from][to][end] != -1)
                    close(process.pipes[from][to][end]);
            }
        }
    }
    fclose(event_log);;

    return OK;
}