set LIB_DIR=src/main/resources/com/github/rjeschke/jpa/native/win64
set LIB_NAME=jpa.dll

mkdir -p target/native
copy /y makefile target/native
cd target/native

nmake all

cd ../..

mkdir -p %LIB_DI%
copy /y target/native/%LIB_NAME% %LIB_DIR%
copy /y portaudio/x64/portaudio.dll %LIB_DIR%

