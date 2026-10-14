package Types;

import java.util.LinkedHashMap;
import java.util.Map;

public class GlobalSymbolTable {
    public Map<String, ClassSymbolTable> classes = new LinkedHashMap<>();

    public boolean addClass(String name, ClassSymbolTable classTable) {
        if (classes.containsKey(name)) {
            return false;
        }
        classes.put(name, classTable);
        return true;
    }

    public ClassSymbolTable getClass(String name) {
        return classes.get(name);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        
        int i = 0;
        for (ClassSymbolTable c : classes.values()) {
            sb.append(c.toString());
            if (i < classes.size() - 1) {
                sb.append("\n\n");
            }
            i++;
        }
        return sb.toString();
    }
}