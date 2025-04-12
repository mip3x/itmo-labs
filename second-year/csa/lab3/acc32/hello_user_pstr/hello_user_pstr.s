    .data

pascal_str_len:  .byte  0
buf:             .byte  '_______________________________'
buf_size:        .word  0x1F

input_addr:      .word  0x80
output_addr:     .word  0x84

q_str:           .byte  'What is your name?\n'
q_size:          .word  19

hello_str:       .byte  'Hello, '
hello_size:      .word  7

excl_mark:       .word  '!'
excl_mark_size:  .word  1

index:           .word  0
ptr:             .word  0
buf_ptr:         .word  0

const_0:         .word  0
const_1:         .word  1
const_FF:        .word  0xFF
buf_mask:        .word  0x5F5F5F00
const_newline:   .word  0x0A


    .text
    .org 0x85

_start:
    load_imm     q_str
    store        ptr

    load         q_size
    store        index

while_q_msg:
    beqz         hello_msg_output

    load_ind     ptr
    and          const_FF
    store_ind    output_addr

    load         ptr
    add          const_1
    store        ptr

    load         index
    sub          const_1
    store        index

    jmp          while_q_msg

hello_msg_output:
    load_imm     buf
    store        buf_ptr

    load_imm     hello_str
    store        ptr

    load         hello_size
    store        index

while_hello_msg:
    beqz         read_line

    load_ind     ptr
    and          const_FF
    or           buf_mask
    store_ind    buf_ptr

    load         ptr
    add          const_1
    store        ptr

    load         buf_ptr
    add          const_1
    store        buf_ptr

    load         index
    sub          const_1
    store        index

    jmp          while_hello_msg

read_line:
    load         buf_size
    sub          hello_size
    sub          excl_mark_size
    store        index

while_read_line:
    beqz         output_excl_mark

    load_ind     input_addr

    sub          const_newline          ; check if symbol == '\n'
    beqz         output_excl_mark
    add          const_newline

    or           buf_mask
    store_ind    buf_ptr

    load         buf_ptr
    add          const_1
    store        buf_ptr

    load         index
    sub          const_1
    store        index

    jmp          while_read_line

output_excl_mark:
    load         excl_mark
    and          const_FF
    or           buf_mask
    store_ind    buf_ptr
    
    load         buf_ptr
    add          const_1
    store        buf_ptr

output_buf:
    load_imm     buf
    store        buf_ptr

    load         const_0
    store        index

while_output_buf:
    load         index
    add          const_1
    store        index

    load_ind     buf_ptr
    and          const_FF
    store_ind    output_addr

    sub          excl_mark
    beqz         calc_pascal_str_length
    add          excl_mark

    load         buf_ptr
    add          const_1
    store        buf_ptr

    jmp          while_output_buf

calc_pascal_str_length:
    load        pascal_str_len
    add         index
    store       pascal_str_len

end:
    halt
