CPP      = g++
CC       = gcc
SRC_DIR  = ../../..
LIB      = libjpa.so
CPPFLAGS = -O3 -fno-strict-aliasing -Wall -Wno-format -march=native -mtune=native -D LINUX -D HB64 -fPIC -I$(SRC_DIR)
CCFLAGS  = $(CPPFLAGS)
LIBS     = -lrt -lpthread -lasound -lportaudio

%.o: $(SRC_DIR)/%.c
	@echo Compiling $< ...
	@$(CC) $(CCFLAGS) -c -o $@ $<

objects = jpa.o

$(LIB): $(objects)
	@echo Linking $(LIB) ...
	@$(CC) -shared -o $(LIB) $(LIBS) $(objects)
	@echo READY.

all: $(LIB)

clean:
	rm -f *.o
	rm -f $(LIB)
