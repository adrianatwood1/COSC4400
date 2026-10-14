package Absyn;

//deals with the boolean type

public class BooleanType extends Type{

    public BooleanType(){}

    public void accept(Visitor v){
        v.visit(this);
    }
}