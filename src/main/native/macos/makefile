CPP      = g++
CC       = gcc
SRC_DIR  = ../../..
LIB      = libjpa.jnilib
CPPFLAGS = -O3 -fno-strict-aliasing -Wall -Wno-format -arch i386 -arch x86_64 -D MACOS -I$(SRC_DIR)
CCFLAGS  = $(CPPFLAGS)
LIBS     = -lportaudio

%.o: $(SRC_DIR)/%.c
	@echo Compiling $< ...
	@$(CC) $(CCFLAGS) -c -o $@ $<

objects = jpa.o

$(LIB): $(objects)
	@echo Linking $(LIB) ...
	@$(CC) -arch i386 -arch x86_64 -shared -o $(LIB) $(LIBS) $(objects)
	@echo READY.

all: $(LIB)

clean:
	rm -f *.o
	rm -f $(LIB)
