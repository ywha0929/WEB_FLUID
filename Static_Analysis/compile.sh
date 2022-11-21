#!/bin/bash
if [ $# -eq 1 ]
then
	mkdir -p build
	javac -d ./build/ -classpath $1:./libs/framework.jar ./src/FakeMainClass.java
	javac -d ./build/ -classpath ./libs/sootclasses-trunk-jar-with-dependencies.jar ./src/DuiCHATool.java
else
	echo "USAGE: ./compile.sh [TARGET_APP_JAR]"
fi
