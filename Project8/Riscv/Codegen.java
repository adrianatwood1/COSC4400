package Riscv

import java.util.LinkedList;
import java.util.List;
import Assem.Instr;
import Temp.Temp;

public class Codegen implements Tree.CodeVisitor
{
    private RiscvFrame frame;
    private LinkedList<Instr> ilist = new LinkedList<Instr>();
    
    public Codegen(RiscvFrame frame){
        this.frame = frame;
    }
    public List<Instr> instrs() {
        return ilist;
    }

    private void emit(Instr i){
        ilist.add(i);
    }
    
    public void visit(Tree.SEQ n)   { throw new Error("SEQ in codegen"); }
    public void visit(Tree.LABEL n) { throw new Error("TODO"); }
    public void visit(Tree.JUMP n)  { throw new Error("TODO"); }
    public void visit(Tree.CJUMP n) { throw new Error("TODO"); }
    public void visit(Tree.MOVE n)  { throw new Error("TODO"); }
    public void visit(Tree.EXP n)   { throw new Error("TODO"); }
    public Temp visit(Tree.ESEQ n)  { throw new Error("ESEQ in codegen"); }
    public Temp visit(Tree.BINOP n) { throw new Error("TODO"); }
    public Temp visit(Tree.MEM n)   { throw new Error("TODO"); }
    public Temp visit(Tree.TEMP n)  { throw new Error("TODO"); }
    public Temp visit(Tree.NAME n)  { throw new Error("TODO"); }
    public Temp visit(Tree.CONST n) { throw new Error("TODO"); }
    public Temp visit(Tree.CALL n)  { throw new Error("TODO"); }

}