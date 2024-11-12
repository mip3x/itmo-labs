ORG 0x0
V0:	WORD $DEFAULT, 0x180
V1:	WORD $INT1, 0x180
V2:	WORD $INT2, 0x180
V3:	WORD $DEFAULT, 0x180
V4:	WORD $DEFAULT, 0x180
V5:	WORD $DEFAULT, 0x180
V6:	WORD $DEFAULT, 0x180
V7:	WORD $DEFAULT, 0x180
DEFAULT:	IRET

ORG 0x033
X:		WORD ?
MAX:	WORD 0x0018; 24 - максимальное значение X
MIN: 	WORD 0xFFE6; -26 - минимальное значение X

ORG 0x040
START:
	DI
	CLA 
	OUT 0x1; запрет прерываний для неиспользуемых ВУ
	OUT 0x7
	OUT 0xB
	OUT 0xD
	OUT 0x11
	OUT 0x15
	OUT 0x19
	OUT 0x1D
	LD #9; разрешить прерывания и вектор №1
	OUT 3; (1001) в MR КВУ-1
	LD #0xA; резрешить прерывания и вектор №2
	OUT 5; (1010) в MR КВУ-2
	JUMP prog

prog: 
	EI
	CLA

ORG 0x060
main: 
	DI
	LD X
	INC
	INC
	CALL $check
	ST X
	EI
	JUMP main

check:
	CMP MAX
	BMI check_min
	JUMP load_min
check_min: 
	CMP MIN
	BPL return
load_min: 
	LD MIN
return: 
	RET

ORG 0x070
INT1:
	NOP
	LD X
	NEG
	ASL
	ASL
	SUB X
	SUB #4
	OUT 2
	NOP
	IRET

ORG 0x080
INT2:	
	PUSH
	NOP
	IN 4
	OR X
	ST X
	NOP
	POP
	IRET
