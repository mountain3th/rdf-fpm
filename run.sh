#!/bin/bash
if [ $# != 5 ];then
	echo "not all args set."
	exit
fi
python transform.py -i $1 -o $2
if [ $? -eq 0 ];then
	echo "prepapre dataset ok."
	java -cp bin launcher.Launcher -debug -file $2 -support $3 -confidence $4
else
	echo "prepapre dataset failed."
fi