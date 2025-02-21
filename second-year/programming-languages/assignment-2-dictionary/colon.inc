%define next_node 0

%macro colon 2
    %2: 
    dq next_node
    db %1, 0
%define next_node %2

%endmacro
