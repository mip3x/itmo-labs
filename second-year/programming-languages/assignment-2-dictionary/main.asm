%include "lib.inc"
%include "dict.inc"

%define BUF_SIZE 256
%define NEW_LINE 10

section .data
%include "words.inc"

section .bss
buffer: resb BUF_SIZE

section .rodata
hello_msg: db "Enter dictionary key (the length must not exceed 255 characters!)", NEW_LINE, "> ", 0
length_error_msg: db "The length of the string should be no more than 255 characters and it shouldn't be empty!", 0
not_found_error_msg: db "There is no such key in dictionary!", 0
attempt_log_msg: db "Trying to find word...", 0
success_log_msg: db "Key found in dictionary. Key value:", 0

section .text

global _start

_start:
    mov     rdi, hello_msg
    call    print_string            ; printing out hello message

.read_string:
    mov     rdi, buffer             ; read string from buffer with max size of BUF_SIZE
    mov     rsi, BUF_SIZE
    call    read_string

    test    rax, rax                ; if $rax == 0 -> string was not read
    jnz     .find_word              ; if $rax != 0 -> string was read -> trying to find matching key
    mov     rdi, length_error_msg   ; printing out error msg if string was not read
    call    print_error
    jmp     .exit

.find_word:
    call    print_newline
    mov     rdi, attempt_log_msg
    call    print_string
    call    print_newline
    mov     rdi, buffer             ; trying to find matching key in dict
    mov     rsi, DICT_START
    call    find_word
    test    rax, rax
    jnz     .print_value
    mov     rdi, not_found_error_msg
    call    print_error
    mov     rdi, 1

.exit:
    call    exit

.print_value:
    mov     rbx, rax
    mov     rdi, success_log_msg
    call    print_string
    call    print_newline
    mov     rdi, rbx
    lea     rdi, [rdi + 8]          ; skip 8-byte pointer
    call    string_length           ; finding the length of key
    lea     rdi, [rdi + rax + 1]    ; start of the key value -> $rdi
    call    print_string
    xor     rdi, rdi
    jmp     .exit
