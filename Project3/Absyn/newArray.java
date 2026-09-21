package Absyn;

//Making new arrays

public class newArray extends Expr{
    public Expr ex;

    public newArray(Expr ex){
        this.ex = ex;

    }

    public void accept(Visitor v){
        v.visit(this);
    }
}