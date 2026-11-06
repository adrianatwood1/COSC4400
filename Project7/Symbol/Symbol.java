package Symbol; 

import java.util.HashMap;

public class Symbol{

    private String name; 
    private static HashMap<String, Symbol> dictionary = new HashMap<String, Symbol>();

    private Symbol(String name){
        this.name = name;
    }

// return a unique symbol for the string
    public static Symbol symbol(String name){
        String uni = name.intern();
        Symbol sym = dictionary.get(uni );
        if (sym == null){
            sym = new Symbol(uni);
            dictionary.put(uni,sym);
        }
        return sym;
    }
    public String toString(){
        return name;
    }




}