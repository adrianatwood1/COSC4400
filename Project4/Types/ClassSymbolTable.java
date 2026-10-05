package Types;

import java.util.*;

public class ClassSymbolTable {
    public String name;
    public String parentClassName;
    public Map<String, Type> fields = new HashMap<>();
    public Map<String, MethodSymbolTable> methods = new HashMap<>();

    public ClassSymbolTable(String name, String parentClassName) {
        this.name = name;
        this.parentClassName = parentClassName;
    }

    public boolean addField(String fieldName, Type type) {
        if (fields.containsKey(fieldName)) return false; // duplicate field
        fields.put(fieldName, type);
        return true;
    }

    public boolean addMethod(String methodName, MethodSymbolTable method) {
        if (methods.containsKey(methodName)) return false; // duplicate method
        methods.put(methodName, method);
        return true;
    }

    public MethodSymbolTable getMethod(String methodName) {
        return methods.get(methodName);
    }

    public Type getField(String fieldName, GlobalSymbolTable global) {
        if (fields.containsKey(fieldName)) {
            return fields.get(fieldName);
        }
        if (parentClassName != null && global != null) {
            ClassSymbolTable parentTable = global.getClass(parentClassName);
            if (parentTable != null) {
                return parentTable.getField(fieldName, global);
            }
        }
        return null;
    }
}