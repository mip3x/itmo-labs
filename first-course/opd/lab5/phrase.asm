ORG 0x342
eol: WORD 0x0A
phrase: WORD 0x628

START:
    CLA
    ask_for_output: IN 3
    AND #0x40
    BEQ ask_for_output
    LD (phrase)+
    CMP eol
    BEQ exit
    OUT 2
    JUMP ask_for_output
    exit: HLT

ORG 0x628
WORD 0x00B1
WORD 0x00CD
WORD 0x00B2
WORD 0x00BC
WORD 0x0A