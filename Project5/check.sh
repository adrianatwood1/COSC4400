#!/bin/bash
# Builds the project, generates extra error tests (once), and compares our
# checker's output with Brylow's reference using a plain diff.
#
# Usage: ./check.sh            generate missing tests, run everything
#        ./check.sh Foo        run only testcases/Foo.java, show the full diff

make > /dev/null 2>&1 || { echo "Compilation failed. Run make to see the errors."; exit 1; }

REF=~brylow/cosc4400/Projects
TMP=$(mktemp -d)
trap 'rm -rf $TMP' EXIT
mkdir -p testcases

# ---------- test generation (skips files that already exist) ----------
# mk NAME 'class body of T'   -> one main class plus class T
mk() {
    [ -e "testcases/$1.java" ] && return
    cat > "testcases/$1.java" << EOF
class $1 { public static void main(String[] a) { Xinu.printint(new T().run()); } }
class T {
$2
}
EOF
}
# raw NAME 'whole file text'  -> for class-level tests
raw() { [ -e "testcases/$1.java" ] || printf '%s\n' "$2" > "testcases/$1.java"; }

# expression, statement and return errors
mk E_UndefVar  'public int run() { x = 1; return 0; }'
mk E_AddBool   'public int run() { int i; i = true + 1; return i; }'
mk E_SubBool   'public int run() { int i; i = true - 1; return i; }'
mk E_MulBool   'public int run() { int i; i = true * 1; return i; }'
mk E_GtBool    'public int run() { boolean b; b = true > 1; return 0; }'
mk E_NotInt    'public int run() { boolean b; b = !5; return 0; }'
mk E_NegBool   'public int run() { int i; i = -true; return i; }'
mk E_AndInt    'public int run() { boolean b; b = 1 && 2; return 0; }'
mk E_OrInt     'public int run() { boolean b; b = 1 || 2; return 0; }'
mk E_IfInt     'public int run() { if (1) { } else { } return 0; }'
mk E_WhileInt  'public int run() { while (1) { } return 0; }'
mk E_RetType   'public boolean run() { return 1; }'
mk E_NullInt   'public int run() { int i; i = null; return 0; }'
mk V_NullObj   'public int run() { T t; t = null; return 0; }'

# calls, arrays, fields
mk E_NoMethod  'public int run() { return this.nope(); }'
mk E_ArgCount  'public int f(int x) { return x; } public int run() { return this.f(1, 2); }'
mk E_ArgType   'public int f(int x) { return x; } public int run() { return this.f(true); }'
mk E_BadTarget 'public int run() { int i; i = 3; return i.foo(); }'
mk E_NotArray  'public int run() { int i; i = 3; return i[0]; }'
mk E_ArrIdx    'public int run() { int[] a; a = new int[3]; return a[true]; }'
mk E_NoField   'public int run() { T t; t = new T(); return t.zzz; }'

# duplicates and unknown classes
mk E_DupLocal  'public int run() { int i; boolean i; return 0; }'
mk E_DupField  'int x; boolean x; public int run() { return 0; }'
mk E_NoClass   'public int run() { Q q; q = new Q(); return 0; }'

# class-level errors
M='class MAIN { public static void main(String[] a) { Xinu.printint(0); } }'
raw C_Cycle2    "${M/MAIN/C_Cycle2}
class A extends B { public int f() { return 0; } }
class B extends A { public int g() { return 0; } }"
raw C_SelfCycle "${M/MAIN/C_SelfCycle}
class A extends A { public int f() { return 0; } }"
raw C_NoParent  "${M/MAIN/C_NoParent}
class A extends Missing { public int f() { return 0; } }"
raw C_DupClass  "${M/MAIN/C_DupClass}
class A { public int f() { return 0; } }
class A { public int g() { return 0; } }"
raw C_BadOver   "${M/MAIN/C_BadOver}
class A { public int f(int x) { return x; } }
class B extends A { public int f(boolean x) { return 0; } }"
raw V_CoVar     "${M/MAIN/V_CoVar}
class A { public A f() { return new A(); } }
class B extends A { public B f() { return new B(); } }"

# ---------- reference runner ----------
ref() {
    $REF/mjparser "$1" > $TMP/t.ast 2>&1
    $REF/mjchecker $TMP/t.ast 2>&1
}

if [ -n "$1" ]; then files="testcases/$1.java"; full=1
else files=$(ls testcases/*.java); full=0; fi

pass=0; fail=0; skip=0; failed=""
for file in $files; do
    timeout 20 java -cp . Semant.Main < "$file" > $TMP/mine.txt 2>&1
    ref "$file" > $TMP/ref.txt

    # reference failed to parse or gave nothing: not our bug
    if [ ! -s $TMP/ref.txt ] || head -1 $TMP/ref.txt | grep -q '^Usage:'; then
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
