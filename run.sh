#!/bin/bash
if [ $# != 4 ];then
	echo "not all args set."
	exit
fi
python transform.py -i dataset/$1 -o dataset/$2
if [ $? -eq 0 ];then
	echo "prepapre dataset ok."
	java -cp bin launcher.Launcher -debug -file dataset/$2 -support $3 -confidence $4
else
	echo "prepapre dataset failed."
fi