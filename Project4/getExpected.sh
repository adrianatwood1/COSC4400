#!/bin/bash

TEST_DIR="testcases"
OUTPUT_FILE="expected_types.txt"

# Clear out the old file
> "$OUTPUT_FILE"

echo "Running type checker on all test cases..."

for file in "$TEST_DIR"/*.java; do
    echo "========================================" >> "$OUTPUT_FILE"
    echo "File: $file" >> "$OUTPUT_FILE"
    echo "========================================" >> "$OUTPUT_FILE"
    
    # Pipe the parser AST directly into the type checker, append to file
    ~brylow/cosc4400/Projects/mjparser "$file" | ~brylow/cosc4400/Projects/mjchecker -c - >> "$OUTPUT_FILE" 2>&1
    
    echo -e "\n" >> "$OUTPUT_FILE"
done

echo "Done! Type checker outputs saved to $OUTPUT_FILE"