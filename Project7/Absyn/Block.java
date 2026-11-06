package Absyn;

import java.util.LinkedList;

//Block statement
//block is for chunks of satements within code

public class Block extends Stmt { 
    public LinkedList<Stmt> sl;

    public Block(LinkedList<Stmt> sl){ 
        this.sl = sl;  
    }
    public void accept(Visitor v){
        v.visit(this);
    }
}
