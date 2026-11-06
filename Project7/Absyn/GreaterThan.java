package Absyn;

/**
 * Represents binary expression '>'
 * It holds two expressions that compare if the left hand side is greater than the right side.
 */

public class GreaterThan extends Expr{
    public Expr e1; //left
    public Expr e2; //right

    public GreaterThan(Expr e1, Expr e2){
        this.e1 = e1;
        this.e2 = e2;
    }

    public void accept(Visitor v){
        v.visit(this);
    }
}