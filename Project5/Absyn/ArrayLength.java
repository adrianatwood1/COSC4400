package Absyn;

// array.length etc

public class ArrayLength extends Expr{
    public Expr ex;

    public ArrayLength(Expr ex){
        this.ex = ex;
    }

    public void accept(Visitor v){
        v.visit(this);
    }
}