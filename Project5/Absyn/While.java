package Absyn;

//While Statements

public class While extends Stmt{
    public Expr ex;
    public Stmt st; 

    public While(Expr ex, Stmt st){
        this.ex = ex;
        this.st = st;

    }

    public void accept(Visitor v){
        v.visit(this);
    }
}