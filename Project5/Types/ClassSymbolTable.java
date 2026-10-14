package Types;

import java.util.*;

public class ClassSymbolTable {
    public String name;
    public String parentClassName;
    
    public List<FieldRecord> fields = new ArrayList<>();
    public Map<String, MethodSymbolTable> methods = new LinkedHashMap<>();
    
    public static class FieldRecord {
        public String name;
        public Type type;
        public FieldRecord(String name, Type type) {
            this.name = name;
            this.type = type;
        }
    }

    public List<FieldRecord> objectFields = new ArrayList<>();
    public Map<String, MethodSymbolTable> objectMethods = new LinkedHashMap<>();
    private boolean isAssembled = false;

    public ClassSymbolTable(String name, String parentClassName) {
        this.name = name;
        this.parentClassName = parentClassName;
    }

    public boolean addField(String fieldName, Type type) {
        fields.add(new FieldRecord(fieldName, type));
        return true;
    }

    public boolean addMethod(String methodName, MethodSymbolTable method) {
        if (methods.containsKey(methodName)) return false; 
        methods.put(methodName, method);
        return true;
    }

    public MethodSymbolTable getMethod(String methodName) {
        return methods.get(methodName);
    }

    public Type getField(String fieldName, GlobalSymbolTable global) {
        for (FieldRecord fr : fields) {
            if (fr.name.equals(fieldName)) return fr.type;
        }
        if (parentClassName != null && global != null) {
            ClassSymbolTable parentTable = global.getClass(parentClassName);
            if (parentTable != null) return parentTable.getField(fieldName, global);
        }
        return null;
    }

    public void assembleInheritance(GlobalSymbolTable global) {
        if (isAssembled) return; 
        
        if (parentClassName != null && global != null) {
            ClassSymbolTable parent = global.getClass(parentClassName);
            if (parent != null) {
                parent.assembleInheritance(global); 
                objectFields.addAll(parent.objectFields);
                objectMethods.putAll(parent.objectMethods);
            }
        }
        
        for (FieldRecord fr : fields) {
            objectFields.add(new FieldRecord(fr.name, fr.type));
        }
        
        objectMethods.putAll(this.methods);
        isAssembled = true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CLASS(").append(name).append("\n");
        sb.append(" ").append(parentClassName == null ? "null" : parentClassName).append("\n");
        
        sb.append(formatRecord(1, methods, true)).append("\n");
        sb.append(formatRecord(1, fields)).append("\n");
        
        sb.append(" OBJECT(").append(name).append("\n");
        sb.append(formatRecord(2, objectMethods, true)).append("\n");
        sb.append(formatRecord(2, objectFields)).append("))");
        
        return sb.toString();
    }

    private String formatType(Type t, int indLvl) {
        String s = (t == null) ? "void" : t.toString();
        return formatTypeStr(s, indLvl);
    }

    private String formatTypeStr(String s, int indLvl) {
        String ind = "";
        for (int k = 0; k < indLvl; k++) ind += " ";
        
        s = s.replace("CLASS(", "").replace("OBJECT(", "").replace(")", "");

        if (s.endsWith("[]")) {
            String base = s.substring(0, s.length() - 2);
            return ind + "ARRAY(\n" + formatTypeStr(base, indLvl + 1) + ")";
        } 
        else if (s.equals("int") || s.equals("INT")) {
            return ind + "INT";
        } else if (s.equals("boolean") || s.equals("BOOLEAN")) {
            return ind + "BOOLEAN";
        } else if (s.equals("void") || s.equals("VOID")) {
            return ind + "VOID";
        } 
        else {
            return ind + "OBJECT(" + s + ")";
        }
    }

    private String formatRecord(int indLvl, Map<String, ?> items, boolean isMethods) {
        String ind = "";
        for (int i = 0; i < indLvl; i++) ind += " ";
        
        if (items.isEmpty()) return ind + "RECORD()";
        
        StringBuilder sb = new StringBuilder();
        sb.append(ind).append("RECORD(\n");
        int i = 0;
        
        for (Map.Entry<String, ?> entry : items.entrySet()) {
            sb.append(ind).append(" FIELD(").append(i++).append(" ").append(entry.getKey()).append("\n");
            
            if (isMethods) {
                MethodSymbolTable m = (MethodSymbolTable) entry.getValue();
                sb.append(ind).append("  FUNCTION(").append(m.name).append("\n");
                
                String ownerClass = (m.parentClass != null) ? m.parentClass.name : name;
                sb.append(ind).append("   OBJECT(").append(ownerClass).append(")\n"); 
                
                if (m.params.isEmpty()) {
                    sb.append(ind).append("   RECORD()\n");
                } else {
                    sb.append(ind).append("   RECORD(\n");
                    int pIdx = 0;
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
                sb.append(formatType(m.returnType, indLvl + 3)).append("))");
            } else {
                Type t = (Type) entry.getValue();
                sb.append(formatType(t, indLvl + 2)).append(")");
            }
            if (i < items.size()) sb.append("\n");
        }
        sb.append(")");
        return sb.toString();
    }

    private String formatRecord(int indLvl, List<FieldRecord> items) {
        String ind = "";
        for (int i = 0; i < indLvl; i++) ind += " ";
        
        if (items.isEmpty()) return ind + "RECORD()";
        
        StringBuilder sb = new StringBuilder();
        sb.append(ind).append("RECORD(\n");
        int i = 0;
        
        for (FieldRecord entry : items) {
            sb.append(ind).append(" FIELD(").append(i++).append(" ").append(entry.name).append("\n");
            sb.append(formatType(entry.type, indLvl + 2)).append(")");
            if (i < items.size()) sb.append("\n");
        }
        sb.append(")");
        return sb.toString();
    }
}