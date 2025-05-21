MAKEFLAGS += -s

EXEC_NAME := ray_tracing
OUTPUT_PPM := output.ppm

SRC_DIR := src
OUT_DIR := out

SRC := $(wildcard $(SRC_DIR)/*.c)
OBJ := $(SRC:$(SRC_DIR)/%.c=$(OUT_DIR)/%.o)

CC = gcc
# MMD and MP are used for automatic dependencies
CFLAGS = -std=c2x -O2 -MMD -MP
LDFLAGS = -lm

all: $(EXEC_NAME)

run: $(EXEC_NAME)
	@echo
	./$(EXEC_NAME)
	@echo

$(EXEC_NAME): $(OBJ)
	$(CC) $(OBJ) -o $@ $(LDFLAGS)

# make object files to out directory
$(OUT_DIR)/%.o: $(SRC_DIR)/%.c
	@mkdir -p $(OUT_DIR) # ensure out directory exists
	$(CC) $(CFLAGS) -c $< -o $@

# dependencies using .d files which keep track of used headers
-include $(OBJ:.o=.d)

clean:
	$(RM) -r $(OUT_DIR)/* $(EXEC_NAME)

.PHONY: clean $(EXEC_NAME) run all
