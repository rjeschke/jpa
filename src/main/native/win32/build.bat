@echo off

set LIB_DIR=src\main\resources\com\github\rjeschke\jpa\native\win32
set LIB_NAME=jpa.dll

mkdir target\native
copy /y build.bat target\native\
if %errorlevel% neq 0 exit /b %errorlevel%
copy /y makefile target\native
if %errorlevel% neq 0 exit /b %errorlevel%
cd target\native
if %errorlevel% neq 0 exit /b %errorlevel%

nmake all
if %errorlevel% neq 0 exit /b %errorlevel%

cd ..\..

mkdir %LIB_DIR%
copy /y target\native\%LIB_NAME% %LIB_DIR%
if %errorlevel% neq 0 exit /b %errorlevel%
copy /y ..\portaudio\x64\portaudio.dll %LIB_DIR%
if %errorlevel% neq 0 exit /b %errorlevel%

