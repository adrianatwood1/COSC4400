package Absyn;

public class NullExpr extends Expr{
    public void accept(Visitor v){
        v.visit(this);
    }
}