    .data

input_addr:     .word 0x80
output_addr:    .word 0x84

    .text

_start:
    read_input
    reverse_bits
    write_output
    end

read_input:
    \ /------------------- T <- input_addr
    \ |           /------- T -> A
    \ |           |  /---- [A] -> T
    \ v           v  v
    @p input_addr a! @
    ;

reverse_bits:
    lit 0                   \ result
    lit 31 >r               \ counter (32 bits) -> R

reverse_loop:
    2*                      \ result <<= 1
    over                    \ res:n -> n:res
    dup                     \ n:n:res
    lit 1 and               \ n&1:n:res
    over                    \ n:n&1:res
    a!                      \ a: n ds: n&1:res
    +                       \ res = res | (n&1) ds:(res|(n&1))
    a                       \ a: n -> ds: n:(res|(n&1))
    2/                      \ ds: n>>1:(res|(n&1))
    over                    \ ds: (res|(n&1)):n>>1
    next reverse_loop
    ;

write_output:
    \ /-------------------- T <- output_addr
    \ |            /------- T -> B
    \ |            |  /---- [B] <- T
    \ v            v  v
    @p output_addr b! !b
    ;

end:
    halt
