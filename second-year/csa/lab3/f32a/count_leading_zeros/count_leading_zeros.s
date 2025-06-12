    .data

input_addr:     .word 0x80
output_addr:    .word 0x84
mask:           .word 0x80000000

    .text

_start:
    read_input
    check_0
    enable_eam
    count_leading_zeros
    write_output

read_input:
    \ /------------------- T <- input_addr
    \ |           /------- T -> A
    \ |           |  /---- [A] -> T
    \ v           v  v
    @p input_addr a! @
    ;

check_0:
    dup                 \ DS: x:x
    if load32
    ;

load32:
    drop                \ DS: [] - empty
    lit 32              \ DS: 32
    write_output ;

enable_eam:
    lit 1 eam
    ;

count_leading_zeros:
    lit 32 >r           \ counter (32 bits) > R
    lit 0               \ result DS: result:x
    over                \ DS: x:result

count_leading_zeros_loop:
    dup                 \ DS: x:x:result
    @p mask             \ DS: mask:x:x:result
    and                 \ DS: mask&x:x:result
    if left_shift       \ if (mask&x == 0) ==> left_shift; DS: x:result
    exit ;

left_shift:
    2*                  \ DS: x<<1:result
    over                \ DS: result:x<<1
    lit 1               \ DS: 1:result:x<<1
    +                   \ DS: result+1:x<<1
    over                \ DS: x<<1:result+1
    next count_leading_zeros_loop

exit:
    drop                \ DS: result
    r>
    drop
    ;

write_output:
    \ /-------------------- T <- output_addr
    \ |            /------- T -> B
    \ |            |  /---- [B] <- T
    \ v            v  v
    @p output_addr b! !b

end:
    halt
