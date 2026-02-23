#include "kernel.h"
#include "common.h"

extern char __bss[], __bss_end[], __stack_top[];

struct cpu cpus[NCPU];

void display_sbi_impl_version() {
    struct sbiret result = sbi_call(0, 0, 0, 0, 0, 0, F_SBI_GET_IMPL_VERSION, BASE_EXTENSION);

    // https://raw.githubusercontent.com/riscv-software-src/opensbi/master/include/sbi/sbi_version.h
    uint32_t version = (uint32_t)result.value;
    uint32_t major = version >> 16;
    uint32_t minor = version & 0xFFFF;

    printf("SBI implementation version: %d.%d\n", major, minor);
}

void display_hart_status() {
    printf("Enter hart ID: ");
    long hart_id = getchar() - '0';
    printf("%d\n", hart_id);

    struct sbiret result = sbi_call(hart_id, 0, 0, 0, 0, 0, F_HART_GET_STATUS, EXTENSION_HART_MGMT);
    if (result.error != 0) {
        puts("Invalid entered hart id!");
        return;
    }

    // https://github.com/riscv-non-isa/riscv-sbi-doc/blob/master/src/ext-hsm.adoc
    printf("Hart status: %d\n", result.value);
}

void stop_hart() {
    uint32_t hart_id = mycpu()->hartid;
    printf("Stopping hart id #%d...\n", hart_id);

    sbi_call(0, 0, 0, 0, 0, 0, F_HART_STOP, EXTENSION_HART_MGMT);
}

void system_shutdown() {
    puts("Exiting system...");
    sbi_call(0, 0, 0, 0, 0, 0, 0, EXTENSION_SYSTEM_SHUTDOWN);
}

void display_menu() {
    puts("Choose 1 from Menu");
    puts("1) Get SBI implementation version");
    puts("2) Hart get status");
    puts("3) Hart stop");
    puts("4) System Shutdown");
    print_newline();
}

void handle_menu_choice() {
    long choice = getchar();
    if (choice < 0) {
        puts("Error occurred during reading symbol");
        return;
    }

    switch (choice) {
        case '1':
            display_sbi_impl_version();
            break;
        case '2':
            display_hart_status();
            break;
        case '3':
            stop_hart();
            break;
        case '4':
            system_shutdown();
            break;
        default:
            puts("Enter symbol from 1 to 4");
            break;
    }

    print_newline();
}

void kernel_main(void) {
    uint32_t hartid = 0;
    __asm__ __volatile__("mv %0, a0" : "=r"(hartid));
    __asm__ __volatile__("mv tp, %0" :: "r"(&cpus[hartid]));

    // memset is before saving hartid because cpus in .bss section
    memset(__bss, 0, (size_t) __bss_end - (size_t) __bss);

    cpus[hartid].hartid = hartid;
    printf("hart id #%d started\n", hartid);

    for (;;) {
        display_menu();
        handle_menu_choice();
    }
}

__attribute__((section(".text.boot")))
__attribute__((naked))
void boot(void) {
    __asm__ __volatile__(
        "la sp, __stack_top\n" // Устанавливаем указатель стека
        "j kernel_main\n"       // Переходим к функции main ядра
    );
}