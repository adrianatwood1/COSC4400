#!/bin/bash
make

for file in testcases/*.java; do
    java Parse.Main < "$file" > "our_output.ast"
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

rm our_output.ast brylow_output.ast diff_result.txt