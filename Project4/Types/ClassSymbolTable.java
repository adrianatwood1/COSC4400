package Types;

import java.util.*;

public class ClassSymbolTable {
    public String name;
    public String parentClassName;
    
    public Map<String, Type> fields = new LinkedHashMap<>();
    public Map<String, MethodSymbolTable> methods = new LinkedHashMap<>();

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
        if (fields.containsKey(fieldName)) return fields.get(fieldName);
        if (parentClassName != null && global != null) {
            ClassSymbolTable parentTable = global.getClass(parentClassName);
            if (parentTable != null) return parentTable.getField(fieldName, global);
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CLASS(").append(name).append("\n");
        sb.append(" ").append(parentClassName == null ? "null" : parentClassName).append("\n");
        
        // print out methods then fields for the class record
        sb.append(formatRecord(1, methods, true)).append("\n");
        sb.append(formatRecord(1, fields, false)).append("\n");
        
        // do it all again for the object record
        sb.append(" OBJECT(").append(name).append("\n");
        sb.append(formatRecord(2, methods, true)).append("\n");
        sb.append(formatRecord(2, fields, false)).append("))");
        
        return sb.toString();
    }

    
    private String formatType(Type t, int indLvl) {
        String unformattedTypeString = t == null ? "VOID" : t.toString();
        
        // injecting newlines for arrays so they stack exactly like the .out files
        unformattedTypeString = unformattedTypeString.replace("ARRAY(", "ARRAY(\n");
        unformattedTypeString = unformattedTypeString.replace("CLASS", "OBJECT");
        
        String[] parts = unformattedTypeString.split("\n");
        StringBuilder sb = new StringBuilder();
        String currentIndent = "";
        
        // getting the spacing right
        for(int k = 0; k < indLvl; k++) currentIndent += " ";
        
        for (int i = 0; i < parts.length; i++) {
            sb.append(currentIndent).append(parts[i]);
            if (i < parts.length - 1) sb.append("\n");
            currentIndent += " "; 
        }
        return sb.toString();
    }

    // building out the RECORD() dumps with the right indentation levels
    private String formatRecord(int indLvl, Map<String, ?> items, boolean isMethods) {
        String ind = "";
        for (int i = 0; i < indLvl; i++) ind += " ";
        
        // empty record check
        if (items.isEmpty()) return ind + "RECORD()";
        
        StringBuilder sb = new StringBuilder();
        sb.append(ind).append("RECORD(\n");
        int i = 0;
        
        for (Map.Entry<String, ?> entry : items.entrySet()) {
            sb.append(ind).append(" FIELD(").append(i++).append(" ").append(entry.getKey()).append("\n");
            
            if (isMethods) {
                // unpacking the method symbol table
                MethodSymbolTable m = (MethodSymbolTable) entry.getValue();
                sb.append(ind).append("  FUNCTION(").append(m.name).append("\n");
                sb.append(ind).append("   OBJECT(").append(name).append(")\n");
                
                if (m.params.isEmpty()) {
                    sb.append(ind).append("   RECORD()\n");
                } else {
                    sb.append(ind).append("   RECORD(\n");
                    int pIdx = 0;
                    // loop through params, gotta make sure the spacing is exactly right
                    for (Map.Entry<String, Type> p : m.params.entrySet()) {
                        sb.append(ind).append("    FIELD(").append(pIdx++).append(" ").append(p.getKey()).append("\n");
                        sb.append(formatType(p.getValue(), indLvl + 5)).append(")");
                        if (pIdx < m.params.size()) {
                            sb.append("\n");
                        } else {
                            sb.append(")\n");
                        }
                    }
                }
                sb.append(formatType(m.returnType, indLvl + 3)).append(")))");
            } else {
                // standard field dump
                Type t = (Type) entry.getValue();
                sb.append(formatType(t, indLvl + 2)).append(")");
            }
            if (i < items.size()) sb.append("\n");
        }
        sb.append(")");
        return sb.toString();
    }
}