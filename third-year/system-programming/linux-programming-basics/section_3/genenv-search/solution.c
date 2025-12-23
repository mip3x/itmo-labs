#include <dirent.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include <ctype.h>

#define GENENV "genenv"

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

bool s_contains_digits_only(const char *string)
{
    while (*string)
    {
        if (isdigit(*string++) == 0)
            return false;
    }

    return true;
}

int main(void)
{
    struct dirent **namelist;
    int n;

    n = scandir("/proc", &namelist, NULL, alphasort);
    if (n == -1)
    {
        perror("scandir");
        exit(EXIT_FAILURE);
    }

    int result = 0;
    while (n--)
    {
        const char *string = namelist[n]->d_name;
        if (s_contains_digits_only(string))
        {
            pid_t pid = (pid_t)atoi(string);
            const char *program_name = get_process_program_name(pid);

            if (strcmp(program_name, GENENV) == 0)
                result++;
        }
        free(namelist[n]);
    }
    free(namelist);

    printf("%d\n", result);

    exit(EXIT_SUCCESS);
}
