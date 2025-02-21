%macro repeat_string 2
    %define %%temp_str ""
    %rep %2
        %strcat %%temp_str %%temp_str %1
    %endrep
    db %%temp_str, 0
%endmacro
