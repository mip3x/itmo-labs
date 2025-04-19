    .data
input_addr:     .word  0x80
output_addr:    .word  0x84

    .text
    .org 0x85

_start:
    ; load n from input_addr
    lui     a0, %hi(input_addr)
    addi    a0, a0, %lo(input_addr)

    lw      a0, 0(a0)               ; a0 = input_addr
    lw      a0, 0(a0)               ; a0 = n

    addi    t0, zero, 1
    bgt     t0, a0, ret_minus_1     ; if 1 > n
    beq     t0, a0, ret_0           ; if n == 1

    addi    t0, zero, 2
    beq     t0, a0, ret_1

    rem     t1, a0, t0              ; if n % 2 == 0
    beq     t1, zero, ret_0

    ; call is_prime_recursive(n, i=3)
    addi    a1, zero, 3             ; a1 = i
    jal     ra, proc_is_prime_entry

    lui     t1, %hi(output_addr)
    addi    t1, t1, %lo(output_addr)
    lw      t1, 0(t1)
    sw      a0, 0(t1)
    halt

ret_1:
    addi    a0, zero, 1
    j       end

ret_0:
    addi    a0, zero, 0
    j       end

ret_minus_1:
    addi    a0, zero, -1

end:
    lui     t1, %hi(output_addr)
    addi    t1, t1, %lo(output_addr)
    lw      t1, 0(t1)               ; t1 = output_addr
    sw      a0, 0(t1)               ; output_addr (t1) <== a0
    halt

; result returned in a0
; n = a0, i = a1
proc_is_prime_entry:
    addi    sp, sp, -4
    sw      ra, 0(sp)

    ; if i > n / i => return 1
    div     t0, a0, a1              ; t0 = n / i
    ble     a1, t0, continue_check
    j       proc_is_prime_return_1

continue_check:
    ; if n % i == 0 => return 0
    rem     t1, a0, a1              ; t1 = n % i
    beq     t1, zero, proc_is_prime_return_0

    addi    a1, a1, 2               ; i += 2
    jal     ra, proc_is_prime_entry
    j       proc_is_prime_exit

proc_is_prime_return_1:
    addi    a0, zero, 1
    j       proc_is_prime_exit

proc_is_prime_return_0:
    addi    a0, zero, 0

proc_is_prime_exit:
    ; restore registers
    lw      ra, 0(sp)
    addi    sp, sp, 4
    jr      ra

