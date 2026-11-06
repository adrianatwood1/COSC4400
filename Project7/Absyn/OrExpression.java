package Absyn;

/**
 * Represents binary expression 'OR'
 * It holds two expressions that at least one must be true for the result to be true
 */

public class OrExpression extends Expr{
    public Expr e1; //left
    public Expr e2; //right

    public OrExpression(Expr e1, Expr e2){
        this.e1 = e1;
        this.e2 = e2;
    }

    public void accept(Visitor v){
        v.visit(this);
    }
}