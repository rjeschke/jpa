CPP=g++
CC=gcc
EXE=../../../lib/jpa/macos/libjpa.jnilib
CPPFLAGS=-O3 -fno-strict-aliasing -Wall -Wno-format -arch i386 -arch x86_64 -D MACOS
CCFLAGS=$(CPPFLAGS)
LIBS = -lportaudio

%.o: ../%.c
	@echo Compiling $< ...
	@$(CC) $(CCFLAGS) -c -o $@ $<

objects = jpa.o

$(EXE): $(objects)
	@echo Linking $(EXE) ...
	@$(CC) -arch i386 -arch x86_64 -shared -o $(EXE) $(LIBS) $(objects)
	@echo READY.

all: $(EXE)

clean:
	rm -f *.o
	rm -f $(EXE)