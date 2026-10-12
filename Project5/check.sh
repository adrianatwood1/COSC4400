#!/bin/bash

echo "Compiling project..."
make

if [ $? -ne 0 ]; then
    echo "Compilation failed! Stopping tests."
    exit 1
fi

echo "Compilation successful. Running tests..."
echo "----------------------------------------"

for file in testcases/*.java; do
    echo "Testing $file..."
    
    # 1. YOUR output: Run your full pipeline
    java -cp . Semant.Main "$file" > my_out.txt
    
    # 2. HIS output: Use the -p flag to tell his checker to parse the raw Java file!
    ~brylow/cosc4400/Projects/mjchecker -p "$file" > ref_out.txt
    
    # Compare the two outputs, ignoring whitespace differences
    diff -w -B my_out.txt ref_out.txt
    if [ $? -eq 0 ]; then
        echo "  [PASS] Output matches exactly!"
    else
        echo "  [FAIL] Output does not match checker!"
        echo "  --- YOUR OUTPUT --- "
        cat my_out.txt
        echo "  --- HIS EXPECTED OUTPUT (checker) --- "
        cat ref_out.txt
    fi
    echo "----------------------------------------"
done

# Clean up temp files
rm -f my_out.txt ref_out.txt