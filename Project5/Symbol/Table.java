package Symbol;


import java.util.*;

public class Table{
    private Stack<Map<Symbol, Object>> scope;

    public Table(){
        scope = new Stack<>();
        //initialize globlal and class scope
        scope.push(new HashMap<>());
        
    }

    // binding decl to current innermost scope

    public void put(Symbol key, Object val){
        scope.peek().put(key,val);
    }

    //moving outwards for innermost scope, null if nothing is found

    public Object get(Symbol key){
        for(int i = scope.size() - 1; i >= 0; i--){
                Map<Symbol,Object> currentScope = scope.get(i);
                if(currentScope.containsKey(key)){
                    return currentScope.get(key);
                }

        }
        return null;
    }

    //mark hash table with the new nested scope
    public void beginScope(){
        scope.push(new HashMap<>());
    }
    // return to previous scope option
    public void endScope(){
        if(scope.size() > 1){
            scope.pop();
        }
    }
    }