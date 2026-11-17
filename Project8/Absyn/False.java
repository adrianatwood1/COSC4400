package Absyn;

/* Represents the value 'true' in expressions */

public class False extends Expr{
    public False() {}

    public void accept(Visitor v){
        v.visit(this);
    }
    public Translate.Exp accept(TreeVisitor v){
        return v.visit(this);
    }
    
}