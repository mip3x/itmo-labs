    .data

input_addr:     .word  0x80               ; Input address where the number 'n' is stored
output_addr:    .word  0x84               ; Output address where the result should be stored
n:              .word  0x00               ; Variable to store the number 'n'
temp:           .word  0x00               ; Temporary variable for calculations
const_1:        .word  0x01               ; Constant 1
const_2:        .word  0x02               ; Constant 2 for division
const_neg1:     .word  -1                 ; Constant -1 for error case

    .text

_start:
    load_ind     input_addr                ; acc = mem[mem[input_addr]]
    store        n                         ; mem[n] = acc

    ; Check if n <= 0 (should return -1)
    load         n                         ; acc = n
    beqz         invalid_input             ; if n == 0, jump to invalid_input
    ble          invalid_input             ; if n < 0, jump to invalid_input

    ; Calculate sum using formula: sum = n*(n+1)/2
    load         n                         ; acc = n
    add          const_1                   ; acc = n + 1
    bvs          overflow                  ; check for overflow
    store        temp                      ; temp = n + 1

    load         n                         ; acc = n
    mul          temp                      ; acc = n*(n+1)
    bvs          overflow                  ; check for overflow

    ; Division by 2
    div          const_2                   ; acc = n*(n+1)/2

    store_ind    output_addr               ; mem[mem[output_addr]] = result
    halt

invalid_input:
    load         const_neg1               ; acc = -1
    store_ind    output_addr              ; mem[mem[output_addr]] = -1
    halt

overflow:
    load_imm     0xCCCC_CCCC              ; overflow value
    store_ind    output_addr              ; mem[mem[output_addr]] = 0xCCCC_CCCC
    halt
