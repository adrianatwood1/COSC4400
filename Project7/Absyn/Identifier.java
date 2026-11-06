package Absyn;

/* Takes the string and holds the variable, method or class identifier so it can reference the specific symbols
 */

public class Identifier {
    public String s;

    public Identifier(String s){
        this.s = s;
    }

    public void accept(Visitor v){
        v.visit(this);
    }

}