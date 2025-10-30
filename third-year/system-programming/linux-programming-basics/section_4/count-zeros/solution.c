#include <stddef.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define READ_MODE "r"
#define ZERO_SYM '0'
#define SPACE " "

int main(int argc, char **argv) {
    if (argc != 3) {
        puts("Incorrect arguments");
        return 1;
    }

    FILE *proc;
    int c;
    size_t result;
    char *command;
    size_t command_len;

    result = 0;

    command_len = strlen(argv[1]) + strlen(argv[2]) + strlen(SPACE) + 1;
    command = malloc(command_len);
    if (command == NULL) {
        puts("Memory allocation failed");
        return 2;
    }
    strcpy(command, argv[1]);
    strcat(command, SPACE);
    strcat(command, argv[2]);

    proc = popen(command, READ_MODE);
    if (proc == NULL) {
        puts("Unable to open process");
        return 3;
    }

    while ((c = fgetc(proc)) != EOF) {
        if (c == ZERO_SYM)
            result++;
    }

    printf("%zu\n", result);

    return 0;
}
