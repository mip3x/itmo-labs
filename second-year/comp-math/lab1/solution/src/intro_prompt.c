#include <stdlib.h>
#include <stdio.h>

#include "../include/intro_prompt.h"

void print_intro_prompt() {
    printf("Вычислительная математика. Лабораторная работа №1\n");

    char* st_name = getenv(ST_NAME_ENV);
    if (st_name) printf("Имя: %s\n", st_name);
    else printf("Имя: %s\n", DEFAULT_ST_NAME);
}

