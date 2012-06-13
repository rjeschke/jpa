src_dir   = ../../..
portaudio = $(src_dir)/portaudio/x64
dll       = jpa.dll
cc        = cl.exe
link      = link.exe
cflags    = /O2 /GL /D "WIN64" /D "HB64" /D "NDEBUG" /D "_WINDOWS" /D "_USRDLL" /D "_WINDLL" /FD /EHsc /MT /W3 /nologo /c /Zi /TC /errorReport:prompt /I$(src_dir) /I$(portaudio)
linkflags = /MANIFEST /INCREMENTAL:NO /NOLOGO /DLL /SUBSYSTEM:WINDOWS /OPT:REF /OPT:ICF /LTCG /MACHINE:X64 /ERRORREPORT:PROMPT
libs      = $(portaudio)/portaudio.lib

objs      = jpa.obj

{$(src_dir)}.c.obj:
	@echo Compiling $< ...
	$(cc) $(cflags) $<

$(dll): $(objs)
	@echo Linking $(dll) ...
	@$(link) $(linkflags) /out:$(dll) $** $(libs)
	@echo READY.

all: $(dll)

clean:
	@del *.obj
	@del $(dll)

