#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define ERR_WRONG_ARGS 1

void print_usage_msg(const char* binary_file_name) {
    fprintf(stderr, "Usage: %s -p X\nX - number of child processes\n", binary_file_name);
}

int main(int argc, char *argv[]) {
    unsigned int child_processes = 0;

    if (argc < 2) {
        print_usage_msg(argv[0]);
        return ERR_WRONG_ARGS;
    }

    if (strcmp(argv[1], "-p") == 0) {
        child_processes = atoi(argv[2]);
        printf("Got %d child processes\n", child_processes);
    } else {
        print_usage_msg(argv[0]);
        return ERR_WRONG_ARGS;
    }

    return 0;
}