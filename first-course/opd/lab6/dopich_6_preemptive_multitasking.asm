ORG 0x0
V0:  WORD $INT0, 0x1A0

queue_link: WORD 0x3
queue: WORD 0x63, 0x0
		  WORD 0x83, 0x0
		  WORD 0x103, 0x0
queue_call: WORD ?

ORG 0x10
reset_timer:
	LD get_seconds
	OUT 0
	RET

get_seconds: WORD 0xF1

ORG 0x020
START:
	DI
   	CALL $reset_timer
    	LD #8
  	OUT 1
  	CLA
  	JUMP SCHEDULE_CYCLE

SCHEDULE_CYCLE:
	DI
	CALL CHECK_FUNC_STATUSES
	LD FINISH_STATUS
	CMP #1
	BEQ finish
	EI
	LD (queue_link)
	ST queue_call
	CALL (queue_call)
	JUMP SCHEDULE_CYCLE

finish: HLT

ORG 0x40
INT0:
	DI
	PUSH
	NOP
	LD (queue_link)+
	LD (queue_link)+
	CMP #0x10
	BEQ set_queue_link_default
	cmp_func_status: LD (queue_link)+
	CMP #1
	BEQ goto_next_iter
	LD -(queue_link)
	ST queue_call
	JUMP call_current_function
	set_queue_link_default: LD #3
	ST queue_link
	ST queue_call
	JUMP cmp_func_status
	call_current_function:
	CALL $reset_timer
	EI
	CALL (queue_call)
	POP
   	IRET
   	goto_next_iter: LD -(queue_link)
	INT 0

ORG 0x60
func1_status: WORD 0x4
func1_number: WORD 0x0
func1_cmp_with: WORD 0x8
FUNC1:
	NOP
	LD func1_number
	CMP func1_cmp_with
	BEQ func1_set_finished
	INC
	INC
	ST func1_number
	func1_ret: 
	NOP
	RET
	func1_set_finished: LD #1
	ST (func1_status)
	JUMP func1_ret

ORG 0x80
func2_status: WORD 0x6
func2_number: WORD 0x0
func2_cmp_with: WORD 0xFFFB
FUNC2:
	NOP
	LD func2_number
	CMP func2_cmp_with
	BEQ func2_set_finished
	DEC
	ST func2_number
	func2_ret: 
	NOP
	RET
	func2_set_finished: LD #1
	ST (func2_status)
	JUMP func2_ret

FINISH_STATUS: WORD 0x0
CHECK_FUNC_STATUSES:
	LD (func1_status)
	CMP #0
	BEQ check_func_statuses_exit
	LD (func2_status)
	CMP #0
	BEQ check_func_statuses_exit
	LD (func3_status)
	CMP #0
	BEQ check_func_statuses_exit
	LD #1
	ST FINISH_STATUS
	check_func_statuses_exit: RET

ORG 0x100
func3_status: WORD 0x8
func3_number: WORD 0x0
func3_cmp_with: WORD 0x8
FUNC3:
	NOP
	LD func3_number
	CMP func3_cmp_with
	BEQ func3_set_finished
	ADD #4
	ST func3_number
	func3_ret: 
	NOP
	RET
	func3_set_finished: LD #1
	ST (func3_status)
	JUMP func3_ret