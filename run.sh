#!/bin/bash
if [ -z $1 ];then
	echo "not all args set."
	exit
fi
python transform.py -i dataset/$1 -o dataset/graph.lg
if [ $? -eq 0 ];then
	echo "prepapre dataset ok."
	java -cp bin launcher.Launcher -debug -file dataset/graph.lg -support $2 
else then
	echo "prepapre dataset failed."
fi