    .data

input_addr:     .word 0x80
output_addr:    .word 0x84
stack_top:      .word 0x200
overflow_value: .word 0xCCCCCCCC

    .text
    .org 0x88

_start:
                                        ; init_stack(uint_16t* stack_top) {
    movea.l stack_top, A7               ;   A7 <-- address of stack_top
    movea.l (A7), A7                    ;   A7 <-- value at stack_top
                                        ; } 
    jsr     read_input
    jsr     check_ranges
    
    cmp.b   0, D1                       ; check if return value from check_ranges is 0
    bne     incorrect_input             ; if not => goto incorrect_input

    jsr     sum_even_n
    jsr     write_output

exit:
    halt

incorrect_input:
    move.l  -1, D0                      ; D0 <- -1 : return value in case input is incorrect
    jsr     write_output
    jmp     exit

read_input:
    movea.l input_addr, A0              ; A0 <- address of input device address
    movea.l (A0), A0                    ; A0 <- value at input_addr (0x80)

    move.l  (A0), D0                    ; D0 <- value from input device (MMIO)

    rts


check_ranges:
    move.l  0, D1                       ; D1 <- 0 : initialize return value from subroutine

    cmp.l   0, D0                       ; set SR (Status Register)
    ble     incorrect_range             ; if D0 <= 0 => goto incorrect_range
    jmp     check_ranges_exit

incorrect_range:
    move.b  1, D1                       ; D1 <- 1 : return value in case input is not in range

check_ranges_exit:
    rts


; D0 - input_value (N)
sum_even_n:
    link    A6, 12

    movea.l overflow_value, A0
    move.l  (A0), -12(A6)               ; stack_third <- overflow_value (0xCCCCCCCC)

    move.l  D0, -4(A6)                  ; stack_first <- N
    div.l   2, -4(A6)                   ; stack_first <- [N / 2] 

    move.l  -4(A6), -8(A6)              ; stack_second <- [N / 2]
    add.l   1, -8(A6)                   ; stack_second <- ([N / 2] + 1)
    mul.l   -4(A6), -8(A6)              ; stack_second <- [([N / 2] + 1) * (N / 2)]

    cmp.l   0, -8(A6)                   ; if [([N / 2] + 1) * (N / 2)] => goto overflow
    blt     overflow

    jmp     finish_sum_even_n

overflow:
    move.l  -12(A6), -8(A6)             ; stack_second <- stack_third (overflow_value)

finish_sum_even_n:
    move.l  -8(A6), D0                  ; D0 <- stack_second

    unlk    A6
    rts


write_output:
    movea.l output_addr, A0             ; A0 <- address of output device address
    movea.l (A0), A0                    ; A0 <- value at output_addr (0x84)

    move.l  D0, (A0)                    ; (A0) <- value from D0 (return value)

    rts
