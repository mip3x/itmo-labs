#include <stddef.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <stdio.h>

#define INIT_PS_PID 1
#define GET_PARENT_PID_ERR 2
#define INCORRECT_ARGS_ERR 3

pid_t get_parent_pid(pid_t pid)
{
    char path[64];
    snprintf(path, sizeof(path), "/proc/%d/status", pid);

    FILE* status_file_ptr = fopen(path, "r");
    if (!status_file_ptr)
    {
        perror("fopen");
        return -1;
    }

    char line[256];
    pid_t ppid = -1;
    while (fgets(line, sizeof(line), status_file_ptr))
    {
        if (strncmp(line, "PPid:", 5) == 0)
        {
            sscanf(line + 5, "%d", &ppid);
            break;
        }
    }

    fclose(status_file_ptr);
    return ppid;
}

void psinittree(pid_t pid)
{
    printf("%d\n", pid);

    if (pid != INIT_PS_PID)
    {
        if (pid == -1)
            exit(GET_PARENT_PID_ERR);

        pid_t parent_pid = get_parent_pid(pid);
        psinittree(parent_pid);
    }

    return;
}

int main(int argc, char *argv[])
{
    if (argc != 2)
    {
        printf("Usage: %s <pid>\n", *argv);
        exit(INCORRECT_ARGS_ERR);
    }

    pid_t pid = (pid_t)atoi(argv[1]);
    psinittree(pid);
}
