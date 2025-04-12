    .data

buffer:          .byte  '____________________'
input_addr:      .word  0x80
output_addr:     .word  0x84

q_str:           .byte  'What is your name?\n'
q_index:         .word  0
q_ptr:           .word  0
q_size:          .word  19

hello_str:       .byte  'Hello, '
excl_mark:       .byte  '!'
buf_sise:        .word  0

const_1:         .word  1
const_FF:        .word  0xFF
    

    .text

_start:
    load_imm     q_str
    store        q_ptr

    load         q_size
    store        q_index

while_q_msg:
    beqz         end

    load_ind     q_ptr
    and          const_FF
    store_ind    output_addr

    load         q_ptr
    add          const_1
    store        q_ptr

    load         q_index
    sub          const_1
    store        q_index

    jmp          while_q_msg

end:
    halt
