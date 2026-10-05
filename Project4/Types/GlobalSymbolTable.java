package Types;

import java.util.*;

public class GlobalSymbolTable {
    public Map<String, ClassSymbolTable> classes = new HashMap<>();

    public boolean addClass(String className, ClassSymbolTable classTable) {
        if (classes.containsKey(className)) return false; // Prevents duplicate class decls
        classes.put(className, classTable);
        return true;
    }

    public ClassSymbolTable getClass(String className) {
        return classes.get(className);
    }
}