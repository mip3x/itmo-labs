ASM = nasm
ASM_FLAGS = -f elf64 -I lib.io -o
LINKER = ld
LINKER_FLAGS = -o
REMOVE = rm -rf
PYTHON = python3
MAKEDIR = mkdir -p

BUILD_DIR = build

LIB_SRC = lib.io/lib.asm
MAIN_SRC = main.asm
DICT_SRC = dict.asm
WORDS_SRC = words.inc

LIB_OBJ = $(BUILD_DIR)/lib.o
MAIN_OBJ = $(BUILD_DIR)/main.o
DICT_OBJ = $(BUILD_DIR)/dict.o
OBJS = $(LIB_OBJ) $(MAIN_OBJ) $(DICT_OBJ)

EXECUTABLE = dictionary

DEBUG = 0
ifeq ($(DEBUG), 1)
ASM_FLAGS += -g
endif

.PHONY: all clean test update

all: update $(EXECUTABLE)

update:
	@git submodule update --init --recursive

test: all
	@$(PYTHON) test.py

$(BUILD_DIR):
	@$(MAKEDIR) $(BUILD_DIR)

$(EXECUTABLE): $(OBJS)
	@echo "Linking..."
	@$(LINKER) $(LINKER_FLAGS) $@ $(OBJS)

$(LIB_OBJ): $(LIB_SRC) | $(BUILD_DIR)
	@echo "Compiling $<..."
	@$(ASM) $(ASM_FLAGS) $@ $<

$(MAIN_OBJ): $(MAIN_SRC) $(WORDS_SRC) | $(BUILD_DIR)
	@echo "Compiling $<..."
	@$(ASM) $(ASM_FLAGS) $@ $<

$(DICT_OBJ): $(DICT_SRC) | $(BUILD_DIR)
	@echo "Compiling $<..."
	@$(ASM) $(ASM_FLAGS) $@ $<

clean:
	@echo "Cleaning..."
	@$(REMOVE) $(BUILD_DIR) $(EXECUTABLE)

