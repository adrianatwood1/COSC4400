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

   //helper to check block's scope first, then pass symbols
   private Type resolveVars(String name){
    Type ty = (Type) env.get(Symbol.symbol(name));
    if(ty != null){
        return ty;
    }
    return currMethod.lookupVariable(name, global);
   }
   // helping to translate the input into types 
    private String formatType(Type t) {
        if (t == null) return "void";
        if (t instanceof Types.INT) return "int";
        if (t instanceof Types.BOOLEAN) return "boolean";
        if (t instanceof Types.CLASS) return "OBJECT:" + ((Types.CLASS)t).name;
        if (t instanceof Types.ARRAY) return "int[]"; 
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
            if (currType != null && currMethod.returnType != null) {
                boolean isValid = currType.coerceTo(currMethod.returnType) || isSubclass(currType, currMethod.returnType);
                if (!isValid && !ast.name.equals("main")) {
                    System.out.println("Type Error: Return type mismatch in method " + ast.name);
                    Main.hasError = true;
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
        env.put(Symbol.symbol(ast.name), declType);
    }

    public void visit(Formal ast){
        ast.type.accept(this);
        Type declType = currType;
        ast.checktype = declType;
        env.put(Symbol.symbol(ast.name),declType);
    }

     public void visit(Call ast) {
        ast.ex.accept(this);
        Type objType = currType;

        if (ast.li != null) {
            for (int i = 0; i < ast.li.size(); i++) {
                if (ast.li.get(i) != null) {
                    ((Visitable) ast.li.get(i)).accept(this);
                }
            }
        }
        if (objType instanceof CLASS) {
            String className = ((CLASS) objType).name;
            ClassSymbolTable targetClass = global.getClass(className);

            if (targetClass != null) {
                MethodSymbolTable targetMethod = targetClass.getMethod(ast.id.s);
                if (targetMethod != null) {
                    currType = targetMethod.returnType;
                    return; 
                } else {
                    System.out.println("Type Error: Method " + ast.id.s + " not found in class " + className);
                    Main.hasError = true;
                }
            }
        } else {
            System.out.println("Type Error: Cannot call method on a non-object type.");
            Main.hasError = true;
        }
        currType = new VOID();
    }

    //statement checking

    public void visit(Assign ast) {
        Type lhsType = resolveVars(ast.id.s);
        if (lhsType == null) {
            System.out.println("Type Error: Undeclared variable " + ast.id.s);
            ast.accept(new PrintVisitor());
            Main.hasError = true;
            lhsType = new VOID();
            return;
        }
        ast.ex.accept(this);
        
        boolean isValid = currType != null && (currType.coerceTo(lhsType) || isSubclass(currType, lhsType));
        
        if (!isValid) {
            String currTypeStr = currType instanceof Types.CLASS ? ((Types.CLASS)currType).name : (currType != null ? currType.toString() : "null");
            System.out.println("Type Error: Cannot assign " + currTypeStr + " to variable " + ast.id.s);
             ast.accept(new PrintVisitor());
            Main.hasError = true;
        }
    }

    public void visit(If ast) {
        ast.ex.accept(this);
        if (!(currType instanceof BOOLEAN)) {
            System.out.println("Type Error: If condition must evaluate to boolean.");
            ast.accept(new PrintVisitor());
            Main.hasError = true;
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
            ast.accept(new PrintVisitor());
            Main.hasError = true;
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
        Type varType = resolveVars(ast.st);
        if (varType == null) {
            System.out.println("Type Error: Undeclared identifier " + ast.st);
            ast.accept(new PrintVisitor());
            Main.hasError = true;
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
            ast.accept(new PrintVisitor());
            Main.hasError = true;
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
            ast.accept(new PrintVisitor());
            Main.hasError = true;
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
    public void visit(XinuCallStmt ast) {}
    public void visit(StringLiteral ast) { currType = new STRING(); }
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
    public void visit(NewArray ast) { currType = new ARRAY(new INT()); }
    public void visit(NewObject ast) { currType = new CLASS(ast.id.s); }
    public void visit(Identifier ast) {}
    
    public void visit(IdentifierType ast) {
        if (ast.id.equals("String")) {
            currType = new STRING();
        } else {
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