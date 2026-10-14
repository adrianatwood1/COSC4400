package Absyn;

/**
 * Represents multiplication expression
 * It holds two expression: (left) factor on the left and (right) factor on the right being multiplied by the left
 */

public class Times extends Expr{
    public Expr e1; //left
    public Expr e2; //right

    public Times(Expr e1, Expr e2){
        this.e1 = e1;
        this.e2 = e2;
    }

    public void accept(Visitor v){
        v.visit(this);
    }
}