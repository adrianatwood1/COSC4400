package Absyn;

/**
 * Represents division expression
 * It holds two expression: (left) numerator on the left and (right) denominator on the right
 */

public class Divide extends Expr{
    public Expr e1; //left
    public Expr e2; //right

    public Divide(Expr e1, Expr e2){
        this.e1 = e1;
        this.e2 = e2;
    }

    public void accept(Visitor v){
        v.visit(this);
    }
}