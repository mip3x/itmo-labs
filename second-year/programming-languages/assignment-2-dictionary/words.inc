%include "colon.inc"
%include "repeat_string.inc"

%define DICT_START key1

colon 0xa, key12
db "new line value", 0

colon "", key11
db "[ДАННЫЕ УДАЛЕНЫ]", 0

colon "  ", key10
db "тут одни пробелы", 0

colon " русске??? ты?", key9
db "текст на русском о нет...(о как??)", 0

colon "test", ke28374
repeat_string "a", 20

colon "very very long", key8
repeat_string "e", 300

colon "tab      ", key7
db "tab value", 0

colon "  k ey6  ", key6
db "value #6", 0

colon "key number fifth", key5
db "fifth value", 0

colon "fourth node", key4
db "fourth value", 0

colon "long", key3
db "third value is very longlonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglong", 0

colon "meow node", key2
db "meow value", 0

colon "first node", key1
db "first node value", 0
