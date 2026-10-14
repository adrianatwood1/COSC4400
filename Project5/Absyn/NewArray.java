package Absyn;
import java.util.*;

//Making new arrays

public class NewArray extends Expr{
    public Expr ex;
    public LinkedList<Expr> dimensions;

    public NewArray(Expr ex){
        this.ex = ex;
        this.dimensions = new LinkedList<Expr>();
        this.dimensions.add(ex);
    }
    public NewArray(Expr ex, int emptyDimensions){
        this.ex = ex;
        this.dimensions = new LinkedList<Expr>();
        this.dimensions.add(ex);
        for (int i = 0; i < emptyDimensions; i++){
            this.dimensions.add(null);
        }
    }

    public void accept(Visitor v){
        v.visit(this);
    }
}