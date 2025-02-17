global find_word
extern string_equals

find_word:
    push    rdi             ; save key (bc string_equals could change $rdi and $rsi)
    push    rsi             ; save label_name
    lea     rsi, [rsi + 8]  ; skip next node in dict address (dq _next_node_key_)
    call    string_equals   ; check if key and entered string are equal
    pop     rsi             ; restore label_name
    pop     rdi             ; restore key
    test    rax, rax        ; string_equals return result in $rax
                            ; if $rax == 1 (means jnz) -> strings are equal
    jnz     .return_value   ; then return this value
    mov     rsi, [rsi]      ; go to the next key in dictionary (load dq _next_node_key_ value)
    test    rsi, rsi        ; check if $rsi is zero (means the last elem of dict)
    jnz     find_word       ; if $rsi != 0 -> jmp to new iteration of cycle

.return_value:
    mov     rax, rsi

.exit:
    ret
