package Absyn;

//calls obj.method()

import java.util.LinkedList;


public class Call extends Expr{
    public Expr ex;
    public Identifier id;
    public LinkedList li;

    public Call(Expr ex, Identifier id, LinkedList li){
        this.ex = ex;
        this.id = id;
        this.li = li;
        
    }

    public void accept(Visitor v){
        v.visit(this);
    }
}