package Absyn;

/* Represents the value 'true' in expressions */

public class True extends Expr {
    public True() {}

    public void accept(Visitor v){
        v.visit(this);
    }
}