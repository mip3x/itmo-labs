    .data
buffer:          .byte  'Hello, _________________________'
greet:           .byte  'What is your name?\n'
    
    .text
    .org 0x88

_start:
    lui      sp, 0x1			                   ; initialize stack pointer with 0x1000
    jal	     ra, main
    halt

main:
    addi     sp, sp, -24                      ; save caller-saved registers a1-a6
    sw       a1, 0(sp)
    sw       a2, 4(sp)
    sw       a3, 8(sp)
    sw       a4, 12(sp)
    sw       a5, 16(sp)
    sw       a6, 20(sp)

    addi     s2, s2, 0x21		                 ; s2 <- exclamation mark
    lui      s3, %hi(0xFFFF0000)
    addi     s3, s3, %lo(0xFFFF0000)         ; s3 <- zero-byte mask

    lui      a0, %hi(greet)
    addi     a0, a0, %lo(greet)
    mv       s1, a0			                     ; s1 <- &greet

    addi     sp, sp, -4                      ; save value from register 'ra' on the stack
    sw       ra, 0(sp)
    jal      ra, strlen
    lw       ra, 0(sp)                       ; restore value from stack to 'ra'
    addi     sp, sp, 4

    mv       a1, a0			                     ; a1 <- strlen(greet)
    mv       a0, s1			                     ; a0 <- &greet

    addi     sp, sp, -4                      ; save value from register 'ra' on the stack
    sw       ra, 0(sp)
    jal      ra, write                       ; write(&greet, strlen(greet))
    lw       ra, 0(sp)                       ; restore value from stack to 'ra'
    addi     sp, sp, 4

    lui	     a0, %hi(buffer)
    addi     a0, a0, %lo(buffer)
    addi     a0, a0, 7
    addi     a1, zero, 24

    addi     sp, sp, -4                      ; save value from register 'ra' on the stack
    sw       ra, 0(sp)
    jal      ra, readline                    ; readline(&(buffer + 7), bytes_counter)
    lw       ra, 0(sp)                       ; restore value from stack to 'ra'
    addi     sp, sp, 4

    addi     a0, a0, -23                     ; is there space for exclamation mark?
    beqz     a0, overflow                    ; no -> post 0xCC and die

    mv       a0, zero

    addi     sp, sp, -4                      ; save value from register 'ra' on the stack
    sw       ra, 0(sp)
    jal      ra, strlen
    lw       ra, 0(sp)                       ; restore value from stack to 'ra'
    addi     sp, sp, 4

    mv       a1, a0
    lw       a0, 0(a1)
    and      a0, a0, s3
    or       a0, a0, s2
    sw       a0, 0(a1)                       ; append(buffer, "!");

    mv       a0, zero

    addi     sp, sp, -4                      ; save value from register 'ra' on the stack
    sw       ra, 0(sp)
    jal      ra, strlen
    lw       ra, 0(sp)                       ; restore value from stack to 'ra'
    addi     sp, sp, 4

    mv       a1, a0
    mv       a0, zero

    addi     sp, sp, -4                      ; save value from register 'ra' on the stack
    sw       ra, 0(sp)
    jal      ra, write                       ; write(buffer, strlen(buffer))
    lw       ra, 0(sp)                       ; restore value from stack to 'ra'
    addi     sp, sp, 4
     
restore_registers:
    lw       a1, 0(sp)
    lw       a2, 4(sp)                       ; restore caller-saved registers a1-a6
    lw       a3, 8(sp)
    lw       a4, 12(sp)
    lw       a5, 16(sp)
    lw       a6, 20(sp)
    addi     sp, sp, 24
    jr       ra


overflow:
    lui      a0, %hi(0xCCCCCCCC)
    addi     a0, a0, %lo(0xCCCCCCCC)
    sw       a0, 0x84(zero)
    j	     restore_registers

; a0 - &greet
strlen:
    addi     t0, zero, 0xFF                  ; t0 = 0xFF (byte mask)
    mv       a1, zero			                   ; counter

strlen_loop:
    add      a2, a0, a1
    lw       a2, 0(a2)
    and      a2, a2, t0			                 ; input & 0xFF
    addi     a1, a1, 1			                 ; counter++
    bnez     a2, strlen_loop                 ; run until zero-byte is met
    addi     a0, a1, -1                      ; exclude zero-byte from final result
    jr       ra

; a0 - &greet, a1 - strlen(greet)
write:
    addi     t0, zero, 0xFF                  ; t0 = 0xFF (byte mask)
    beqz     a1, write_end                   ; exit if length == 0

write_loop:
    lw       a2, 0(a0)                       ; load word
    and      a2, a2, t0                      ; extract byte
    sw       a2, 0x84(zero)                  ; output byte
    addi     a0, a0, 1                       ; next byte
    addi     a1, a1, -1                      ; decrement length
    bnez a1, write_loop                      ; loop if more bytes

write_end:
    jr ra                                    ; return


; a0 - &(buffer + 7), a1 - bytes_counter
readline:
    addi     t0, zero, 0xFF                  ; byte mask
    lui      t1, %hi(0xFFFFFF00)
    addi     t1, t1, %lo(0xFFFFFF00)         ; non-destructive byte store mask

    addi     a2, a1, -1
    beqz     a2, readline_end                ; fail if there is only space for zero byte
    mv       a1, zero                        ; total bytes written
    mv       a3, zero
    addi     a3, a3, 0xA                     ; LF character

readline_loop:
    lw       a4, 0x80(zero)                  ; get last dword from IO port
    and      a5, a4, t0                      ; mask with 0xFF

    beq      a5, a3, readline_lf             ; if dword == \n => goto readline_lf
    add      a5, a0, a1                      ; calculate current byte to set
    addi     a1, a1, 1                       ; bytes_counter++

    lw       a6, 0(a5)			                 ; a6 = [calculated_address]
    and      a6, a6, t1			                 ; a6 = a6 & 0xFFFFFF00
    or       a4, a6, a4			                 ; a6 = a6 | a4

    sw       a4, 0(a5)                       ; store the byte in buffer, retaining higher ones
    bne      a2, a1, readline_loop           ; continue if buffer has enough space
    mv       a1, a2

readline_lf:
    add      a0, a0, a1
    lw       a2, 0(a0)
    and      a2, a2, t1
    sw       a2, 0(a0)                       ; append zero-byte to output
    mv       a0, a1
    jr       ra                              ; return total bytes written

readline_end:
    lw       a2, 0(a0)
    and      a2, a2, t1
    sw       a2, 0(a0)
    mv       a0, zero
    jr       ra
