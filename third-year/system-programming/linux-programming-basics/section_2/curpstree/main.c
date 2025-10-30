#include <stddef.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <stdio.h>

#define INIT_PS_PID 1
#define GET_PARENT_PID_ERR 2

pid_t get_parent_pid(pid_t pid) {
    char path[64];
    snprintf(path, sizeof(path), "/proc/%d/status", pid);

    FILE* status_file_ptr = fopen(path, "r");
    if (!status_file_ptr) {
        perror("fopen");
        return -1;
    }

    char line[256];
    pid_t ppid = -1;
    while (fgets(line, sizeof(line), status_file_ptr)) {
        if (strncmp(line, "PPid:", 5) == 0) {
            sscanf(line + 5, "%d", &ppid);
            break;
        }
    }

    fclose(status_file_ptr);
    return ppid;
}

char* get_process_program_name(pid_t pid) {
    static char program_name[256];
    char path[64];
    snprintf(path, sizeof(path), "/proc/%d/status", pid);

    FILE* status_file_ptr = fopen(path, "r");
    if (!status_file_ptr) {
        perror("fopen");
        return NULL;
    }

    char line[256];
    while (fgets(line, sizeof(line), status_file_ptr)) {
        if (strncmp(line, "Name:", 5) == 0) {
            sscanf(line + 5, "%s", program_name);
            fclose(status_file_ptr);
            return program_name;
        }
    }

    fclose(status_file_ptr);
    return NULL;
}

void curpstree(pid_t pid) {
    static size_t space_counter = 0;

    pid_t parent_pid = get_parent_pid(pid);

    if (parent_pid != INIT_PS_PID) {
        if (parent_pid == -1) {
            exit(GET_PARENT_PID_ERR);
        }

        curpstree(parent_pid);
    }

    char* program_name = get_process_program_name(parent_pid);

    for (size_t i = 0; i < space_counter; i++)
        printf("  ");

    printf("%s(%d)\n", program_name, parent_pid);
    space_counter++;

    return;
}

int main() {
    curpstree(getpid());
}
