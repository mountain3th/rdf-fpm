if ![ -d "bin"]; then
	mkdir bin
fi
if ![ -d "log"]; then
	mkdir log
fi
javac -d bin @src/sourcefiles
if [ $? = 0 ]; then
	echo "done."