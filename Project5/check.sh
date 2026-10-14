#!/bin/bash
make || { echo "Compilation failed"; exit 1; }

# Edit this ONE function after you find the form that works by hand:
ref() { ~brylow/cosc4400/Projects/mjchecker "$1" 2>&1; }

pass=0; fail=0
for file in testcases/*.java; do
    java -cp . Semant.Main < "$file" > my_out.txt 2>&1
    ref "$file" > ref_out.txt

    if [ ! -s ref_out.txt ]; then
        echo "[SKIP] $file - reference printed nothing"; continue
    fi
    if diff my_out.txt ref_out.txt > diff_out.txt; then
        pass=$((pass+1))
    else
        echo "[FAIL] $file"; head -15 diff_out.txt; fail=$((fail+1))
    fi
done
echo "pass=$pass fail=$fail"
rm -f my_out.txt ref_out.txt diff_out.txt