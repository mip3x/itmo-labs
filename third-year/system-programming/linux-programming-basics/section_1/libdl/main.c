#include <stddef.h>
#include <stdbool.h>
#include <stdio.h>
#include <dlfcn.h>

void (*hello_message)(const char *);

bool init_library() {
    void *handle = dlopen("./libHello.so", RTLD_LAZY);
    if (handle == NULL)
        return false;

    hello_message = (void (*)(const char*))dlsym(handle, "hello_message");

    if (hello_message == NULL)
        return false;

    return true;
}

int main() {
    if (init_library())
        hello_message("Vasya");
    else
        printf("Library was not loaded\n");

    return 0;
}
