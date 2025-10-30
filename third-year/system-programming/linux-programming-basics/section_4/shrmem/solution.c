#include <stdio.h>
#include <stdlib.h>
#include <sys/ipc.h>
#include <sys/types.h>
#include <sys/shm.h>

#define SHM_REG_SIZE 1000

int main(int argc, char **argv) {
    if (argc != 3) {
        printf("Expected 2 args\n");
        return 1;
    }

    key_t reg1_key = (key_t)atoi(argv[1]);
    key_t reg2_key = (key_t)atoi(argv[2]);

    int reg1_shmid = shmget(reg1_key, SHM_REG_SIZE, 0);
    int reg2_shmid = shmget(reg2_key, SHM_REG_SIZE, 0);

    int *array1;
    if ((array1 = shmat(reg1_shmid, NULL, 0)) == (void*)(-1)) {
        printf("Cannot attach shared memory reg1\n");
        return 1;
    }

    int *array2;
    if ((array2 = shmat(reg2_shmid, NULL, 0)) == (void*)(-1)) {
        printf("Cannot attach shared memory reg2\n");
        return 1;
    }
    
    key_t result_key;
    if ((result_key = ftok(argv[0], 0)) < 0) {
        printf("Cannot generate key\n");
        return 1;
    }

    int *result_array;
    int reg_result_shmid;
    if ((reg_result_shmid = shmget(result_key, SHM_REG_SIZE, 0666 |
                                   IPC_CREAT |
                                   IPC_EXCL)) < 0) {
        printf("Cannot create shared memory\n");
        return 1;
    }

    if ((result_array = (int*)shmat(reg_result_shmid, NULL, 0)) == (int*)(-1)) {
        printf("Cannot attach shared memory reg result\n");
        return 1;
    }

    for (size_t i = 0; i < 100; i++) {
        result_array[i] = array1[i] + array2[i];
    }

    
    /*for (size_t i = 0; i < 100; i++) {*/
    /*    printf("%zu: %d\n", i, result_array[i]);*/
    /*}*/

    if (shmdt(array1) < 0) {
        printf("Cannot detach shared memory reg1\n");
        return 1;
    }
    if (shmdt(array2) < 0) {
        printf("Cannot detach shared memory reg2\n");
        return 1;
    }
    if (shmdt(result_array) < 0) {
        printf("Cannot detach shared memory reg result\n");
        return 1;
    }

    printf("%d\n", result_key);
}
