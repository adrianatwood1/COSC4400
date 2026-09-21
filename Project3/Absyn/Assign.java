package Absyn;
//Assignment Statement

public class Assign extends Stmt { 
    public Identifier id;
    public Expr ex; 

    public Assign(Identifier id, Expr ex){
        this.id = id; 
        this.ex = ex;
    }
}
public void accept(Visitor v){
    v.visit(this);
}