    .data
input_addr:     .word 0x80
output_addr:    .word 0x84

    .text
    .org 0x88

_start:
    jal         ra, initialize_stack
    jal         ra, main
    jal         ra, print_res
    halt

initialize_stack:
    lui         sp, 0x1 
    jr          ra

main:
    addi        sp, sp, -4                      ; allocate space on stack for caller-saved registers
    sw          ra, 0(sp)                       ; write RA (caller-saved register) on stack

    lui         a0, %hi(input_addr)
    addi        a0, a0, %lo(input_addr)
    lw          a0, 0(a0)                       ; a0 <-- input_addr
    lw          a0, 0(a0)                       ; a0 <-- mem[input_addr]

    jal         ra, is_prime 

    lw          ra, 0(sp)                       ; restore caller-saved registers
    addi        sp, sp, 4
    jr          ra

is_prime:
    ; func_arguments: a0 - maybe prime number
bounds_checking:
    addi        t0, zero, 1
    bgt         t0, a0, out_of_bounds           ; if n < 1 ==> out of bounds
    beq         t0, a0, not_prime               ; if n == 1 ==> not prime

    addi        t0, t0, 1                       ; t0 = 2
    beq         t0, a0, prime                   ; if n == 2 ==> prime

is_prime_loop:
    rem         t1, a0, t0
    beqz        t1, not_prime                   ; if n % t0 == 0 ==> not prime
    mul         t1, t0, t0
    bleu        a0, t1, prime                   ; if n < t0 ^ 2 ==> prime
    addi        t0, t0, 1
    j           is_prime_loop

out_of_bounds:
    addi        a0, zero, -1
    j           is_prime_end

prime:
    addi        a0, zero, 1
    j           is_prime_end

not_prime:
    xor         a0, a0, a0
    j           is_prime_end

is_prime_end:
    jr          ra

print_res:
    lui         t0, %hi(output_addr)
    addi        t0, t0, %lo(output_addr)
    lw          t0, 0(t0)
    sw          a0, 0(t0)
    jr          ra
