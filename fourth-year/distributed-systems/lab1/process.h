#pragma once

#include "ipc.h"

typedef struct {
    local_id id;
    unsigned int total_processes;
    int pipes[MAX_PROCESS_ID + 1][MAX_PROCESS_ID + 1][2];
} Process;