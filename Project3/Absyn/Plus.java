package Absyn;

/**
 * Represents binary addition in the AST.
 * It will also hold the two expression: one on the left and the other on the right of the '+' operator.
 */

public class Plus extends Expr{
    public Expr e1; //left
    public Expr e2; //right

    public Plus(Expr e1, Expr e2){
        this.e1 = e1;
        this.e2 = e2;
    }

    public void accept(Visitor v){
        v.visit(this);
    }
}
