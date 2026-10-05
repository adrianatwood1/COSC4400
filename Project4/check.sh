#!/bin/bash

# Clean and build, exit immediately if compilation fails
make clean && make || { echo "Build failed. Aborting tests."; exit 1; }

for file in testcases/*.java; do
    # Added explicit classpath matching the Makefile JFLAGS
    java -cp . Parse.Main < "$file" > "our_output.ast"
    
    ~brylow/cosc4400/Projects/mjparser - < "$file" > "brylow_output.ast"
    
    diff -w -u --color=always "our_output.ast" "brylow_output.ast" > diff_result.txt
    
    if [ -s diff_result.txt ]; then
        echo "========================================"
        echo "FAIL: $file"
        echo "Red (-) is your output. Green (+) is Brylow's expected output."
        echo "----------------------------------------"
        cat diff_result.txt
        echo "========================================"
    else
        echo "PASS: $file"
    fi
done

# Cleanup test artifacts
rm -f our_output.ast brylow_output.ast diff_result.txt