#include <stdio.h>
#include <string.h>
#include <sys/types.h>
#include <unistd.h>

pid_t get_parent_id(pid_t pid)
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

int main()
{
    pid_t parent_pid = get_parent_id(getpid());
    printf("%d\n", parent_pid);
}
