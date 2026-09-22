package Absyn;

//Handles int type

public class IntegerType extends Type{
    
    public IntegerType(){

    }
    public void accept(Visitor v){
    v.visit(this);
}


}


