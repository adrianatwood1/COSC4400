package Absyn;

 public class NotEqExpr extends Expr {
 public Expr ex1, ex2;
    public NotEqExpr(Expr ex1, Expr ex2){
        this.ex1 = ex1; 
        this.ex2 = ex2;
        
    }
    public void accept(Visitor v){
        v.visit(this);
    }
}