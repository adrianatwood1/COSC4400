package Types;

import java.util.*;

public class GlobalSymbolTable {
    public Map<String, ClassSymbolTable> classes = new LinkedHashMap<>();

    public void addClass(String name, ClassSymbolTable cst) {
        classes.put(name, cst);
    }

    public ClassSymbolTable getClass(String name) {
        return classes.get(name);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n"); 
        int count = 0;
        
        // stringing all the class tables together in order
        for (ClassSymbolTable cst : classes.values()) {
            sb.append(cst.toString());
            count++;
            // only add the double newline if we arent at the very end
            if (count < classes.size()) {
                sb.append("\n\n");
            }
        }
        sb.append("\n");
        return sb.toString();
    }
}