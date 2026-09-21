package Absyn;

/**
 * Represents the "NOT" expression
 * It holds a single sub-expression which truth value is negated
 */
public class Not extends Expr{
    public Expr e; // negates the expression
    
    public Not(Expr e){
        this.e = e;
    }

    public void accept(Visitor v){
        v.visit(this);
    }

}