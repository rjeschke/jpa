CPP=g++
CC=gcc
EXE=../../../lib/jpa/linux32/libjpa.so
CPPFLAGS=-O3 -fno-strict-aliasing -Wall -Wno-format -march=i686 -mfpmath=387 -D LINUX -D HB32
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