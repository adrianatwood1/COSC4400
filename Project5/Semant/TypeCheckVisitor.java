package Semant;

import Absyn.*;
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

public class TypeCheckVisitor implements Visitor { 
    private GlobalSymbolTable global;
    private ClassSymbolTable currClass;
    private MethodSymbolTable currMethod;
    private Type currType; 
    
    public TypeCheckVisitor(GlobalSymbolTable global) {
        this.global = global;
    }
    
    // Walk up the parent chain to see if childType extends parentType
    private boolean isSubclass(Type childType, Type parentType) {
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

    //scope managers

    public void visit(Program ast) {
        for (int i = 0; i < ast.classes.size(); i++) {
            if (ast.classes.get(i) != null) {
                ((Visitable) ast.classes.get(i)).accept(this);
            }
        }
    }

    public void visit(ClassDecl ast) {
        currClass = global.getClass(ast.name);
        for (int i = 0; i < ast.methods.size(); i++) {
            if (ast.methods.get(i) != null) {
                ((Visitable) ast.methods.get(i)).accept(this);
            }
        }
        currClass = null;
    }

    public void visit(MethodDecl ast) {
        currMethod = currClass.getMethod(ast.name);
        for (int i = 0; i < ast.stmts.size(); i++) {
            if (ast.stmts.get(i) != null) {
                ((Visitable) ast.stmts.get(i)).accept(this);
            }
        }
        
        if (ast.returnVal != null) {
            ast.returnVal.accept(this);
            if (currType != null && currMethod.returnType != null) {
                // allow upcasting on return types
                boolean isValid = isSubclass(currType, currMethod.returnType);

                if (!isValid && !ast.name.equals("main")) {
                    System.out.println("Type Error: Return type mismatch in method " + ast.name);
                }
            }
        }
        
        currMethod = null;
    }

    //statement checking

    public void visit(Assign ast) {
        Type lhsType = currMethod.lookupVariable(ast.id.s, global);
        if (lhsType == null) {
            System.out.println("Type Error: Undeclared variable " + ast.id.s);
            return;
        }
        ast.ex.accept(this);
        
        // allow upcasting assignments (Child to Parent)
        boolean isValid = isSubclass(currType, lhsType);
        
        if (!isValid) {
            // grab the raw class names without "OBJECT()" wrappers for cleaner errors
            String cTypeStr = currType instanceof CLASS ? ((CLASS)currType).name : currType.toString();
            System.out.println("Type Error: Cannot assign " + cTypeStr + " to variable " + ast.id.s);
        }
    }

    public void visit(If ast) {
        ast.ex.accept(this);
        if (!(currType instanceof BOOLEAN)) {
            System.out.println("Type Error: If condition must evaluate to boolean.");
        }
        ast.st1.accept(this);
        if (ast.st2 != null) {
            ast.st2.accept(this);
        }
    }

    public void visit(While ast) {
        ast.ex.accept(this);
        if (!(currType instanceof BOOLEAN)) {
            System.out.println("Type Error: While condition must evaluate to boolean.");
        }
        ast.st.accept(this);
    }

    public void visit(Block ast) {
        for (int i = 0; i < ast.sl.size(); i++) {
            if (ast.sl.get(i) != null) {
                ((Visitable) ast.sl.get(i)).accept(this);
            }
        }
    }

    //expression evaluation

    public void visit(IdentifierExp ast) {
        Type varType = currMethod.lookupVariable(ast.st, global);
        if (varType == null) {
            System.out.println("Type Error: Undeclared identifier " + ast.st);
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
            System.out.println("Type Error: '+' operator requires integer operands.");
        }
        currType = new INT(); 
    }

    public void visit(LessThan ast) {
        ast.e1.accept(this);
        Type t1 = currType;
        ast.e2.accept(this);
        Type t2 = currType;
        if (!(t1 instanceof INT) || !(t2 instanceof INT)) {
            System.out.println("Type Error: '<' operator requires integer operands.");
        }
        currType = new BOOLEAN(); 
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

    //unused visits
    
    public void visit(java.util.AbstractList list) {}
    public void visit(Formal ast) {}
    public void visit(IdentifierType ast) {}
    public void visit(VarDecl ast) {}
    public void visit(XinuCallStmt ast) {}
    public void visit(StringLiteral ast) { currType = new STRING(); }
    public void visit(ArrayType ast) {}
    public void visit(IntegerType ast) {}
    public void visit(BooleanType ast) {}
    public void visit(Identifier ast) {}
    public void visit(This ast) {}
    public void visit(Minus ast) { currType = new INT(); }
    public void visit(Times ast) { currType = new INT(); }
    public void visit(Divide ast) { currType = new INT(); }
    public void visit(GreaterThan ast) { currType = new BOOLEAN(); }
    public void visit(AndExpression ast) { currType = new BOOLEAN(); }
    public void visit(OrExpression ast) { currType = new BOOLEAN(); }
    public void visit(Not ast) { currType = new BOOLEAN(); }
    public void visit(ArrayAssign ast) {}
    public void visit(NullExpr ast) { currType = new NIL(); }
    public void visit(EqualExpr ast) { currType = new BOOLEAN(); }
    public void visit(NotEqExpr ast) { currType = new BOOLEAN(); }
    public void visit(NegExpr ast) { currType = new INT(); }
    public void visit(XinuCallExpr ast) {}
    public void visit(ArrayLength ast) { currType = new INT(); }
    public void visit(ArrayLookup ast) { currType = new INT(); }
    public void visit(Call ast) { currType = new INT(); }
    public void visit(NewArray ast) { currType = new ARRAY(new INT()); }
    public void visit(NewObject ast) { currType = new CLASS(ast.id.s); }
}