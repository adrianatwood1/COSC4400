package Semant;

import Absyn.*;
import Types.ARRAY;
import Types.BOOLEAN;
import Types.CLASS;
import Types.ClassSymbolTable;
import Types.GlobalSymbolTable;
import Types.INT;
import Types.MethodSymbolTable;
import Types.Type;
import Types.VOID;


public class BuildSymbolVisitor implements Visitor { 
    private GlobalSymbolTable global;
    private ClassSymbolTable currClass;
    private MethodSymbolTable currMethod;

    public BuildSymbolVisitor(GlobalSymbolTable global) {
        this.global = global;
    }

    
    private Type extractType(Object astType) { 
        if (astType == null) return new VOID(); 
        if (astType instanceof IntegerType) return new INT();
        if (astType instanceof BooleanType) return new BOOLEAN();
        if (astType instanceof IdentifierType) return new CLASS(((IdentifierType) astType).id);
        if (astType instanceof ArrayType) return new ARRAY(extractType(((ArrayType) astType).base));
        return new VOID();
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
        currClass = new ClassSymbolTable(ast.name, ast.parent);
        global.addClass(ast.name, currClass);

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
        if (currMethod != null) {
            boolean added = currMethod.addLocal(ast.name, t);
            if (!added) System.err.println("Type Error: Local variable " + ast.name + " already defined.");
        } else if (currClass != null) {
            boolean added = currClass.addField(ast.name, t);
            if (!added) System.err.println("Type Error: Field " + ast.name + " already defined.");
        }
    }

    public void visit(Formal ast) {
        Type t = extractType(ast.type);
        boolean added = currMethod.addParam(ast.name, t);
        if (!added) System.err.println("Type Error: Parameter " + ast.name + " already defined.");
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

