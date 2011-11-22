CPP=g++
CC=gcc
EXE=../../../lib/jpa/macos64/libjpa.jnilib
CPPFLAGS=-O3 -fno-strict-aliasing -Wall -Wno-format -arch x86_64 -D MACOS -D HB64
CCFLAGS=$(CPPFLAGS)
LIBS = -lportaudio

%.o: ../%.c
	@echo Compiling $< ...
	@$(CC) $(CCFLAGS) -c -o $@ $<

objects = jpa.o

$(EXE): $(objects)
	@echo Linking $(EXE) ...
	@$(CC) -shared -o $(EXE) $(LIBS) $(objects)
	@echo READY.

all: $(EXE)

clean:
	rm -f *.o
	rm -f $(EXE)