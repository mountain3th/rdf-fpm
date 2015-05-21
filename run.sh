#!/bin/bash
if [ $# != 6 ];then
	echo "not all args set."
	exit
fi
cd dataset
python transform.py -mi $1 -mo $2 -ti $3 -to $4
if [ $? -eq 0 ];then
	echo "prepapre dataset ok."
	cd ..
	java -cp bin launcher.Launcher -debug -ifile dataset/$2 -tfile dataset/$4 -support $5 -confidence $6
else
	echo "prepapre dataset failed."
fi