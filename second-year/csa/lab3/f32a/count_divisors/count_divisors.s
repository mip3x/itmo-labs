    .data

input_addr:     .word 0x80
output_addr:    .word 0x84
divisible:      .word 0
divisor:        .word 0

    .text
    .org 0x88

_start:
    read_input
    check_ranges
    count_divisors_prep
    write_output

read_input:
    \ /------------------- T <- input_addr
    \ |           /------- T -> A
    \ |           |  /---- [A] -> T
    \ v           v  v
    @p input_addr a! @
    !p divisible
    ;

check_ranges:
    @p divisible dup    \ [x:x]
    if x_not_in_range   \ [x]
    dup                 \ [x:x]
    -if x_is_positive   \ [x]

x_not_in_range:
    drop                \ []
    lit -1              \ [-1]
    write_output ;

x_is_positive:
    drop
    ;

count_divisors_prep:
    count_divisors
    r>                  \ T <- divisors_counter
    ;

count_divisors:
    lit 0 >r            \ R <- divisors_counter
    lit divisor b!      \ b <- &divisor
    main_loop ;

main_loop:
    @p divisible a!     \ a <- divisible
    lit 0
    dup
    lit 31 >r

division_step:
    +/                  \ dividend - A; divisor - [B]; quotient - T; remainder - S
    next division_step
    drop                \ drop quotient
    if div              \ if remainder == 0 => divisor / divisible -> +1

not_div:
    continue ;

div:
    r> lit 1 + >r

continue:
    @p divisor
    inv lit 1 +        \ ds:[-divisor]
    @p divisible
    +
    if end             \ if (divisible == divisor) ==> end
    @p divisor lit 1 + 
    !p divisor         \ divisor++
    main_loop ;

end:
    r> r>              \ rs:[counter,return_addr]
    over
    >r >r              \ rs:[return_addr,counter]
    ;

write_output:
    \ /-------------------- T <- output_addr
    \ |            /------- T -> B
    \ |            |  /---- [B] -> T
    \ v            v  v
    @p output_addr b! !b
   
exit:
    r>                 \ drop return addr (clear return stack)
    halt
