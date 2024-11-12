ORG 0x5A0
mas_first_elem: WORD 0x05B2
mas_cur_elem: WORD 0xA000
mas_length: WORD 0xE000
result: WORD 0xE000
START:
    CLA
    ST result
    LD #5
    ST mas_length
    LD mas_first_elem
    ST mas_cur_elem
    repeat: LD (mas_cur_elem)+
    ROR
    BCS skip_counting
    ROL
    ADC (result)+
    skip_counting: LOOP $mas_length
    JUMP repeat
    HLT

arr: WORD 0x75A4, 0xF200, 0xF100, 0x0801, 0x1101
