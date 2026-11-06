package Absyn;

// If statement expressions

public class If extends Stmt {
    public Expr ex; 
    public Stmt st1; 
    public Stmt st2; 

    public If(Expr ex, Stmt st1, Stmt st2){
        this.ex = ex;
        this.st1 = st1;
        this.st2 = st2;
    }

    public void accept(Visitor v){
        v.visit(this);
    }

}
