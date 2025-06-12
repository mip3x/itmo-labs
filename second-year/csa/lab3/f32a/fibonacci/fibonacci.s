    .data

input_addr:     .word 0x80
output_addr:    .word 0x84

    .text

_start:
    read_input
    fibonacci
    write_output
    end

read_input:
    \ /------------------- T <- input_addr
    \ |           /------- T -> A
    \ |           |  /---- [A] -> T
    \ v           v  v
    @p input_addr a! @
    ;

\ first value of ds is argument
fibonacci:
    dup 			    \ ds:[x:x]
    lit -2			    \ ds:[-2:x:x]
    +	    			\ ds:[x-2:x]
    -if fib_continue	\ ds:[x]  if (x >= 2) => fib_continue
    dup				    \ ds:[x:x]
    -if return_num		\ ds:[x]  if (x >= 0 && x < 2) => return_num
     
incorrect_range:
    lit -1			    \ ds:[-1]
    ;

return_num:
    ;

fib_continue:
    lit -2 +
    >r				    \ ds:[] rs:[x]
    lit 0 a!
    lit 1			    \ a = 0, ds:[1]

fib_loop:
    dup				    \ ds:[i:i]
    a				    \ ds:[a:i:i]
    +				    \ ds:[a+i:i]
    dup 		 	    \ ds:[a+i:a+i:i]
   -if c_fib_loop		\ ds:[a+i:i] if (a+i >= 0) => c_fib_loop
    
overflow:
    drop drop r> drop	\ clear stacks
    lit 0xCCCCCCCC
    fib_end ;

c_fib_loop:
    over			    \ ds:[i:a+i]
    a!				    \ a = i (b), ds:[a+i(b)]			    
    next fib_loop

fib_end:
    ;

write_output:
    \ /-------------------- T <- output_addr
    \ |            /------- T -> B
    \ |            |  /---- [A] -> T
    \ v            v  v
    @p output_addr b! !b
    ;

end:
    halt
