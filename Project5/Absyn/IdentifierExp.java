package Absyn;

// identify Exp like x and other strings


public class IdentifierExp extends Expr{
    public String st; 
    
    public IdentifierExp(String st){
        this.st = st; 
    }

    public void accept(Visitor v){
        v.visit(this);
    }
}