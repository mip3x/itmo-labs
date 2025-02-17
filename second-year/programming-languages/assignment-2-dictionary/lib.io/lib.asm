%include "lib.inc"

section .text

%define NEW_LINE    0xA
%define SPACE       0x20
%define TAB         0x9
%define EOF         0x0
%define ASCII_PLUS  0x2b
%define ASCII_MINUS 0x2d
%define ASCII_ZERO  0x30
%define ASCII_NINE  0x39
 
%define SYS_READ    0
%define SYS_WRITE   1
%define SYS_EXIT    60

%define STDIN       0
%define STDOUT      1
%define STDERR      2
 
; Принимает код возврата и завершает текущий процесс
exit: 
    mov     rax, SYS_EXIT
    syscall

; Принимает указатель на нуль-терминированную строку, возвращает её длину
string_length:
    xor     rax, rax            ; %rax is used as a counter (bc its first argument to output),
                                ; so %rax should be zeroed
.loop:
    cmp byte [rdi + rax], EOF   ; check if the current symbol is null-terminator
    je .end                     ; jump to the end if symbol is null-terminator
    inc rax                     ; incrementing counter
    jmp .loop                   ; jump to the new cycle iteration (new symbol)
.end:
    ret

print_error:
    push    rdi                 ; push value from %rdi on top of the stack to comply
                                ; with stack alignment ABI conventions
                                ; and to save its value, bc %rdi is caller-saved register
                                ;
    call    string_length       ; find out string length
                                ;
    pop     rsi                 ; `push rdi, pop rsi` equals to `mov rsi, rdi`
                                ; stack is used bc of the alignment, despite
                                ; the fact that register operations are faster 
                                ; (in case using `mov` we need to save %rsi to follow conventions -> more actions)
    mov     rdx, rax            ; put string length -> %rdx
    mov     rax, SYS_WRITE      ; <write> syscall number -> %rax
    mov     rdi, STDERR         ; put fd (stderr -> 2) -> %rdi

    syscall
    ret

; Принимает указатель на нуль-терминированную строку, выводит её в stdout
print_string:
    push    rdi                 ; push value from %rdi on top of the stack to comply
                                ; with stack alignment ABI conventions
                                ; and to save its value, bc %rdi is caller-saved register
                                ;
    call    string_length       ; find out string length
                                ;
    pop     rsi                 ; `push rdi, pop rsi` equals to `mov rsi, rdi`
                                ; stack is used bc of the alignment, despite
                                ; the fact that register operations are faster 
                                ; (in case using `mov` we need to save %rsi to follow conventions -> more actions)
    mov     rdx, rax            ; put string length -> %rdx
    mov     rax, SYS_WRITE      ; <write> syscall number -> %rax
    mov     rdi, STDOUT         ; put fd (stdout -> 1) -> %rdi

    syscall
    ret

; Переводит строку (выводит символ с кодом 0xA)
print_newline:
    mov     rdi, NEW_LINE       ; '\n' symbol -> %rdi

; Принимает код символа и выводит его в stdout
print_char:
    push    rdi                 ; push received char (from %rdi) on top of the stack
    mov     rax, SYS_WRITE      ; <write> syscall number -> %rax
    mov     rdi, STDOUT         ; put fd (stdout -> 1) -> %rdi
    mov     rsi, rsp            ; put string pointer from stack (%rsp) -> %rsi
    mov     rdx, 1              ; put number of symbols to output: 1
    syscall
    pop     rdi                 ; pop received char
    ret

; Выводит знаковое 8-байтовое число в десятичном формате 
print_int:
    test    rdi, rdi            ; check if highest bit of %rdi =1 (equals SF is 1)
                                ; ~check if %rdi is negative
    jns     print_uint          ; if not, just print the number as unsigned
    push    rdi                 ; if yes, printing the '-' and after that printing the negative number
    mov     rdi, ASCII_MINUS
    call    print_char
    pop     rdi
    neg     rdi

; Выводит беззнаковое 8-байтовое число в десятичном формате 
; Совет: выделите место в стеке и храните там результаты деления
; Не забудьте перевести цифры в их ASCII коды.
print_uint:
    mov     rax, rsp            ; equals to commented instructions below (this takes more bytes and slower, used as alternative)
    xchg    rax, rdi            ; used article: https://dev.to/bartosz/assembly-code-size-optimization-tricks-2abd

    ;mov     rax, rdi           ; put %rdi -> %rax bc div needs divisible to store in %rax
    ;mov     rdi, rsp           ; current %rsp -> %rdi

    sub     rsp, 24             ; allocate space on stack for digits (24 bc of stack aligning: (ret addr: 0x...8) - 24(0x18) = 0x...0 - aligned)
    dec     rdi                 ; space for null-terminator
    mov     byte [rdi], EOF     ; null-terminator to the end of the string
    mov     r10, 10             ; saving divisor -> %r10 (temporary register)

.dividing_loop:
    xor     rdx, rdx            ; zeroing %rdx bc when rdx is not zeroed it is interpreted
                                ; as 1st part of 128-bits number RDX:RAX
    div     r10
    add     rdx, ASCII_ZERO     ; adding '0' (0x30 in ASCII) to transform number into ASCII character
    dec     rdi                 ; this and next commands put %dl into buffer on stack
    mov     [rdi], dl
    test    rax, rax
    jnz     .dividing_loop

.printing:
    call    print_string        ; printing string of ASCII symbols

.end:
    add     rsp, 24             ; recovery %rsp value
    ret

; Принимает два указателя на нуль-терминированные строки, возвращает 1 если они равны, 0 иначе
string_equals:
    xor     rax, rax            ; zeroing %rax as default value (~strings are not equal)
    xor     rcx, rcx            ; zeroing %rcx (counter)

.cmp_loop:
    mov     dl, [rdi + rcx]     ; put byte from 1st string
    cmp     dl, [rsi + rcx]     ; compare with byte from 2nd string
    jne     .exit
    test    dl, dl              ; check if EOL
    jz      .strings_are_equal
    inc     rcx                 ; incrementing counter
    jmp     .cmp_loop

.strings_are_equal:
    inc     rax                 ; =mov rax, 1

.exit:
    ret

; Читает один символ из stdin и возвращает его. Возвращает 0 если достигнут конец потока
read_char:
    sub     rsp, 8              ; allocate space on stack for byte
    mov     rax, SYS_READ
    mov     rdi, STDIN
    mov     rsi, rsp
    mov     rdx, 1              ; reading 1 symbol
    syscall
    test    rax, rax
    jz      .eof                ; <read> syscall doesnt save EOF in buffer
                                ; therefore, when receiving a value from the buffer (next instruction) the previous value will be returned
    lodsb                       ; put byte in %al as returning value

.eof:
    add     rsp, 8
    ret 

read_string:
    push    r12                 ; saving callee-saved registers
    push    r13
    push    r14
    xor     r14, r14            ; zeroing counter
    mov     r12, rdi            ; using callee-saved registers bc handling too many situations
    mov     r13, rsi            ; in case using caller-saved too many alignings need to be done -> more operations
                                ; (buf pointer is %r12) (buf size is %r13) (counter is %r14)
    call    read_char           ; read a char

.char_to_buf:
    cmp     r13, r14            ; compare buf size and counter
    jle     .failure            ; branch if overflow (buf size < current counter value)
    mov     [r12 + r14], al     ; write the read byte to the buffer
    inc     r14                 ; incrementing counter
    call    read_char           ; read a char
    cmp     al, NEW_LINE        ; test if NEW_LINE (NEW_LINE means EOF)
    je      .eof
    test    al, al              ; test if EOF
    jnz     .char_to_buf

.eof:
    mov     byte [r12 + r14], EOF
    mov     rdx, r14            ; word size (%r14) -> %rdx
    mov     rax, r12            ; buf pointer -> %rax
    jmp     .exit

.failure:
    xor     rdx, rdx            ; zeroing %rdx (length should be zero)
    xor     rax, rax            ; zeroing %rax

.exit:
    pop     r14                 ; restoring callee-saved registers
    pop     r13
    pop     r12
    ret


; Принимает: адрес начала буфера, размер буфера
; Читает в буфер слово из stdin, пропуская пробельные символы в начале, .
; Пробельные символы это пробел 0x20, табуляция 0x9 и перевод строки 0xA.
; Останавливается и возвращает 0 если слово слишком большое для буфера
; При успехе возвращает адрес буфера в rax, длину слова в rdx.
; При неудаче возвращает 0 в rax
; Эта функция должна дописывать к слову нуль-терминатор

read_word:
    push    r12                 ; saving callee-saved registers
    push    r13
    push    r14
    xor     r14, r14            ; zeroing counter
    mov     r12, rdi            ; using callee-saved registers bc handling too many situations
    mov     r13, rsi            ; in case using caller-saved too many alignings need to be done -> more operations
                                ; (buf pointer is %r12) (buf size is %r13) (counter is %r14)
.handle_whitespaces:
    call    read_char           ; read a char
    test    al, al              ; test if EOF
    jz      .failure            ; branch if yes
    cmp     al, SPACE
    je      .handle_whitespaces
    cmp     al, TAB
    je      .handle_whitespaces
    cmp     al, NEW_LINE
    je      .handle_whitespaces

.char_to_buf:
    cmp     r13, r14            ; compare buf size and counter
    jle     .failure            ; branch if overflow (buf size < current counter value)
    mov     [r12 + r14], al     ; write the read byte to the buffer
    inc     r14                 ; incrementing counter
    call    read_char           ; read a char
    cmp     al, SPACE
    je      .eof_or_eow
    cmp     al, TAB 
    je      .eof_or_eow
    cmp     al, NEW_LINE
    je      .eof_or_eow
    test    al, al              ; test if EOF
    jnz     .char_to_buf

.eof_or_eow:
    mov     byte [r12 + r14], EOF
    mov     rdx, r14            ; word size (%r14) -> %rdx
    mov     rax, r12            ; buf pointer -> %rax
    jmp     .exit

.failure:
    xor     rdx, rdx            ; zeroing %rdx (length should be zero)
    xor     rax, rax            ; zeroing %rax

.exit:
    pop     r14                 ; restoring callee-saved registers
    pop     r13
    pop     r12
    ret

 
; Принимает указатель на строку, пытается
; прочитать из её начала знаковое число.
; Если есть знак, пробелы между ним и числом не разрешены.
; Возвращает в rax: число, rdx : его длину в символах (включая знак, если он был) 
; rdx = 0 если число прочитать не удалось
parse_int:
    mov     cl, [rdi]           ; reading first byte
    cmp     cl, ASCII_MINUS
    je      .minus
    cmp     cl, ASCII_PLUS
    je      .plus
    jmp     parse_uint          ; if no sign, interpret as unsigned :D

.minus:
    push    -1                  ; push '-1' on stack as sign of minus
    jmp     .sign_not_empty

.plus:
    push    1                   ; push '1' on stack as sign of plus

.sign_not_empty:
    inc     rdi
    call    parse_uint
    pop     rcx
    test    rdx, rdx
    jz      .exit
    inc     rdx
    cmp     rcx, -1
    jne     .exit
    neg     rax

.exit:
    ret


; Принимает указатель на строку, пытается
; прочитать из её начала беззнаковое число.
; Возвращает в rax: число, rdx : его длину в символах
; rdx = 0 если число прочитать не удалось
parse_uint:
    xor     rax, rax
    xor     rdx, rdx            ; counter of symbols

.parse_loop:
    movzx   rcx, byte [rdi]     ; load byte from string with zero extension
    test    rcx, rcx            ; if EOF, branch to exit
    je      .exit

    cmp     rcx, ASCII_ZERO     ; if below ASCII (0x30 = '0') => branch to exit
    jl      .exit
    cmp     rcx, ASCII_NINE     ; if above ASCII (0x39 = '9') => branch to exit
    jg      .exit
    sub     rcx, ASCII_ZERO     ; ASCII number -> number

    lea     rax, [rax * 5]      ; %rax * 5 by lea (faster than mul)
    shl     rax, 1              ; %rax * 2 (by shift) => (%rax * 5) * 2 = %rax * 10
    add     rax, rcx

    inc     rdi
    inc     rdx                 ; increment counter
    jmp     .parse_loop

.exit:
    test    rdx, rdx
    cmovz   rax, rdx            ; if %rdx = 0 (means no number were read) => %rdx -> %rax
    ret


; Принимает указатель на строку, указатель на буфер и длину буфера
; Копирует строку в буфер
; Возвращает длину строки если она умещается в буфер, иначе 0
string_copy:
    xor     rcx, rcx            ; counter for string
    xor     rax, rax            ; temp register for storing value from string

.copy_loop:
    cmp     rcx, rdx            ; check if overflow (%rcx equals to %rdx and we know that %al is not `\0`)
    je      .failure            ; branch if overflow

    mov     al, [rdi]           ; load next byte -> %al
    mov     [rsi], al           ; put %al -> buffer
    inc     rdi
    inc     rsi
    inc     rcx
    test    al, al              ; test if `\0` in %al
    jnz     .copy_loop
    jmp     .exit

.failure:
    xor     rax, rax            ; return 0 if overflow

.exit:
    ret


