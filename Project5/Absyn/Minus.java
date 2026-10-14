package Absyn;

/**
 * Represents a binary subtraction expression
 * It then holds two expression: (left) expression being subtracted, (right) expression being subtracted by
 */

public class Minus extends Expr{
    public Expr e1; //left
    public Expr e2; //right 

    public Minus(Expr e1, Expr e2){
        this.e1 = e1;
        this.e2 = e2;
    }

    public void accept(Visitor v){
        v.visit(this);
    }
}