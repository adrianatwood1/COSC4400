package Absyn;

//Making new arrays

public class NewArray extends Expr{
    public Expr ex;

    public NewArray(Expr ex){
        this.ex = ex;

    }

    public void accept(Visitor v){
        v.visit(this);
    }
}