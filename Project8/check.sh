#!/bin/bash
# Builds the project and compares each stage of our instruction selector
# with Brylow's reference (mjassem) using a plain diff.
#
# Usage: ./check.sh                    every test, every stage
#        ./check.sh Foo                one test, every stage, full diffs
#        ./check.sh Foo -a             one test, one stage
#        ./check.sh -p                 every test, one stage
#        ./check.sh ref Foo [-a ...]   show Brylow's output (default stage -f)
#
# Stages: -f final output (what is graded)   -c canonical trees
#         -b basic blocks   -t traces   -p prologue/epilogue   -a assembly

REF=~brylow/cosc4400/Projects
TMP=$(mktemp -d)
trap 'rm -rf $TMP' EXIT
mkdir -p testcases

names=(); stages=()
for a in "$@"; do
    case "$a" in
        -*) stages+=("$a") ;;
        *)  names+=("$a") ;;
    esac
done
given=${#stages[@]}

mode=check
if [ "${names[0]}" = "ref" ]; then mode=ref; names=("${names[@]:1}"); fi

if [ $given -eq 0 ]; then
    if [ $mode = ref ]; then stages=(-f); else stages=(-f -c -b -t -p -a); fi
fi

# E_ and C_ tests are type errors and never reach the instruction selector
if [ ${#names[@]} -gt 0 ]; then
    files=$(for n in "${names[@]}"; do echo testcases/$n.java; done)
    full=1; [ ${#names[@]} -gt 1 ] && full=0
else
    files=$(for f in testcases/*.java; do
        case $(basename $f) in E_*|C_*) ;; *) echo $f ;; esac
    done)
    full=0
fi

# Brylow's pipeline: parser -> checker -> translator -> instruction selector
# mjassem only parses "Riscv.RiscvFrame(", but mjtranslator prints "RiscvFrame("
mkir() {
    $REF/mjparser "$1" > $TMP/t.ast 2>&1
    $REF/mjchecker $TMP/t.ast > $TMP/t.java.ast 2>&1
    $REF/mjtranslator $TMP/t.java.ast 2>&1 \
        | sed 's/^RiscvFrame($/Riscv.RiscvFrame(/' > $TMP/t.ir
}

flag_of() { if [ "$1" = "-f" ]; then echo ""; else echo "$1"; fi; }

# for -c -b -t -p keep only the stage sections, so these stages can be
# compared before the final assembly exists
keep() {
    case "$1" in
        -c|-b|-t|-p) awk '/^\/\* \*\*\*\*/{p=1} /^ProcFrag\(/{p=0} p' ;;
        *) cat ;;
    esac
}

if [ $mode = ref ]; then
    for file in $files; do
        mkir "$file"
        for s in "${stages[@]}"; do
            echo "=== $file $s ==="
            $REF/mjassem $(flag_of $s) $TMP/t.ir 2>&1
        done
    done
    exit 0
fi

make > /dev/null 2>&1 || { echo "Compilation failed. Run make to see the errors."; exit 1; }

pass=0; fail=0; skip=0; failed=""
for file in $files; do
    mkir "$file"
    if [ ! -s $TMP/t.ir ] || head -1 $TMP/t.ir | grep -q '^\(Usage:\|ERROR\)'; then
        echo "[SKIP] $file (reference gave no usable IR)"
        skip=$((skip+1)); continue
    fi

    badstages=""; firstdiff=""; firststage=""
    for s in "${stages[@]}"; do
        f=$(flag_of $s)
        $REF/mjassem $f $TMP/t.ir 2>&1 | keep $s > $TMP/ref.txt
        timeout 20 java -cp . Assem.Main $f < "$file" 2>&1 | keep $s > $TMP/mine.txt
        if ! diff $TMP/mine.txt $TMP/ref.txt > $TMP/diff.txt; then
            badstages="$badstages $s"
            if [ -z "$firststage" ]; then
                firststage=$s; cp $TMP/diff.txt $TMP/firstdiff.txt
            fi
        fi
    done

    if [ -z "$badstages" ]; then
        echo "[PASS] $file"; pass=$((pass+1))
    else
        echo "[FAIL] $file   failing stages:$badstages   (< = ours, > = Brylow's)"
        echo "       first diff ($firststage):"
        if [ $full -eq 1 ]; then cat $TMP/firstdiff.txt
        else head -15 $TMP/firstdiff.txt; fi
        fail=$((fail+1)); failed="$failed $(basename $file .java)"
    fi
done

echo "----------------------------------------"
echo "pass=$pass fail=$fail skip=$skip"
[ -n "$failed" ] && echo "failed:$failed"
exit 0