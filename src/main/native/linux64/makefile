CPP=g++
CC=gcc
EXE=../../../lib/com/github/rjeschke/jpa/linux64/libjpa.so
CPPFLAGS=-O3 -fno-strict-aliasing -Wall -Wno-format -march=native -mtune=native -D LINUX -D HB64 -fPIC
CCFLAGS=$(CPPFLAGS)
LIBS = -lrt -lpthread -lasound -lportaudio

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