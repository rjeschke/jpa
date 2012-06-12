#!bin/bash

LIB_DIR=src/main/resources/com/github/rjeschke/jpa/native/macos
LIB_NAME=libjpa.jnilib

set -e

mkdir -p target/native
cp makefile target/native
cd target/native

make all

cd ../..

mkdir -p $LIB_DIR
cp target/native/$LIB_NAME $LIB_DIR

