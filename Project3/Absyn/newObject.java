package Absyn;

// making new objects since its an object based lang

public class newObject extends Expr{

    public Identifier id;

    public newObject(Identifier id){
        this.id = id;
    }

    public void accept(Visitor v){
        v.visit(this);
    }
}