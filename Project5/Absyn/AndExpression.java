package Absyn;

/**
 * Represents binary expression 'AND'
 * It holds two expressions that both must be true for the result to be true
 */

public class AndExpression extends Expr{
    public Expr e1; //left
    public Expr e2; //right

    public AndExpression(Expr e1, Expr e2){
        this.e1 = e1;
        this.e2 = e2;
    }

    public void accept(Visitor v){
        v.visit(this);
    }
}