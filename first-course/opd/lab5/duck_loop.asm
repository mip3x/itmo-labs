ORG 0x342
eol: WORD 0x0A
phrase: WORD 0x628
duck_count: WORD 0x004
V_SYMBOL: WORD 0x00F7
T_SYMBOL: WORD 0x00F4
QUIT_SYMBOL: WORD 0xFF00

START:
    CLA
    ask_for_symbol: IN 0x18
    CMP QUIT_SYMBOL
    BEQ exit
    CMP V_SYMBOL
    BEQ ask_for_symbol
    CMP T_SYMBOL
    BEQ print_duck
    JUMP ask_for_symbol
    print_duck: LD (phrase)+
	    CMP eol
	    BEQ duck_loop
	    OUT 0x10
	    SWAB
	    OUT 0x10
	    JUMP print_duck
    duck_loop: LOOP duck_count
    JUMP next_iter
    JUMP ask_for_symbol
    exit: HLT
    next_iter: LD phrase
    SUB #7
    ST phrase
    JUMP print_duck

ORG 0x627
WORD 0x6E24
WORD 0xFFFF
WORD 0x0D6D
WORD 0x0F0B
WORD 0x1C0E
WORD 0x0000
WORD 0x0A