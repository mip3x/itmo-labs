ORG 0x0
V0:  WORD $INT0, 0x180

ORG 0x10
set_counter_to_zero:
LD get_seconds
OUT 0
RET

X: WORD ?
get_seconds: WORD 0xF0

ORG 0x020
START:
	DI
   	CALL $set_counter_to_zero
    	LD #8
  	OUT 1
    	JUMP $PROG
PROG:
    	CLA
INCLP:
    	DI
    	LD X
    	INC
    	ST X
    	EI
    JUMP INCLP

ORG 0x40
INT0:
	LD X
   	ASL
   	OUT 2
   	CALL $set_counter_to_zero
   	IRET