#!/bin/bash

# COSC 4400 - Project 5 Test Harness
echo "Compiling project..."
javac -cp . Semant/*.java Types/*.java Symbol/*.java Absyn/*.java Parse/*.java

if [ $? -ne 0 ]; then
    echo "Compilation failed! Stopping."
    exit 1
fi

echo "Compilation successful. Running tests..."
echo "----------------------------------------"

# Ensure testcases directory exists
if [ ! -d "testcases" ]; then
    echo "Error: 'testcases' directory not found!"
    exit 1
fi

for file in testcases/*.java; do
    echo "Testing $file..."

    # Run Dr. Brylow's reference parser piped into his reference checker
    ~brylow/cosc4400/Projects/parser < "$file" | ~brylow/cosc4400/Projects/checker > "${file}.expected" 2>&1

    # Run your compiler (which already includes the parser in Main.java)
    java -cp . Semant.Main < "$file" > "${file}.out" 2>&1

    # Compare the outputs, ignoring whitespace differences
    if diff -w "${file}.out" "${file}.expected" > /dev/null; then
        echo "  [PASS] Output perfectly matches checker."
    else
        echo "  [FAIL] Output does not match checker!"
        echo "  --- YOUR OUTPUT --- "
        cat "${file}.out"
        echo "  --- HIS EXPECTED OUTPUT (checker) --- "
        cat "${file}.expected"
    fi
    echo "----------------------------------------"
done

echo "Testing complete!"