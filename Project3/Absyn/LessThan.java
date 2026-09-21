package Absyn;

/**
 * Represents binary expression '<'
 * It holds two expressions that compare evaluating if true when the left side is less than the right side.
 */

public class LessThan extends Expr{
    public Expr e1; //left
    public Expr e2; //right

    public LessThan(Expr e1, Expr e2){
        this.e1 = e1;
        this.e2 = e2;
    }

    public void accept(Visitor v){
        v.visit(this);
    }
}