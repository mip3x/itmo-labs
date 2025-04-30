    .data
input_addr:     .word 0x80
output_addr:    .word 0x84

input_value:    .word 0
output_value:   .word 0
byte_mask:      .word 0xFF

    .text
    .org    0x85
_start:
    lui     sp, 0x1
    jal     ra, main
    halt

main:
    addi    sp, sp, -4                      ; save caller-saved register RA
    sw      ra, 0(sp)

    jal     ra, read_input

    lui     a0, %hi(input_value)            ; a0 (1st func arg) <== input_value address
    addi    a0, a0, %lo(input_value)

    jal     ra, reverse_bytes

    lui     a0, %hi(output_value)           ; 1st func arg <== output value
    addi    a0, a0, %lo(output_value)
    lw      a0, 0(a0)

    lui     a1, %hi(output_addr)            ; 2nd func arg <== output address
    addi    a1, a1, %lo(output_addr)
    lw      a1, 0(a1)

    jal     ra, print_output

    lw      ra, 0(sp)                       ; restore RA
    addi    sp, sp, 4

    jr      ra

read_input:
    addi    sp, sp, -8
    sw      s2, 0(sp)
    sw      s3, 4(sp)                       ; handle callee-saved registers

    lui     s2, %hi(input_addr)             ; load address in memory
    addi    s2, s2, %lo(input_addr)
    lw      s2, 0(s2)                       ; load input_addr value

    lui     s3, %hi(input_value)
    addi    s3, s3, %lo(input_value)

    lw      s2, 0(s2)                       ; load value from MMIO
    sw      s2, 0(s3)                       ; store value to memory

    lw      s2, 0(sp)
    lw      s3, 4(sp)
    addi    sp, sp, 8

    jr      ra     

reverse_bytes:
prep:
    lui     t0, 0
    addi    t0, t0, 4

    lui     t2, %hi(byte_mask)
    addi    t2, t2, %lo(byte_mask)
    lw      t2, 0(t2)

    lui     t3, %hi(output_value)
    addi    t3, t3, %lo(output_value)

; a0 - input value address, t0 - counter, t1 - value, t2 - mask, t3 - output value address
loop:
    beqz    t0, end                         ; if counter == 0 => end
    lw      t1, 0(a0)                       ; load word from input value addr
    and     t1, t1, t2                      ; word & 0xFF => less significant byte

    add     t3, t3, t0                      ; output_value_addr += counter
    addi    t3, t3, -1                      ; output_value_addr -= 1
    sb      t1, 0(t3)                       ; memory[output_value_addr] <== input value
    addi    t3, t3, 1
    sub     t3, t3, t0

    addi    t0, t0, -1                      ; counter -= 1
    addi    a0, a0, 1                       ; input_value_addr += 1

    j       loop

end:
    jr      ra

; a0 - output value, a1 - output address
print_output:
    sw      a0, 0(a1)
    jr      ra
