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
find src/ -name \*.java -print > file.list
	javac -g -d classes -cp $CP @file.list
	echo "Task    : [compile] completed"
	cp src/com/microlib/server/*.properties classes/com/microlib/server/
	cp src/com/microlib/jndi/service/*.properties classes/com/microlib/jndi/service/
	cp src/*.properties classes/
	echo "Task    : [copying resources] completed"
    echo " "
}

function run() {
	java -cp $CP com.microlib.server.TheServer 9000 100
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


