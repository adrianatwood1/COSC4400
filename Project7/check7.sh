#!/bin/bash
# Usage: ./check7.sh ref [Name ...]  save and show Brylow's output (testcases/Name.ref)
#        ./check7.sh                 build, diff ours vs Brylow's on every testcase
#        ./check7.sh Name            diff one testcase, show the full diff

REF=~brylow/cosc4400/Projects
TMP=$(mktemp -d)
trap 'rm -rf $TMP' EXIT
mkdir -p testcases

# raw NAME 'whole file text'
raw() { [ -e "testcases/$1.java" ] || printf '%s\n' "$2" > "testcases/$1.java"; }

raw T7_Fields 'class T7_Fields { public static void main(String[] a) { Xinu.printint(new B().f(3)); } }
class B { int x; public int f(int y) { x = y; return x; } }'

raw T7_Arrays 'class T7_Arrays { public static void main(String[] a) { Xinu.printint(new B().f()); } }
class B { public int f() { int[] q; q = new int[3]; q[1] = 5; return q[1] + q.length; } }'

raw T7_Control 'class T7_Control { public static void main(String[] a) { Xinu.printint(new C().f(3)); } }
class C { public int f(int n) { int s; s = 0; while (n > 0) { if (n < 2 && !(s == 5) || n == 3) { s = s + n; } else { s = s - 1; } n = n - 1; } return s; } }'

raw T7_Inherit 'class T7_Inherit { public static void main(String[] a) { A p; p = new B(); Xinu.printint(p.g()); } }
class A { int x; public int g() { return x; } }
class B extends A { int y; public int g() { return x + y; } }'

# Brylow's pipeline: parser -> type checker -> translator
ref() {
    $REF/mjparser "$1" > $TMP/t.ast 2>&1
    $REF/mjchecker $TMP/t.ast > $TMP/t.java.ast 2>&1
    $REF/mjtranslator $TMP/t.java.ast 2>&1
}

if [ "$1" = "ref" ]; then
    shift
    if [ $# -gt 0 ]; then files=$(for n in "$@"; do echo testcases/$n.java; done)
    else files=$(ls testcases/*.java); fi
    for file in $files; do
        out=testcases/$(basename $file .java).ref
        ref "$file" > $out
        echo "=== $file ==="
        cat $out
    done
    exit 0
fi

make > /dev/null 2>&1 || { echo "Compilation failed. Run make to see the errors."; exit 1; }

if [ -n "$1" ]; then files="testcases/$1.java"; full=1
else files=$(ls testcases/*.java); full=0; fi

pass=0; fail=0; skip=0; failed=""
for file in $files; do
    timeout 20 java -cp . Translate.Main < "$file" > $TMP/mine.txt 2>&1
    ref "$file" > $TMP/ref.txt

    if [ ! -s $TMP/ref.txt ] || head -1 $TMP/ref.txt | grep -q '^\(Usage:\|ERROR\)'; then
        echo "[SKIP] $file (reference gave no usable output)"
        skip=$((skip+1)); continue
    fi

    if diff $TMP/mine.txt $TMP/ref.txt > $TMP/diff.txt; then
        echo "[PASS] $file"; pass=$((pass+1))
    else
        echo "[FAIL] $file   (< = ours, > = Brylow's)"
        if [ $full -eq 1 ]; then cat $TMP/diff.txt; else head -15 $TMP/diff.txt; fi
        fail=$((fail+1)); failed="$failed $(basename $file)"
    fi
done

echo "----------------------------------------"
echo "pass=$pass fail=$fail skip=$skip"
[ -n "$failed" ] && echo "failed:$failed"
exit 0