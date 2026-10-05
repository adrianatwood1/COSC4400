package Types;

import java.util.*;

public class MethodSymbolTable {
    public String name;
    public Type returnType;
    public Map<String, Type> params = new LinkedHashMap<>(); 
    public Map<String, Type> locals = new HashMap<>();
    public ClassSymbolTable parentClass;

    public MethodSymbolTable(String name, Type returnType, ClassSymbolTable parentClass) {
        this.name = name;
        this.returnType = returnType;
        this.parentClass = parentClass;
    }

    public boolean addParam(String paramName, Type type) {
        if (params.containsKey(paramName)) return false; // duplicate parameter
        params.put(paramName, type);
        return true;
    }

    public boolean addLocal(String localName, Type type) {
        if (locals.containsKey(localName) || params.containsKey(localName)) return false; // duplicate local variable
        locals.put(localName, type);
        return true;
    }

    //our shadowing logic by checking inheritance by scope!!!

    public Type lookupVariable(String varName, GlobalSymbolTable global) {
        if (locals.containsKey(varName)) {
            return locals.get(varName);
        }
        if (params.containsKey(varName)) {
            return params.get(varName);
        }
        if (parentClass != null) {
            return parentClass.getField(varName, global);
        }
        return null;
    }
}