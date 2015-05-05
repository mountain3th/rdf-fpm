#!/bin/bash
inputfile = mappingbased_properties_en.ttl
support_value = 1000
python transform.py -i dataset/$1 -o dataset/graph.lg
if [ $? == 0 ];then
	echo "prepapre dataset ok"
	java -cp bin launcher.Launcher -debug -file dataset/graph.lg -support $2 
else;then
	echo "prepapre dataset failed."
fi