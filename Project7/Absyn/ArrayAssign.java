package Absyn;


//Array assignment statements 

public class ArrayAssign extends Stmt{
    public Identifier i;
    public Expr e1;
    public Expr e2;

    public ArrayAssign(Identifier i, Expr e1, Expr e2){
        this.i = i;
        this.e1 = e1;
        this.e2 = e2; 
    }

    public void accept(Visitor v) { 
        v.visit(this);
    }
}