#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>

#define INCORRECT_ARGS_ERR 3

size_t get_file_content(const char* filename, char** string)
{
    FILE *file = fopen(filename, "rb");
    if (!file)
    {
        perror("fopen");
        return -1;
    }

    fseek(file, 0, SEEK_END);
    long fsize = ftell(file);
    fseek(file, 0, SEEK_SET);

    *string = malloc(fsize + 1);

    char* buf = NULL;
    size_t cap = 0;
    ssize_t len = getline(&buf, &cap, file);
    fclose(file);

    if (len < 0) {
        free(buf);
        perror("getline");
        return 0;
    }

    *string = buf;
    return (size_t)len;
}

size_t count_descendants(pid_t pid)
{
    char* children = NULL;

    char path[64];
    snprintf(path, sizeof(path), "/proc/%d/task/%d/children", pid, pid);

    size_t fsize = get_file_content(path, &children);
    if (!children || fsize == 0)
    {
        free(children);
        return 0;
    }

    char* s = children;
    size_t descendants = 0;
    while (*s)
    {
        char c = *s++;
        if (c == ' ')
        {
            descendants++;
            printf("\n");
        }
        else
            printf("%c", c);
    }

    free(children);

    return descendants;
}

int main(int argc, char* argv[])
{
    if (argc != 2)
    {
        printf("Usage: %s <pid>\n", argv[0]);
        exit(INCORRECT_ARGS_ERR);
    }

    pid_t pid = (pid_t)atoi(argv[1]);
    size_t descendants = count_descendants(pid);
    
    printf("%zu\n", descendants);
}
