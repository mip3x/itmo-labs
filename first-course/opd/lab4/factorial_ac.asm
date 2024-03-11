ORG 0x200
data: WORD 0x003
start:
    CLA
    LD data
    CALL $factorial
    POP
    ST data
    HLT
 
ORG 0x300
factorial: ; f(x): return 1 if x == 1 else return (x * f(x - 1))
    CMP one
    BEQ return
    PUSH; пуш множимого
    DEC; получаем множитель
    PUSH; пуш множителя, чтобы получить от него факториал
    LD &0
    CALL $factorial
    CLA; очищаем АС для результата
    PUSH; пуш результата на стек
    CALL $mult
    ST &4
    POP
    POP
    POP
    return: RET
    one: WORD 0x001

ORG 0x400
shifts: WORD 0x001
mult:
    repeat: LD &3; загрузка множителя
    BEQ exit; если множитель == 0, то выход
    ASR; сдвиг множителя
    ST &3; выгрузка множителя на стэк
    LD shifts; загрузка необходимого количества сдвигов
    BCC shifts_incr; переход к след. итерации, если не нужно производить операции над множимым
    PUSH; пуш количества сдвигов (тк они будут использоваться в цикле)
    LD &3; загрузка множимого
    shift_iter: LOOP $shifts; цикл на shifts (количество сдвигов)
    JUMP shift_left
    ADD &2; сложение с результатом
    ST &2; загрузка сложенного в результат
    POP; выгрузка количества сдвигов
    shifts_incr: INC; увеличение количества сдвигов
    ST shifts; выгрузка количества сдвигов
    JUMP repeat
    shift_left: ASL; сдвиг множимого
    JUMP shift_iter
    exit: LD #1
    ST shifts
    LD &1
    RET
