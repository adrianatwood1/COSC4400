package Semant;

import Absyn.*;
import Types.ARRAY;
import Types.BOOLEAN;
import Types.CLASS;
import Types.ClassSymbolTable;
import Types.GlobalSymbolTable;
import Types.INT;
import Types.MethodSymbolTable;
import Types.STRING;
import Types.Type;
import Types.VOID;

public class BuildSymbolVisitor implements Visitor { 
    private GlobalSymbolTable global;
    private ClassSymbolTable currClass;
    private MethodSymbolTable currMethod;

    public BuildSymbolVisitor(GlobalSymbolTable global) {
        this.global = global;
    }
    // helps printing out errors to our AST
    private void error(Visitable ast, String msg) {
        System.out.print("ERROR " + msg + ": ");
        java.io.PrintWriter pw = new java.io.PrintWriter(System.out, true);
        PrintVisitor pv = new PrintVisitor(pw);
        pv.indentCount = 10;
        pv.priType = false;
        ast.accept(pv);
        pw.flush();
        System.out.println();
        Main.hasError = true;
    }
    
    private Type extractType(Object astType) { 
        if (astType == null) return new VOID(); 
        if (astType instanceof IntegerType) return new INT();
        if (astType instanceof BooleanType) return new BOOLEAN();
        if (astType instanceof IdentifierType) {
            String id = ((IdentifierType) astType).id;
            if (id.equals("String")) return new STRING();
            return new CLASS(id);
        }
        if (astType instanceof ArrayType) return new ARRAY(extractType(((ArrayType) astType).base));
        return new VOID();
    }

    //scope managers

    public void visit(Program ast) {
        makeXinuType();
        for (int i = 0; i < ast.classes.size(); i++) {
            if (ast.classes.get(i) != null) {
                ((Visitable) ast.classes.get(i)).accept(this);
            }
        }
    }

    public void visit(ClassDecl ast) {
        currClass = new ClassSymbolTable(ast.name, ast.parent);
        ast.symbolTable = currClass;
        if (!global.addClass(ast.name, currClass)) {
            System.out.println("ERROR duplicate class: " + ast.name + ": line not available");
            Main.hasError = true;
        }

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
        Type retType = extractType(ast.returnType);
        currMethod = new MethodSymbolTable(ast.name, retType, currClass);
        ast.symbolTable = currMethod;
        currClass.addMethod(ast.name, currMethod);

        for (int i = 0; i < ast.params.size(); i++) {
            if (ast.params.get(i) != null) {
                ((Visitable) ast.params.get(i)).accept(this);
            }
        }

        for (int i = 0; i < ast.locals.size(); i++) {
            if (ast.locals.get(i) != null) {
                ((Visitable) ast.locals.get(i)).accept(this);
            }
        }
        currMethod = null;
    }

    public void visit(VarDecl ast) {
        Type t = extractType(ast.type);
        ast.checktype = t;
        if (currMethod != null) {
            boolean added = currMethod.addLocal(ast.name, t);
            if (!added) {
                error(ast, ast.name + " is already defined in " + currMethod.name);
            }
        } else if (currClass != null) {
            boolean added = currClass.addField(ast.name, t);
            if (!added) {
                error(ast, ast.name + " is already defined in " + currClass.name);
            }
        }
    }

    public void visit(Formal ast) {
        Type t = extractType(ast.type);
        ast.checktype = t;
        boolean added = currMethod.addParam(ast.name, t);
        if (!added) {
            error(ast, ast.name + " is already defined in " + currMethod.name);
        }
    }

    private void makeXinuType() {
        // creating xinu class
        currClass = new Types.ClassSymbolTable("Xinu", null);
        global.classes.put("Xinu", currClass);
        java.util.LinkedList<MethodDecl> xinuMethods = new java.util.LinkedList<MethodDecl>();
        // Empty lists to prevent NullPointerExceptions during visitation
        java.util.LinkedList<VarDecl> emptyLocals = new java.util.LinkedList<VarDecl>();
        java.util.LinkedList<Stmt> emptyStmts = new java.util.LinkedList<Stmt>();
        
        // print
        java.util.LinkedList<Formal> params = new java.util.LinkedList<Formal>();
        params.add(new Formal(new IdentifierType("String"), "arg"));
        xinuMethods.add(new MethodDecl(null, false, "print", params, emptyLocals, emptyStmts, null));
        // printint
        params = new java.util.LinkedList<Formal>();
        params.add(new Formal(new IntegerType(), "arg"));
        xinuMethods.add(new MethodDecl(null, false, "printint", params, emptyLocals, emptyStmts, null));
        // sleep 
        xinuMethods.add(new MethodDecl(null, false, "sleep", params, emptyLocals, emptyStmts, null));
        // readint()
        params = new java.util.LinkedList<Formal>();
        xinuMethods.add(new MethodDecl(new IntegerType(), false, "readint", params, emptyLocals, emptyStmts, null));
        // println()
        xinuMethods.add(new MethodDecl(null, false, "println", params, emptyLocals, emptyStmts, null));
        // yield() 
        xinuMethods.add(new MethodDecl(null, false, "yield", params, emptyLocals, emptyStmts, null));
        // threadCreate
        params = new java.util.LinkedList<Formal>();
        params.add(new Formal(new IdentifierType("Thread"), "arg"));
        xinuMethods.add(new MethodDecl(null, false, "threadCreate", params, emptyLocals, emptyStmts, null));
        //feed into visitor pattern
        for (MethodDecl md : xinuMethods) {
            this.visit(md); 
        }        
        currClass = null; 
    }

    //unused visits for table building

    public void visit(java.util.AbstractList list) {}
    public void visit(IdentifierType ast) {}
    public void visit(XinuCallStmt ast) {}
    public void visit(IntegerLiteral ast) {}
    public void visit(StringLiteral ast) {}
    public void visit(ArrayType ast) {}
    public void visit(IntegerType ast) {}
    public void visit(BooleanType ast) {}
    public void visit(Identifier ast) {}
    public void visit(IdentifierExp ast) {}
    public void visit(True ast) {}
    public void visit(False ast) {}
    public void visit(This ast) {}
    public void visit(Plus ast) {}
    public void visit(Minus ast) {}
    public void visit(Times ast) {}
    public void visit(Divide ast) {}
    public void visit(LessThan ast) {}
    public void visit(GreaterThan ast) {}
    public void visit(AndExpression ast) {}
    public void visit(OrExpression ast) {}
    public void visit(Not ast) {}
    public void visit(Assign ast) {}
    public void visit(ArrayAssign ast) {}
    public void visit(If ast) {}
    public void visit(While ast) {}
    public void visit(Block ast) {}
    public void visit(ArrayLookup ast) {}
    public void visit(ArrayLength ast) {}
    public void visit(Call ast) {}
    public void visit(NewArray ast) {}
    public void visit(NewObject ast) {}
    public void visit(NullExpr ast) {}
    public void visit(EqualExpr ast) {}
    public void visit(NotEqExpr ast) {}
    public void visit(NegExpr ast) {}
    public void visit(XinuCallExpr ast) {}
}