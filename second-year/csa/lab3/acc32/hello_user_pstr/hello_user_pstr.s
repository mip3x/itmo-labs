    .data

buf:             .byte  '________________________________'
buf_size:        .word  0x20

input_addr:      .word  0x80
output_addr:     .word  0x84

q_str:           .byte  'What is your name?\n'
q_size:          .word  19

hello_str:       .byte  'Hello, '
hello_size:      .word  7

excl_mark:       .byte  '!'
excl_mark_size:  .word  1

index:           .word  0
ptr:             .word  0
buf_ptr:         .word  0

const_0:         .word  0
const_1:         .word  1
const_FF:        .word  0xFF
buf_mask:        .word  0x5C5C5C00

    .text

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
    add          const_1        ; skip pascal length
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

end:
    halt
