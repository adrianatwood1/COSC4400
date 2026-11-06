package Absyn;

//LOOks for an Array like array[i]

public class ArrayLookup extends Expr { 
    public Expr ex1;
    public Expr ex2; 

    public ArrayLookup(Expr ex1, Expr ex2){
        this.ex1 = ex1;
        this.ex2 = ex2;
    }

    public void accept(Visitor v){
        v.visit(this);
    }
}