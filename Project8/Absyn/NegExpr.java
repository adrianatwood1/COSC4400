package Absyn;

public class NegExpr extends Expr {
 public Expr ex1;
    public NegExpr(Expr ex1){
        this.ex1 = ex1; 
    
        
    }
    public void accept(Visitor v){
        v.visit(this);
    }
    public Translate.Exp accept(TreeVisitor v){
        return v.visit(this);
    }
}