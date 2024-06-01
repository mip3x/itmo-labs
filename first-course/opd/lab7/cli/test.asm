ORG 0x1D0
ARG1: WORD 0xFFFF
ARG2: WORD 0x78C2
ARG3: WORD 0xBE24

CHECK1: WORD 0x0
CHECK2: WORD 0x0
CHECK3: WORD 0x0
FINAL: WORD 0x0

CORRECT_RES1: WORD 0x7FFF
CORRECT_RES2: WORD 0x3C61
CORRECT_RES3: WORD 0x5F12

ORG 0x1E5
START:
    CLA
    CALL TEST1
    CALL TEST2
    CALL TEST3
    LD #0x1
    AND CHECK1
    AND CHECK2
    AND CHECK3
    ST FINAL
STOP: HLT

TEST1:
    LD ARG1
	CLC
    ; HLT
    WORD 0x0F03; 0F03 - команда SHR 
    BCC carry_is_0_first
    JUMP exit
    carry_is_0_first:
    CMP CORRECT_RES1
    BEQ set_number1_is_correct
    JUMP exit
    set_number1_is_correct: LD CHECK1
    INC
    ST CHECK1
    exit: RET

TEST2:
    LD ARG2
    CLC
    CMC
    ; HLT
    WORD 0x0F03; 0F03 - команда SHR 
    ; HLT
    BCS carry_is_1_second
    JUMP exit
    carry_is_1_second:
    CMP CORRECT_RES2
    BEQ set_number2_is_correct
    JUMP exit
    set_number2_is_correct: LD CHECK2
    INC
    ST CHECK2
    JUMP exit


TEST3:
    LD ARG3
    CLC
    ; HLT
    WORD 0x0F03; 0F03 - команда SHR 
    ; HLT
    BCC carry_is_0_third
    JUMP exit
    carry_is_0_third:
    CMP CORRECT_RES3
    BEQ set_number3_is_correct
    JUMP exit
    set_number3_is_correct: LD CHECK3
    INC
    ST CHECK3
    JUMP exit
