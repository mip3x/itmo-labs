ORG 0x342
eol: WORD 0x0A
phrase: WORD 0x628

START:
    CLA
    return: LD (phrase)+
    CMP eol
	BEQ exit
	OUT 2
	SWAB
	OUT 2
	JUMP return
	exit: HLT

ORG 0x628
WORD 0x041B
WORD 0x0443
WORD 0x041D
WORD 0x0410
WORD 0x0A
