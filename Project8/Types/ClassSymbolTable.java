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
        // Make sure we don't add duplicate fields in the same class
        for (FieldRecord fr : fields) {
            if (fr.name.equals(fieldName)) return false;
        }
        fields.add(new FieldRecord(fieldName, type));
        return true;
    }

    public boolean addMethod(String methodName, MethodSymbolTable method) {
        if (methods.containsKey(methodName)) return false; 
        methods.put(methodName, method);
        return true;
    }

    public MethodSymbolTable getMethod(String methodName) {
        if (objectMethods.containsKey(methodName)) return objectMethods.get(methodName);
        return methods.get(methodName);
    }

    public int getMethodIndex(String methodName) {
        int idx = 0;
        for (String k : objectMethods.keySet()) {
            if (k.equals(methodName)) return idx;
            idx++;
        }
        idx = 0;
        for (String k : methods.keySet()) {
            if (k.equals(methodName)) return idx;
            idx++;
        }
        return 0;
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

    public int getFieldIndex(String fieldName) {
        for (int i = objectFields.size() - 1; i >= 0; i--) {
            if (objectFields.get(i).name.equals(fieldName)) return i;
        }
        for (int i = fields.size() - 1; i >= 0; i--) {
            if (fields.get(i).name.equals(fieldName)) return i;
        }
        return -1;
    }

    public void assembleInheritance(GlobalSymbolTable global) {
        if (isAssembled) return; 
        
        // check using a visited set
        ClassSymbolTable curr = this;
        Set<String> visited = new HashSet<>();
        while (curr != null) {
                        if (!visited.add(curr.name)) {
                if (curr == this) {
                    System.out.println("ERROR cyclic inheritance involving " + this.name + ": line not available");
                    Semant.Main.hasError = true;
                }
                this.isAssembled = true;   // skip silently if we only lead into a cycle
                return;
            }
            if (curr.parentClassName == null) break;
            ClassSymbolTable nextParent = global.getClass(curr.parentClassName);
            if (nextParent == null && curr == this) {
                System.out.println("ERROR cannot resolve parent class: " + curr.parentClassName + ": line not available");
                Semant.Main.hasError = true;
                this.isAssembled = true;
                return;
            }
            curr = nextParent;
        }

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
        
        // Validate overrides and merge methods
        for (Map.Entry<String, MethodSymbolTable> entry : this.methods.entrySet()) {
            String mName = entry.getKey();
            MethodSymbolTable childM = entry.getValue();
            if (objectMethods.containsKey(mName)) {
                MethodSymbolTable parentM = objectMethods.get(mName);
                if (!isValidOverride(childM, parentM, global)) {
                    System.out.println("ERROR incompatible method override: " + mName + " in class " + this.name + ": line not available");
                    Semant.Main.hasError = true;
                }
            }
            objectMethods.put(mName, childM);
        }
        isAssembled = true;
    }

    private boolean isValidOverride(MethodSymbolTable childM, MethodSymbolTable parentM, GlobalSymbolTable global) {
        if (childM.params.size() != parentM.params.size()) return false;
        Iterator<Type> cIt = childM.params.values().iterator();
        Iterator<Type> pIt = parentM.params.values().iterator();
        while (cIt.hasNext() && pIt.hasNext()) {
            if (!cIt.next().toString().equals(pIt.next().toString())) return false;
        }
        return isSubtype(childM.returnType, parentM.returnType, global);
    }

    private boolean isSubtype(Type childT, Type parentT, GlobalSymbolTable global) {
        if (childT == null || parentT == null) return childT == parentT;
        if (childT.toString().equals(parentT.toString())) return true;
        if (childT instanceof CLASS && parentT instanceof CLASS) {
            String cName = ((CLASS) childT).name;
            String pName = ((CLASS) parentT).name;
            ClassSymbolTable cTable = global.getClass(cName);
            Set<String> seen = new HashSet<>();
            while (cTable != null && cTable.parentClassName != null && seen.add(cTable.name)) {
                if (cTable.parentClassName.equals(pName)) return true;
                cTable = global.getClass(cTable.parentClassName);
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return toString(0);
    }

    public String toString(int baseInd) {
        String spaced = "";
        for (int i = 0; i < baseInd; i++) spaced += " ";
        StringBuilder sb = new StringBuilder();
        sb.append(spaced).append("CLASS(").append(name).append("\n");
        sb.append(spaced).append(" ").append(parentClassName == null ? "null" : parentClassName).append("\n");
        
        sb.append(formatRecord(baseInd + 1, methods, true)).append("\n");
        sb.append(formatRecord(baseInd + 1, fields)).append("\n");
        
        sb.append(spaced).append(" OBJECT(").append(name).append("\n");
        sb.append(formatRecord(baseInd + 2, objectMethods, true)).append("\n");
        sb.append(formatRecord(baseInd + 2, objectFields)).append("))");
        
        return sb.toString();
    }
    // gemini was used to help brainstorm with formating methods... written on our own.
    public String formatMethod(MethodSymbolTable m, int baseInd) {
        String ind = "";
        for (int i = 0; i < baseInd; i++) ind += " ";
        StringBuilder sb = new StringBuilder();
        sb.append(ind).append("FUNCTION(").append(m.name).append("\n");
        String ownerClass = name;
        if(m.parentClass != null) ownerClass = m.parentClass.name;
        sb.append(ind).append(" OBJECT(").append(ownerClass).append(")\n");
        if (m.params.isEmpty()) {
            sb.append(ind).append(" RECORD()\n");
        } else {
            sb.append(ind).append(" RECORD(\n");
            int pIdx = 0;
            for (Map.Entry<String, Type> p : m.params.entrySet()) {
                sb.append(ind).append("  FIELD(").append(pIdx++).append(" ").append(p.getKey()).append("\n");
                sb.append(formatType(p.getValue(), baseInd + 3)).append(")");
                if (pIdx < m.params.size()) {
                    sb.append("\n");
                } else {
                    sb.append(")\n");
                }
            }
        }
        sb.append(formatType(m.returnType, baseInd + 1)).append(")");
        return sb.toString();
    }

    public static String formatTypeStatic(Type t, int indLvl) {
        String s = (t == null) ? "void" : t.toString();
        return formatTypeStrStatic(s, indLvl);
    }

    private static String formatTypeStrStatic(String s, int indLvl) {
        String ind = "";
        for (int k = 0; k < indLvl; k++) ind += " ";
                s = s.replace("STRING:", "").replace("CLASS(", "").replace("OBJECT(", "").replace(")", "");
                if (s.endsWith("[]")) {
            String base = s.substring(0, s.length() - 2);
            return ind + "ARRAY(\n" + formatTypeStrStatic(base, indLvl + 1) + ")";
        } else if (s.equals("int") || s.equals("INT")) {
            return ind + "INT";
        } else if (s.equals("boolean") || s.equals("BOOLEAN")) {
            return ind + "BOOLEAN";
        } else if (s.equals("void") || s.equals("VOID")) {
            return ind + "VOID";
        } else {
            return ind + "OBJECT(" + s + ")";
        }
    }

    private String formatType(Type t, int indLvl) {
        return formatTypeStatic(t, indLvl);
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