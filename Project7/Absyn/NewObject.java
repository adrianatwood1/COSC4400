package Absyn;

// making new objects since its an object based lang

public class NewObject extends Expr{

    public Identifier id;

    public NewObject(Identifier id){
        this.id = id;
    }

    public void accept(Visitor v){
        v.visit(this);
    }
}