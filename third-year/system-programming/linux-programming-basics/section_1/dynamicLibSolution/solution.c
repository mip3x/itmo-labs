#include <stddef.h>
#include <stdio.h>

int stringStat(const char *string, size_t multiplier, int *count) {
    (*count)++;

    const char *str_ptr = string;
    size_t str_len = 0;

    for (;; str_ptr++, str_len++) {
        if (!*str_ptr) break;
        /*printf("%c", *str_ptr);*/
    }

    /*printf("\n");*/
    str_len *= multiplier;

    return (int)str_len;
}

/*int main() {*/
/*    const char *string = "libSolution string!";*/
/*    int count = 10;*/
/*    printf("string_len: %d\n", stringStat(string, 3, &count));*/
/*    printf("count: %d\n", count);*/
/*}*/
