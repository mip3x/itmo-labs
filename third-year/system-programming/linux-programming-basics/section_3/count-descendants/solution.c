#include <ctype.h>
#include <stddef.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <stdio.h>
#include <stdbool.h>
#include <dirent.h>

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
        printf("[%s]", path);
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

bool is_descendant(pid_t pid, pid_t ancestor)
{
    /*printf("calling is_descendant: (pid: %d, ancestor: %d)\n", pid, ancestor);*/

    if (pid == -1)
        exit(GET_PARENT_PID_ERR);
    if (pid == 0)
        return false;

    if (pid != INIT_PS_PID)
    {
        pid_t parent_pid = get_parent_pid(pid);

        if (parent_pid == ancestor)
            return true;

        return is_descendant(parent_pid, ancestor);
    }

    return false;
}

bool s_contains_digits_only(const char *string)
{
    while (*string)
    {
        if (isdigit(*string++) == 0)
            return false;
    }

    return true;
}

size_t count_descendants(pid_t ancestor)
{
    struct dirent **namelist;
    int n;

    n = scandir("/proc", &namelist, NULL, alphasort);
    if (n == -1)
    {
        perror("scandir");
        exit(EXIT_FAILURE);
    }

    size_t result = 0;
    while (n--)
    {
        const char *string = namelist[n]->d_name;
        if (s_contains_digits_only(string))
        {
            pid_t pid = (pid_t)atoi(string);
            
            /*printf("checking if %d is descendant of %d\n", pid, ancestor);*/

            if (pid == ancestor)
            {
                result++;
                continue;
            }

            if (is_descendant(pid, ancestor) == true)
            {
                /*printf("%d is descendant of %d\n", pid, ancestor);*/
                result++;
            }
        }
        free(namelist[n]);
    }
    free(namelist);

    return result;
}

int main(int argc, char *argv[])
{
    if (argc != 2)
    {
        printf("Usage: %s <pid>\n", *argv);
        exit(INCORRECT_ARGS_ERR);
    }

    pid_t pid = (pid_t)atoi(argv[1]);
    size_t descendants = count_descendants(pid);

    printf("%zu\n", descendants);
}
