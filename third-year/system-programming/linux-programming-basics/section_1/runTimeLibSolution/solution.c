#include <stdlib.h>
#include <stdio.h>
#include <stdbool.h>
#include <dlfcn.h>

int (*function)(int);

bool init_library(char* library_name, char* function_name) {
    void* handle = dlopen(library_name, RTLD_LAZY);
    if (handle == NULL)
        return false;

    function = (int (*)(int))dlsym(handle, function_name);

    if (function == NULL)
        return false;

    return true;
}

int main(int argc, char** argv) {
    if (argc != 4) {
        printf("Need 4 args!\n");
        return 1;
    }

    char* library_name = argv[1];
    char* function_name = argv[2];
    int function_argument = atoi(argv[3]);

    if (init_library(library_name, function_name)) {
        int result = function(function_argument);
        printf("%d\n", result);
    }
    else
        printf("Library was not loaded\n");

    return 0;
}
