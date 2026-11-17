package Absyn;

/* Represents 'this' pointer inside class methods to reference the present objects in the method
 */
public class This extends Expr {
    public This() {}

    public void accept(Visitor v){
        v.visit(this);
    }
    public Translate.Exp accept(TreeVisitor v){
        return v.visit(this);
    }
}