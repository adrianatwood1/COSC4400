package Riscv;
import java.util.LinkedList;
import java.util.List;
import Assem.Instr;
import Temp.Temp;
import Temp.Label;


/**
 * COSC 4400 - Project 8
 * Instruction Selection
 * @adrian atwood pj panarese
 * Dr.Brylow
 * TA-BOT:MAILTO adrian.atwood-langeler@marquette.edu patrick.panarese@marquette.edu
 */
public class Codegen implements Tree.CodeVisitor {
    private RiscvFrame frame;
    private LinkedList<Instr> ilist = new LinkedList<Instr>();

    public Codegen(RiscvFrame frame){
        this.frame = frame;
    }
    public List<Instr> instrs(){
        return ilist;
    }
    private void emit(Instr i){
        ilist.add(i);
    }
    private static Temp[] L(Temp... t){
        return t;
    }
    private static boolean fits(Tree.Exp e){
        if (!(e instanceof Tree.CONST)){
            return false;
        }
        int v = ((Tree.CONST) e).value;
        if (v >= -2048 && v <= 2047){
            return true;
        }
        return false;
    }
    private static int value(Tree.Exp e){
        return ((Tree.CONST) e).value;
    }

    private static boolean power2(Tree.Exp e){
        if (!(e instanceof Tree.CONST)){
            return false;
        }
        int v = ((Tree.CONST) e).value;
        if (v > 0 && (v & (v - 1)) == 0){
            return true;
        }
        return false;
    }
    private static int log2(Tree.Exp e){
        return Integer.numberOfTrailingZeros(((Tree.CONST) e).value);
    }
    private void move(Temp d, Temp s){
        emit(new Assem.MOVE("\tmv\t`d0,\t`s0", d, s));
    }
    public void visit(Tree.SEQ n){
        throw new Error("SEQ in codegen");
    }
    public void visit(Tree.LABEL n){
        emit(new Assem.LABEL(n.label + ":", n.label));
    }
    public void visit(Tree.JUMP n){
        if (n.exp instanceof Tree.NAME){
            emit(new Assem.OPER("\tj\t`j0", null, null, n.targets));
        } else {
            Temp t = n.exp.accept(this);
            emit(new Assem.OPER("\tjalr\t`s0", null, L(t), n.targets));
        }
    }
    public void visit(Tree.CJUMP n){
        String op;
        if (n.relop == Tree.CJUMP.EQ){
            op = "beq";
        } else if (n.relop == Tree.CJUMP.NE){
            op = "bne";
        } else if (n.relop == Tree.CJUMP.LT){
            op = "blt";
        } else if (n.relop == Tree.CJUMP.GT){
            op = "bgt";
        } else if (n.relop == Tree.CJUMP.LE){
            op = "ble";
        } else if (n.relop == Tree.CJUMP.GE){
            op = "bge";
        } else {
            throw new Error("unsupported relop in CJUMP");
        }
        Temp l = n.left.accept(this);
        Temp r = n.right.accept(this);
        LinkedList<Label> targets = new LinkedList<Label>();
        targets.add(n.iftrue);
        targets.add(n.iffalse);
        emit(new Assem.OPER("\t" + op + "\t`s0,\t`s1,\t`j0", null, L(l, r), targets));
    }
    public void visit(Tree.MOVE n){
        if (n.dst instanceof Tree.MEM){
            Tree.Exp addr = ((Tree.MEM) n.dst).exp;
            if (addr instanceof Tree.BINOP && ((Tree.BINOP) addr).binop == Tree.BINOP.PLUS){
                Tree.BINOP b = (Tree.BINOP) addr;
                if (fits(b.right)){
                    Temp s = n.src.accept(this);
                    Temp base = b.left.accept(this);
                    emit(new Assem.OPER("\tsw\t`s0,\t" + value(b.right) + "(`s1)", null, L(s, base), null));
                    return;
                }
                if (fits(b.left)){
                    Temp s = n.src.accept(this);
                    Temp base = b.right.accept(this);
                    emit(new Assem.OPER("\tsw\t`s0,\t" + value(b.left) + "(`s1)",null, L(s, base), null));
                    return;
                }
            }
            Temp s = n.src.accept(this);
            Temp base = addr.accept(this);
            emit(new Assem.OPER("\tsw\t`s0,\t0(`s1)", null, L(s, base), null));
        } else {
            Temp s = n.src.accept(this);
            Temp d = n.dst.accept(this);
            move(d, s);
        }
    }

    public void visit(Tree.EXP n){
        n.exp.accept(this);
    }

    public Temp visit(Tree.ESEQ n){
        throw new Error("ESEQ in codegen");
    }

    public Temp visit(Tree.CONST n){
        if (n.value == 0){
            return RiscvFrame.ZERO;
        }
        Temp r = new Temp();
        emit(new Assem.OPER("\tli\t`d0,\t" + n.value, L(r), null, null));
        return r;
    }
    public Temp visit(Tree.NAME n){
        Temp r = new Temp();
        emit(new Assem.OPER("\tla\t`d0,\t" + n.label, L(r), null, null));
        return r;
    }
    public Temp visit(Tree.TEMP n){
        return n.temp;
    }
    public Temp visit(Tree.MEM n){
        Temp r = new Temp();
        if (n.exp instanceof Tree.BINOP && ((Tree.BINOP) n.exp).binop == Tree.BINOP.PLUS){
            Tree.BINOP b = (Tree.BINOP) n.exp;
            if (fits(b.right)){
                Temp base = b.left.accept(this);
                emit(new Assem.OPER("\tlw\t`d0,\t" + value(b.right) + "(`s0)",
                                    L(r), L(base), null));
                return r;
            }
            if (fits(b.left)){
                Temp base = b.right.accept(this);
                emit(new Assem.OPER("\tlw\t`d0,\t" + value(b.left) + "(`s0)",
                                    L(r), L(base), null));
                return r;
            }
        }
        Temp a = n.exp.accept(this);
        emit(new Assem.OPER("\tlw\t`d0,\t0(`s0)", L(r), L(a), null));
        return r;
    }
    private void regReg(String op, Temp r, Tree.Exp left, Tree.Exp right){
        Temp a = left.accept(this);
        Temp b = right.accept(this);
        emit(new Assem.OPER("\t" + op + "\t`d0,\t`s0,\t`s1", L(r), L(a, b), null));
    }
    private void regImm(String op, Temp r, Tree.Exp reg, int imm){
        Temp a = reg.accept(this);
        emit(new Assem.OPER("\t" + op + "\t`d0,\t`s0,\t" + imm, L(r), L(a), null));
    }
    public Temp visit(Tree.BINOP n){
        Temp r = new Temp();
        int op = n.binop;
        if (op == Tree.BINOP.PLUS){
            if (fits(n.right)){
                regImm("addi", r, n.left, value(n.right));
            } else if (fits(n.left)){
                regImm("addi", r, n.right, value(n.left));
            } else {
                regReg("add", r, n.left, n.right);
            }
        } else if (op == Tree.BINOP.MINUS){
            if (fits(n.right)){
                regImm("addi", r, n.left, -value(n.right));
            } else {
                regReg("sub", r, n.left, n.right);
            }
        } else if (op == Tree.BINOP.MUL){
            if (power2(n.right)){
                regImm("sll", r, n.left, log2(n.right));
            } else if (power2(n.left)){
                regImm("sll", r, n.right, log2(n.left));
            } else {
                regReg("mul", r, n.left, n.right);
            }
        } else if (op == Tree.BINOP.DIV){
            if (power2(n.right)){
                regImm("sra", r, n.left, log2(n.right));
            } else {
                regReg("div", r, n.left, n.right);
            }
        } else if (op == Tree.BINOP.AND || op == Tree.BINOP.BITAND){
            if (fits(n.right)){
                regImm("andi", r, n.left, value(n.right));
            } else if (fits(n.left)){
                regImm("andi", r, n.right, value(n.left));
            } else {
                regReg("and", r, n.left, n.right);
            }
        } else if (op == Tree.BINOP.OR || op == Tree.BINOP.BITOR){
            if (fits(n.right)){
                regImm("ori", r, n.left, value(n.right));
            } else if (fits(n.left)){
                regImm("ori", r, n.right, value(n.left));
            } else {
                regReg("or", r, n.left, n.right);
            }
        } else if (op == Tree.BINOP.LSHIFT){
            if (fits(n.right)){
                regImm("sll", r, n.left, value(n.right));
            } else {
                regReg("sll", r, n.left, n.right);
            }
        } else if (op == Tree.BINOP.RSHIFT){
            if (fits(n.right)){
                regImm("srl", r, n.left, value(n.right));
            } else {
                regReg("srl", r, n.left, n.right);
            }
        } else if (op == Tree.BINOP.ARSHIFT){
            if (fits(n.right)){
                regImm("sra", r, n.left, value(n.right));
            } else {
                regReg("sra", r, n.left, n.right);
            }
        } else if (op == Tree.BINOP.BITXOR){
            if (fits(n.right)){
                regImm("xori", r, n.left, value(n.right));
            } else if (fits(n.left)){
                regImm("xori", r, n.right, value(n.left));
            } else {
                regReg("xor", r, n.left, n.right);
            }
        } else {
            throw new Error("unsupported BINOP");
        }
        return r;
    }
    public Temp visit(Tree.CALL n){
        int count = 0;
        LinkedList<Temp> used = new LinkedList<Temp>();
        for (Tree.Exp arg : n.args){
            Temp t = arg.accept(this);
            if (count < RiscvFrame.argRegs.length){
                move(RiscvFrame.argRegs[count], t);
                used.add(RiscvFrame.argRegs[count]);
            } else {
                int off = (count - RiscvFrame.argRegs.length) * frame.wordSize();
                emit(new Assem.OPER("\tsw\t`s0,\t" + off + "(`s1)",
                                    null, L(t, RiscvFrame.SP), null));
            }
            count++;
        }
        if (n.func instanceof Tree.NAME){
            emit(new Assem.OPER("\tcall\t" + ((Tree.NAME) n.func).label,
                                RiscvFrame.calldefs.clone(),
                                used.toArray(new Temp[0]), null));
        } else {
            Temp f = n.func.accept(this);
            LinkedList<Temp> uses = new LinkedList<Temp>();
            uses.add(f);
            uses.addAll(used);
            emit(new Assem.OPER("\tjalr\t`s0",
                                RiscvFrame.calldefs.clone(),
                                uses.toArray(new Temp[0]), null));
        }
        emit(new Assem.OPER("\t// Call sink", null, RiscvFrame.calldefs.clone(), null));
        return frame.RV();
    }
}