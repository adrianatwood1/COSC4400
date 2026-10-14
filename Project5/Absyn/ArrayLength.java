package Absyn;

// array.length etc

public class ArrayLength extends Expr{

    public Expr ex;
    public String fName = "length";
    public int typeIndex = -1;

    public ArrayLength(Expr ex){
        this.ex = ex;
    }
    public ArrayLength(Expr ex, String fName){
        this.ex = ex; 
        this.fName = fName;
    }

    public void accept(Visitor v){
        v.visit(this);
    }
}