    .data

input_addr:     .word 0x80
output_addr:    .word 0x84
stack_top:      .word 0x200

    .text
    .org 0x88

_start:
                                        ; init_stack(uint_16t* stack_top) {
    movea.l stack_top, A7               ;   A7 <-- address of stack_top
    movea.l (A7), A7                    ;   A7 <-- value at stack_top
                                        ; } 
    jsr read_input
    jsr check_ranges
    
    cmp.b   0, D1                       ; check if return value from check_ranges is 0
    bne incorrect_input                 ; if not => goto incorrect_input

    jsr sum_odd_n

exit:
    halt

incorrect_input:
    move.l -1, D0                       ; D0 <- -1 : return value in case input is incorrect
    jsr write_output
    jmp exit

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
    move.b  1, D1                       ; D1 <-- 1 : return value in case input is not in range

check_ranges_exit:
    rts

sum_odd_n:
    ;movea.l 100, A6
    link    A6, 8
    move.l  0, -4(A6)                    ; zeroing current_value
    move.l  0, -8(A6)                    ; zeroing result

    ;movea.l 0(A7), A1

    unlk    A6
    halt

    rts

write_output:
    movea.l output_addr, A0             ; A0 <- address of output device address
    movea.l (A0), A0                    ; A0 <- value at output_addr (0x84)

    move.l  D0, (A0)                    ; (A0) <- value from D0 (return value)

    rts
