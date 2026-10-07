#!/bin/bash

echo "Compiling project..."
make
if [ $? -ne 0 ]; then
    echo "Make failed. Stopping tests."
    exit 1
fi
echo "Compilation successful. Running tests..."
echo "----------------------------------------"

for file in testcases/*.java; do
    echo "Testing $file..."
    
    # 1. Run your Semantic compiler 
    java Semant.Main "$file" > "${file}.myout" 2> "${file}.myerr"
    
    # 2. Run Dr. Brylow's reference pipeline (Parser piped into Checker)
    ~brylow/cosc4400/Projects/mjparser "$file" | ~brylow/cosc4400/Projects/mjchecker -c - > "${file}.hisout" 2> "${file}.hiserr"
    
    # 3. Combine his stdout and stderr to serve as the master expected output
    cat "${file}.hisout" "${file}.hiserr" > "${file}.expected"
    
    # 4. Combine your stdout and stderr for the comparison
    cat "${file}.myout" "${file}.myerr" > "${file}.mycombined"

    # 5. Diff the combined outputs
    diff -w "${file}.mycombined" "${file}.expected" > "${file}.diff"
    
    if [ -s "${file}.diff" ]; then
        echo -e "\033[0;31m  [FAIL] Output does not match mjchecker!\033[0m"
        
        echo -e "\033[0;36m  --- YOUR OUTPUT --- \033[0m"
        cat "${file}.mycombined"
        
        echo -e "\033[0;36m  --- HIS EXPECTED OUTPUT (mjchecker) --- \033[0m"
        cat "${file}.expected"
        
        echo "  ----------------------------------------"
    else
        echo -e "\033[0;32m  [PASS] Output perfectly matches mjchecker.\033[0m"
    fi
    
    # Clean up temporary test files
    rm "${file}.diff" "${file}.myout" "${file}.myerr" "${file}.hisout" "${file}.hiserr" "${file}.expected" "${file}.mycombined" 2>/dev/null
    
    echo "----------------------------------------"
done
echo "Testing complete!"