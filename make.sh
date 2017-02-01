#!/bin/bash

#source env.properties
PROJECT="JWT"
VER="1.0.0"


CP="classes:"
jars=`ls lib/*.jar`
for i in $jars; 
do  
	CP="$CP:$i"
done

#clear
echo " "
echo "Project : $PROJECT performing task [ $1 ] "
echo " "
echo "Classpath : $CP "

function usage() {
	echo "Usage : ./make.sh [target]"
	echo "where target : one of [ clean compile ]"
}

function clean() {
	rm -rf classes/com/*
	echo "Task    : [clean] completed"
    	echo " "
}

function compile() {
	#find  -name \*.java -print > file.list
	javac -g -d classes -cp $CP src/com/microlib/service/TestJwt.java
	echo "Task    : [compile] completed"
    	echo " "
}

function run() {
	java -cp $CP com.microlib.service.TestJwt
	echo "Task    : [run] completed"
    	echo " "
}


if [ "$#" -lt 1 ]
then
	usage
	exit
fi

if [ "$1" = "clean" ]
then
	clean
	exit
fi  

if [ "$1" = "compile" ]
then
	clean
	compile
	exit
fi

if [ "$1" = "run" ]
then
	run
	exit
fi


