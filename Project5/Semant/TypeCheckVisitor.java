package Semant;

import Absyn.*;
import Symbol.Symbol;
import Symbol.Table;
import Types.ARRAY;
import Types.BOOLEAN;
import Types.CLASS;
import Types.ClassSymbolTable;
import Types.GlobalSymbolTable;
import Types.INT;
import Types.MethodSymbolTable;
import Types.NIL;
import Types.STRING;
import Types.Type;
import Types.VOID;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;

public class TypeCheckVisitor implements Visitor { 
    private GlobalSymbolTable global;
    private ClassSymbolTable currClass;
    private MethodSymbolTable currMethod;
    private String currClassName; 
    private Type currType; 
    private Table env;
    
    public TypeCheckVisitor(GlobalSymbolTable global) {
        this.global = global;
        this.env = new Table();
    }

        private void error(Visitable ast, String msg) {
        System.out.print("ERROR " + msg + ": ");
        PrintWriter pw = new PrintWriter(System.out, true);
        PrintVisitor pv = new PrintVisitor(pw);
        pv.indentCount = 10;
        pv.priType = false;
        ast.accept(pv);
        pw.flush();
        System.out.println();
        Main.hasError = true;
    }
   
    private boolean isSubclass(Type childType, Type parentType) {
        if (childType == null || parentType == null) return false;
        if (childType instanceof NIL && (parentType instanceof CLASS || parentType instanceof ARRAY)) return true;
        if (childType instanceof STRING && parentType instanceof CLASS && ((CLASS) parentType).name.equals("String")) return true;
        if (childType.toString().equals(parentType.toString())) return true;

        if (childType instanceof CLASS && parentType instanceof CLASS) {
            String childName = ((CLASS) childType).name;
            String parentName = ((CLASS) parentType).name;
            
            ClassSymbolTable childClass = global.getClass(childName);
            while (childClass != null && childClass.parentClassName != null) {
                if (childClass.parentClassName.equals(parentName)) {
                    return true;
                }
                childClass = global.getClass(childClass.parentClassName);
            }
        }
        return false;
    }

    //helper to check block's scope first, then pass symbols
    private Type resolveVars(String name){
        Type ty = (Type) env.get(Symbol.symbol(name));
        if(ty != null){
            return ty;
        }
        if (currMethod != null) return currMethod.lookupVariable(name, global);
        return currClass.getField(name, global);
    }

    // helping to translate the input into types 
    private String formatType(Type t) {
        if (t == null) return "void";
        if (t instanceof Types.INT) return "int";
        if (t instanceof Types.BOOLEAN) return "boolean";
        if (t instanceof Types.STRING) return "STRING:String";
        if (t instanceof Types.CLASS) return "OBJECT:" + ((Types.CLASS)t).name;
        if (t instanceof Types.ARRAY) return formatType(((Types.ARRAY)t).element) + "[]";
        if (t instanceof Types.NIL) return "NIL";
        if (t instanceof Types.VOID) return "void";
        return t.toString();
    }

    //scope managers

    public void visit(Program ast) {
        for (int i = 0; i < ast.classes.size(); i++) {
            if (ast.classes.get(i) != null) {
                ((Visitable) ast.classes.get(i)).accept(this);
            }
        }
    }

    public void visit(ClassDecl ast) {
        currClassName = ast.name;
        currClass = (ast.symbolTable != null) ? ast.symbolTable : global.getClass(ast.name);
        for (int i = 0; i < ast.fields.size(); i++) {
            if (ast.fields.get(i) != null) {
                ((Visitable) ast.fields.get(i)).accept(this);
            }
        }
        for (int i = 0; i < ast.methods.size(); i++) {
            if (ast.methods.get(i) != null) {
                ((Visitable) ast.methods.get(i)).accept(this);
            }
        }
        currClass = null;
    }

    public void visit(MethodDecl ast) {
        currMethod = currClass.getMethod(ast.name);
        if(currMethod == null) return;
        env.beginScope();
        // Visit parameters
        for (int i = 0; i < ast.params.size(); i++) {
            if (ast.params.get(i) != null) {
                ((Visitable) ast.params.get(i)).accept(this);
            }
        }
        // Visit local variables
        for (int i = 0; i < ast.locals.size(); i++) {
            if (ast.locals.get(i) != null) {
                ((Visitable) ast.locals.get(i)).accept(this);
            }
        }
        // Visit statements
        for (int i = 0; i < ast.stmts.size(); i++) {
            if (ast.stmts.get(i) != null) {
                ((Visitable) ast.stmts.get(i)).accept(this);
            }
        }
        //Error checking
        if (ast.returnVal != null) {
            ast.returnVal.accept(this);
            if (currType != null && currMethod.returnType != null && !ast.name.equals("main")) {
                boolean isValid = !(currType instanceof VOID) && (currType.coerceTo(currMethod.returnType) || isSubclass(currType, currMethod.returnType));
                if (!isValid) {
                    error(ast.returnVal, "incompatible types: " + formatType(currMethod.returnType) + " required, but " + formatType(currType) + " found");
                }
            }
        }
        env.endScope();
        currMethod = null;
    }

    public void visit(VarDecl ast){
        ast.type.accept(this);
        Type declType = currType;
        ast.checktype = declType;
        if (currMethod != null) {
            env.put(Symbol.symbol(ast.name), declType);
        }
        if (ast.init != null) {
            ast.init.accept(this);
            if (!(currType.coerceTo(declType) || isSubclass(currType, declType))) {
                error(ast.init, "incompatible types: " + formatType(declType) + " required, but " + formatType(currType) + " found");
            }
        }
    }

    public void visit(Formal ast){
        ast.type.accept(this);
        Type declType = currType;
        ast.checktype = declType;
        env.put(Symbol.symbol(ast.name), declType);
    }

    public void visit(Call ast) {
        ast.ex.accept(this);
        Type objType = currType;

        ArrayList<Type> argTypes = new ArrayList<Type>();
        if (ast.li != null) {
            for (int i = 0; i < ast.li.size(); i++) {
                if (ast.li.get(i) != null) {
                    ((Visitable) ast.li.get(i)).accept(this);
                    argTypes.add(currType);
                }
            }
        }
        if (objType instanceof CLASS) {
            String className = ((CLASS) objType).name;
            ClassSymbolTable targetClass = global.getClass(className);

            if (targetClass != null) {
                MethodSymbolTable targetMethod = targetClass.getMethod(ast.id.s);
                if (targetMethod != null) {
                    ast.typeIndex = targetClass.getMethodIndex(ast.id.s);
                    ArrayList<Type> paramTypes = new ArrayList<Type>(targetMethod.params.values());
                    if (argTypes.size() != paramTypes.size()) {
                        error(ast, "mismatch in number of arguments");
                    } else {
                        for (int i = 0; i < argTypes.size(); i++) {
                            Type req = paramTypes.get(i);
                            Type act = argTypes.get(i);
                            if (!(act.coerceTo(req) || isSubclass(act, req))) {
                                error((Visitable) ast.li.get(i), "incompatible types: " + formatType(req) + " required, but " + formatType(act) + " found");
                            }
                        }
                    }
                    currType = targetMethod.returnType;
                    return; 
                } else {
                    error(ast, "cannot resolve method " + ast.id.s);
                }
            }
        } else {
            error(ast, "target not object, type " + formatType(objType));
        }
        currType = new VOID();
    }

    //statement checking

    public void visit(Assign ast) {
        Type lhsType = resolveVars(ast.id.s);
        if (lhsType == null) {
            error(new IdentifierExp(ast.id.s), "cannot resolve symbol " + ast.id.s);
            lhsType = new VOID();
        }
        ast.ex.accept(this);
        
        boolean isValid = currType != null && !(currType instanceof VOID) && !(lhsType instanceof VOID)
                          && (currType.coerceTo(lhsType) || isSubclass(currType, lhsType));
        
        if (!isValid) {
            error(ast, "incompatible types: " + formatType(lhsType) + " required, but " + formatType(currType) + " found");
        }
    }

    public void visit(ArrayAssign ast) {
        Type arrType = resolveVars(ast.i.s);
        ast.e1.accept(this);
        if (!(currType instanceof INT)) {
            error(ast.e1, "incompatible types: int required, but " + formatType(currType) + " found");
        }
        ast.e2.accept(this);
        Type elemType = (arrType instanceof ARRAY) ? ((ARRAY) arrType).element : new VOID();
        if (!(currType.coerceTo(elemType) || isSubclass(currType, elemType))) {
            error(ast, "incompatible types: " + formatType(elemType) + " required, but " + formatType(currType) + " found");
        }
    }

    public void visit(If ast) {
        ast.ex.accept(this);
        if (!(currType instanceof BOOLEAN)) {
            error(ast, "incompatible types: boolean required, but " + formatType(currType) + " found");
        }
        ast.st1.accept(this);
        if (ast.st2 != null) {
            ast.st2.accept(this);
        }
    }

    public void visit(While ast) {
        ast.ex.accept(this);
        if (!(currType instanceof BOOLEAN)) {
            error(ast, "incompatible types: boolean required, but " + formatType(currType) + " found");
        }
        ast.st.accept(this);
    }

    public void visit(Block ast) {
        env.beginScope();
        for (int i = 0; i < ast.sl.size(); i++) {
            if (ast.sl.get(i) != null) {
                ((Visitable) ast.sl.get(i)).accept(this);
            }
        }
        env.endScope();
    }

    //expression evaluation

    public void visit(IdentifierExp ast) {
        if (ast.st.equals("Xinu")) {
            currType = new CLASS("Xinu");
            return;
        }
        Type varType = resolveVars(ast.st);
        if (varType == null) {
            error(ast, "cannot resolve symbol " + ast.st);
            currType = new VOID();
        } else {
            currType = varType;
        }
    }

    public void visit(Plus ast) {
        ast.e1.accept(this);
        Type t1 = currType;
        ast.e2.accept(this);
        Type t2 = currType;
        if (!(t1 instanceof INT) || !(t2 instanceof INT)) {
            error(ast, "operator + cannot be applied to " + formatType(t1) + ", " + formatType(t2));
        }
        currType = new INT(); 
    }

    private void binary(Visitable node, Expr e1, Expr e2, String op, boolean wantInt, Type result) {
        e1.accept(this); Type t1 = currType;
        e2.accept(this); Type t2 = currType;
        boolean ok = wantInt ? (t1 instanceof INT && t2 instanceof INT) : (t1 instanceof BOOLEAN && t2 instanceof BOOLEAN);
        if (!ok) error(node, "operator " + op + " cannot be applied to " + formatType(t1) + ", " + formatType(t2));
        currType = result;
    }

    public void visit(Minus ast)         { binary(ast, ast.e1, ast.e2, "-",  true,  new INT()); }
    public void visit(Times ast)         { binary(ast, ast.e1, ast.e2, "*",  true,  new INT()); }
    public void visit(Divide ast)        { binary(ast, ast.e1, ast.e2, "/",  true,  new INT()); }

    public void visit(LessThan ast) {
        ast.e1.accept(this);
        Type t1 = currType;
        ast.e2.accept(this);
        Type t2 = currType;
        if (!(t1 instanceof INT) || !(t2 instanceof INT)) {
            error(ast, "operator < cannot be applied to " + formatType(t1) + ", " + formatType(t2));
        }
        currType = new BOOLEAN();
    }

    public void visit(GreaterThan ast)   { binary(ast, ast.e1, ast.e2, ">",  true,  new BOOLEAN()); }
    public void visit(AndExpression ast) { binary(ast, ast.e1, ast.e2, "&&", false, new BOOLEAN()); }
    public void visit(OrExpression ast)  { binary(ast, ast.e1, ast.e2, "||", false, new BOOLEAN()); }
    public void visit(EqualExpr ast) { ast.ex1.accept(this); ast.ex2.accept(this); currType = new BOOLEAN(); }
    public void visit(NotEqExpr ast) { ast.ex1.accept(this); ast.ex2.accept(this); currType = new BOOLEAN(); }
    public void visit(Not ast) {
    ast.e.accept(this);
    if (!(currType instanceof BOOLEAN))
        error(ast, "operator ! cannot be applied to " + formatType(currType));
    currType = new BOOLEAN();
}
    public void visit(NegExpr ast) {
        ast.ex1.accept(this);
        if (!(currType instanceof INT)) {
            error(ast, "operator - cannot be applied to " + formatType(currType));
        }
        currType = new INT();
    }

    public void visit(ArrayLength ast) {
        ast.ex.accept(this);
        if (ast.fName.equals("length") && currType instanceof ARRAY) {
            currType = new INT();
        } else if (currType instanceof CLASS) {
            ClassSymbolTable cst = global.getClass(((CLASS) currType).name);
            Type fType = (cst != null) ? cst.getField(ast.fName, global) : null;
            if (fType != null) {
                ast.typeIndex = cst.getFieldIndex(ast.fName);
                currType = fType;
            } else {
                error(ast, "cannot resolve field " + ast.fName);
                currType = new VOID();
            }
        } else {
            error(ast, "target not object, type " + formatType(currType));
            currType = new VOID();
        }
    }

    public void visit(ArrayLookup ast) {
        ast.ex1.accept(this);
        Type arrType = currType;
        ast.ex2.accept(this);
        if (!(currType instanceof INT)) {
            error(ast.ex2, "incompatible types: int required, but " + formatType(currType) + " found");
        }
        if (!(arrType instanceof ARRAY)) {
            error(ast, "incompatible types: array required, but " + formatType(arrType) + " found");
            currType = new VOID();
        } else {
            currType = ((ARRAY) arrType).element;
        }
    }

    public void visit(NewArray ast) {
        ast.ex.accept(this);
        if (!(currType instanceof INT)) {
            error(ast.ex, "incompatible types: int required, but " + formatType(currType) + " found");
        }
        currType = new INT();
        for (int i = 0; i < ast.dimensions.size(); i++) {
            currType = new ARRAY(currType);
        }
    }

    public void visit(NewObject ast) {
        if (global.getClass(ast.id.s) == null) {
            System.out.println("ERROR cannot resolve class " + ast.id.s + ": IdentifierType(" + ast.id.s + ")");
            Main.hasError = true;
        }
        currType = new CLASS(ast.id.s);
    }

    public void visit(XinuCallStmt ast) {
        ArrayList<Type> argTypes = new ArrayList<Type>();
        if (ast.args != null) {
            for (int i = 0; i < ast.args.size(); i++) {
                if (ast.args.get(i) != null) {
                    ((Visitable) ast.args.get(i)).accept(this);
                    argTypes.add(currType);
                }
            }
        }
        MethodSymbolTable m = global.getClass("Xinu").getMethod(ast.method);
        if (m != null) {
            ArrayList<Type> paramTypes = new ArrayList<Type>(m.params.values());
            if (argTypes.size() != paramTypes.size()) {
                error(ast, "mismatch in number of arguments");
            } else {
                    for (int i = 0; i < argTypes.size(); i++) {
                    Type req = paramTypes.get(i);
                    Type act = argTypes.get(i);
                    if (!(act.coerceTo(req) || isSubclass(act, req))) {
                        error((Visitable) ast.args.get(i), "incompatible types: " + formatType(req) + " required, but " + formatType(act) + " found");
                    }
                }
            }
        }
    }

    public void visit(XinuCallExpr ast) {
        visit(new Call(new IdentifierExp("Xinu"), new Identifier(ast.method), ast.args));
    }

    public void visit(IntegerLiteral ast) {
        currType = new INT();
    }

    public void visit(True ast) {
        currType = new BOOLEAN();
    }

    public void visit(False ast) {
        currType = new BOOLEAN();
    }

    //not fully implemented type setters yet
    public void visit(java.util.AbstractList list) {}
    public void visit(StringLiteral ast) { currType = new STRING(); }
    public void visit(NullExpr ast) { currType = new NIL(); }
    public void visit(Identifier ast) {}
    
    public void visit(IdentifierType ast) {
        if (ast.id.equals("String")) {
            currType = new STRING();
        } else {
            if (global.getClass(ast.id) == null && !ast.id.equals("Thread")) {
                System.out.println("ERROR cannot resolve class " + ast.id + ": IdentifierType(" + ast.id + ")");
                Main.hasError = true;
            }
            currType = new CLASS(ast.id);
        }
    }

    public void visit(ArrayType ast) {
        ast.base.accept(this);
        currType = new ARRAY(currType);
    }

    public void visit(IntegerType ast) {
        currType = new INT();
    }

    public void visit(BooleanType ast) {
        currType = new BOOLEAN(); 
    }

    public void visit(This ast) {
        if (currClassName != null) {
            currType = new CLASS(currClassName);
        }
    }
}