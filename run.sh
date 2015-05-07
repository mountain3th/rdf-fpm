#!/bin/bash
if [ $# != 4 ];then
	echo "not all args set."
	exit
fi
cd dataset
python transform.py -i $1 -o $2
if [ $? -eq 0 ];then
	echo "prepapre dataset ok."
	cd ..
	java -cp bin launcher.Launcher -debug -file dataset/$2 -support $3 -confidence $4
else
	echo "prepapre dataset failed."
fi