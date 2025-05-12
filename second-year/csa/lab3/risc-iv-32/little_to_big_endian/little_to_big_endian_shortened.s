    .data
input_addr:     .word 0x80
output_addr:    .word 0x84

    .text

_start:
    lui     sp, 0x1
    jal     ra, main
    halt

main:
    addi    sp, sp, -4
    sw      ra, 0(sp)

    jal     ra, read_and_reverse

    lw      ra, 0(sp)
    addi    sp, sp, 4

    jr      ra

read_and_reverse:
    lui     t0, %hi(input_addr)
    addi    t0, t0, %lo(input_addr)
    lw      t0, 0(t0)         ; t0 = input_addr
    lw      a0, 0(t0)         ; a0 = M[t0]

    addi    t1, zero, 4       ; bytes counter
    addi    t2, zero, 255     ; 0xFF mask
    addi    t3, zero, 0       ; result
    addi    t5, zero, 8       ; shifts counter

reverse_loop:
    beqz    t1, done          ; no more bytes --> done
    addi    t1, t1, -1
    and     t4, a0, t2        ; t4 = lowest byte

    sll     t3, t3, t5        ; <<= 8
    or      t3, t3, t4        ; |= byte
    srl     a0, a0, t5        ; >>= 8
    j       reverse_loop

done:
    add     a0, t3, zero      ; move result to a0

    lui     t0, %hi(output_addr)
    addi    t0, t0, %lo(output_addr)
    lw      t0, 0(t0)         ; t0 = output_addr
    sw      a0, 0(t0)

    jr      ra
