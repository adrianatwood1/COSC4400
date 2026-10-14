<<<<<<< HEAD
import Types.GlobalSymbolTable;
import Types.ClassSymbolTable;
import Types.MethodSymbolTable;
import Types.Type;
import Absyn.*;
import java.io.PrintWriter;

// visitor implementation for pretty printing the minijava ast
public class PrintVisitor implements Visitor {
    // output writer stream
    private PrintWriter out;
    // tracks current indentation depth
    private int indentLevel = 0;

    // constructor initializing the printwriter output stream
=======
import Absyn.*;
import java.io.PrintWriter;

public class PrintVisitor implements Visitor {
    private PrintWriter out;
    private int indentLevel = 0;

>>>>>>> 449c93a1fcff3a8c916a5d469cfe118d062181e8
    public PrintVisitor(PrintWriter out) {
        this.out = out;
    }

<<<<<<< HEAD
    // helper method to print indentation spaces based on current depth
=======
>>>>>>> 449c93a1fcff3a8c916a5d469cfe118d062181e8
    private void indent() {
        for (int i = 0; i < indentLevel; i++) {
            out.print("  ");
        }
    }

<<<<<<< HEAD
    // Declarations
    // visits root program node and iterates through all class declarations
=======
    //declarations

>>>>>>> 449c93a1fcff3a8c916a5d469cfe118d062181e8
    public void visit(Program ast) {
        if (ast == null) return;
        for (int i = 0; i < ast.classes.size(); i++) {
            if (ast.classes.get(i) != null) {
                ((Visitable) ast.classes.get(i)).accept(this);
                out.println();
            }
        }
    }

<<<<<<< HEAD
    // visits class declaration printing class name inheritance fields and methods
=======
>>>>>>> 449c93a1fcff3a8c916a5d469cfe118d062181e8
    public void visit(ClassDecl ast) {
        if (ast == null) return;
        indent();
        out.print("class " + ast.name);
<<<<<<< HEAD
        // prints extended parent class if inheritance exists
=======
        
>>>>>>> 449c93a1fcff3a8c916a5d469cfe118d062181e8
        if (ast.parent != null) {
            out.print(" extends " + ast.parent);
        }
        out.println(" {");
        indentLevel++;

<<<<<<< HEAD
        // visits all field declarations in the class
=======
>>>>>>> 449c93a1fcff3a8c916a5d469cfe118d062181e8
        for (int i = 0; i < ast.fields.size(); i++) {
            if (ast.fields.get(i) != null) {
                ((Visitable) ast.fields.get(i)).accept(this);
            }
        }

<<<<<<< HEAD
        // visits all method declarations in the class
=======
>>>>>>> 449c93a1fcff3a8c916a5d469cfe118d062181e8
        for (int i = 0; i < ast.methods.size(); i++) {
            if (ast.methods.get(i) != null) {
                ((Visitable) ast.methods.get(i)).accept(this);
            }
        }

        indentLevel--;
        indent();
        out.println("}");
    }

<<<<<<< HEAD
    // visits method declaration printing return type parameters locals and body
=======
>>>>>>> 449c93a1fcff3a8c916a5d469cfe118d062181e8
    public void visit(MethodDecl ast) {
        if (ast == null) return;
        indent();
        out.print("public ");
        if (ast.returnType != null) {
            ((Visitable) ast.returnType).accept(this);
            out.print(" ");
        } else {
            out.print("void ");
        }
        out.print(ast.name + "(");

<<<<<<< HEAD
        // visits parameter list
=======
>>>>>>> 449c93a1fcff3a8c916a5d469cfe118d062181e8
        for (int i = 0; i < ast.params.size(); i++) {
            if (i > 0) out.print(", ");
            if (ast.params.get(i) != null) {
                ((Visitable) ast.params.get(i)).accept(this);
            }
        }
        out.println(") {");
        indentLevel++;

<<<<<<< HEAD
        // visits local variable declarations
=======
>>>>>>> 449c93a1fcff3a8c916a5d469cfe118d062181e8
        for (int i = 0; i < ast.locals.size(); i++) {
            if (ast.locals.get(i) != null) {
                ((Visitable) ast.locals.get(i)).accept(this);
            }
        }

<<<<<<< HEAD
        // visits method statements
=======
>>>>>>> 449c93a1fcff3a8c916a5d469cfe118d062181e8
        for (int i = 0; i < ast.stmts.size(); i++) {
            if (ast.stmts.get(i) != null) {
                ((Visitable) ast.stmts.get(i)).accept(this);
            }
        }

<<<<<<< HEAD
        // visits return expression if present
=======
>>>>>>> 449c93a1fcff3a8c916a5d469cfe118d062181e8
        if (ast.returnVal != null) {
            indent();
            out.print("return ");
            ast.returnVal.accept(this);
            out.println(";");
        }

        indentLevel--;
        indent();
        out.println("}");
    }

<<<<<<< HEAD
    // visits variable declaration printing data type and identifier
=======
>>>>>>> 449c93a1fcff3a8c916a5d469cfe118d062181e8
    public void visit(VarDecl ast) {
        if (ast == null) return;
        indent();
        if (ast.type != null) {
            ((Visitable) ast.type).accept(this);
            out.print(" ");
        }
        out.println(ast.name + ";");
    }

<<<<<<< HEAD
    // visits formal parameter printing data type and parameter name
=======
>>>>>>> 449c93a1fcff3a8c916a5d469cfe118d062181e8
    public void visit(Formal ast) {
        if (ast == null) return;
        if (ast.type != null) {
            ((Visitable) ast.type).accept(this);
            out.print(" ");
        }
        out.print(ast.name);
    }

<<<<<<< HEAD
    // Types
    // visits custom user defined class or object type
=======
    //types

>>>>>>> 449c93a1fcff3a8c916a5d469cfe118d062181e8
    public void visit(IdentifierType ast) {
        if (ast != null) out.print(ast.id);
    }

<<<<<<< HEAD
    // visits primitive integer type
=======
>>>>>>> 449c93a1fcff3a8c916a5d469cfe118d062181e8
    public void visit(IntegerType ast) {
        out.print("int");
    }

<<<<<<< HEAD
    // visits primitive boolean type
=======
>>>>>>> 449c93a1fcff3a8c916a5d469cfe118d062181e8
    public void visit(BooleanType ast) {
        out.print("boolean");
    }

<<<<<<< HEAD
    // visits array type printing base type followed by brackets
=======
>>>>>>> 449c93a1fcff3a8c916a5d469cfe118d062181e8
    public void visit(ArrayType ast) {
        if (ast == null) return;
        if (ast.base != null) {
            ((Visitable) ast.base).accept(this);
        }
        out.print("[]");
    }

<<<<<<< HEAD
    // Statements
    // visits code block wrapped in curly braces
=======
    //statements

>>>>>>> 449c93a1fcff3a8c916a5d469cfe118d062181e8
    public void visit(Block ast) {
        if (ast == null) return;
        indent();
        out.println("{");
        indentLevel++;
        for (int i = 0; i < ast.sl.size(); i++) {
            if (ast.sl.get(i) != null) {
                ((Visitable) ast.sl.get(i)).accept(this);
            }
        }
        indentLevel--;
        indent();
        out.println("}");
    }

<<<<<<< HEAD
    // visits if condition and handles optional else branch
=======
>>>>>>> 449c93a1fcff3a8c916a5d469cfe118d062181e8
    public void visit(If ast) {
        if (ast == null) return;
        indent();
        out.print("if (");
        if (ast.ex != null) ast.ex.accept(this);
        out.println(")");
        
        if (ast.st1 != null) ast.st1.accept(this);

        if (ast.st2 != null) {
            indent();
            out.println("else");
            ast.st2.accept(this);
        }
    }

<<<<<<< HEAD
    // visits while loop printing condition and loop body
=======
>>>>>>> 449c93a1fcff3a8c916a5d469cfe118d062181e8
    public void visit(While ast) {
        if (ast == null) return;
        indent();
        out.print("while (");
        if (ast.ex != null) ast.ex.accept(this);
        out.println(")");
        if (ast.st != null) ast.st.accept(this);
    }

<<<<<<< HEAD
    // visits standard variable assignment statement
=======
>>>>>>> 449c93a1fcff3a8c916a5d469cfe118d062181e8
    public void visit(Assign ast) {
        if (ast == null) return;
        indent();
        out.print(ast.id.s + " = ");
        if (ast.ex != null) ast.ex.accept(this);
        out.println(";");
    }

<<<<<<< HEAD
    // visits array element assignment statement with index
    public void visit(ArrayAssign ast) {
        if (ast == null) return;
        indent();
        out.print(ast.name + "[");
        if (ast.index != null) ast.index.accept(this);
        out.print("] = ");
        if (ast.value != null) ast.value.accept(this);
        out.println(";");
    }

    // visits xinu operating system call statement
    public void visit(XinuCallStmt ast) {
        if (ast == null) return;
        indent();
        out.print("xinuCall(");
=======
    public void visit(ArrayAssign ast) {
        if (ast == null) return;
        indent();
        out.print(ast.i.s + "[");
        if (ast.e1 != null) ast.e1.accept(this);
        out.print("] = ");
        if (ast.e2 != null) ast.e2.accept(this);
        out.println(";");
    }

    public void visit(XinuCallStmt ast) {
        if (ast == null) return;
        indent();
        out.print("Xinu." + ast.method + "(");
>>>>>>> 449c93a1fcff3a8c916a5d469cfe118d062181e8
        if (ast.args != null) {
            for (int i = 0; i < ast.args.size(); i++) {
                if (i > 0) out.print(", ");
                if (ast.args.get(i) != null) ((Visitable) ast.args.get(i)).accept(this);
            }
        }
        out.println(");");
    }

<<<<<<< HEAD
    // Expressions
    // visits binary addition expression
=======
    //expressions

>>>>>>> 449c93a1fcff3a8c916a5d469cfe118d062181e8
    public void visit(Plus ast) {
        out.print("(");
        if (ast.e1 != null) ast.e1.accept(this);
        out.print(" + ");
        if (ast.e2 != null) ast.e2.accept(this);
        out.print(")");
    }

<<<<<<< HEAD
    // visits binary subtraction expression
=======
>>>>>>> 449c93a1fcff3a8c916a5d469cfe118d062181e8
    public void visit(Minus ast) {
        out.print("(");
        if (ast.e1 != null) ast.e1.accept(this);
        out.print(" - ");
        if (ast.e2 != null) ast.e2.accept(this);
        out.print(")");
    }

<<<<<<< HEAD
    // visits binary multiplication expression
=======
>>>>>>> 449c93a1fcff3a8c916a5d469cfe118d062181e8
    public void visit(Times ast) {
        out.print("(");
        if (ast.e1 != null) ast.e1.accept(this);
        out.print(" * ");
        if (ast.e2 != null) ast.e2.accept(this);
        out.print(")");
    }

<<<<<<< HEAD
    // visits binary division expression
=======
>>>>>>> 449c93a1fcff3a8c916a5d469cfe118d062181e8
    public void visit(Divide ast) {
        out.print("(");
        if (ast.e1 != null) ast.e1.accept(this);
        out.print(" / ");
        if (ast.e2 != null) ast.e2.accept(this);
        out.print(")");
    }

<<<<<<< HEAD
    // visits less than comparison expression
=======
>>>>>>> 449c93a1fcff3a8c916a5d469cfe118d062181e8
    public void visit(LessThan ast) {
        out.print("(");
        if (ast.e1 != null) ast.e1.accept(this);
        out.print(" < ");
        if (ast.e2 != null) ast.e2.accept(this);
        out.print(")");
    }

<<<<<<< HEAD
    // visits greater than comparison expression
=======
>>>>>>> 449c93a1fcff3a8c916a5d469cfe118d062181e8
    public void visit(GreaterThan ast) {
        out.print("(");
        if (ast.e1 != null) ast.e1.accept(this);
        out.print(" > ");
        if (ast.e2 != null) ast.e2.accept(this);
        out.print(")");
    }

<<<<<<< HEAD
    // visits logical and expression
=======
>>>>>>> 449c93a1fcff3a8c916a5d469cfe118d062181e8
    public void visit(AndExpression ast) {
        out.print("(");
        if (ast.e1 != null) ast.e1.accept(this);
        out.print(" && ");
        if (ast.e2 != null) ast.e2.accept(this);
        out.print(")");
    }

<<<<<<< HEAD
    // visits logical or expression
=======
>>>>>>> 449c93a1fcff3a8c916a5d469cfe118d062181e8
    public void visit(OrExpression ast) {
        out.print("(");
        if (ast.e1 != null) ast.e1.accept(this);
        out.print(" || ");
        if (ast.e2 != null) ast.e2.accept(this);
        out.print(")");
    }

<<<<<<< HEAD
    // visits logical not expression
=======
>>>>>>> 449c93a1fcff3a8c916a5d469cfe118d062181e8
    public void visit(Not ast) {
        out.print("!");
        if (ast.e != null) ast.e.accept(this);
    }

<<<<<<< HEAD
    // visits array element lookup expression
    public void visit(ArrayLookup ast) {
        if (ast.array != null) ast.array.accept(this);
        out.print("[");
        if (ast.index != null) ast.index.accept(this);
        out.print("]");
    }

    // visits array length property access expression
    public void visit(ArrayLength ast) {
        if (ast.array != null) ast.array.accept(this);
        out.print(".length");
    }

    // visits method call expression on object instance
    public void visit(Call ast) {
        if (ast.object != null) {
            ast.object.accept(this);
            out.print(".");
        }
        out.print(ast.method + "(");
        if (ast.args != null) {
            for (int i = 0; i < ast.args.size(); i++) {
                if (i > 0) out.print(", ");
                if (ast.args.get(i) != null) ((Visitable) ast.args.get(i)).accept(this);
=======
    public void visit(ArrayLookup ast) {
        if (ast.ex1 != null) ast.ex1.accept(this);
        out.print("[");
        if (ast.ex2 != null) ast.ex2.accept(this);
        out.print("]");
    }

    public void visit(ArrayLength ast) {
        if (ast.ex != null) ast.ex.accept(this);
        out.print(".length");
    }

    public void visit(Call ast) {
        if (ast.ex != null) {
            ast.ex.accept(this);
            out.print(".");
        }
        out.print(ast.id.s + "(");
        if (ast.li != null) {
            for (int i = 0; i < ast.li.size(); i++) {
                if (i > 0) out.print(", ");
                if (ast.li.get(i) != null) ((Visitable) ast.li.get(i)).accept(this);
>>>>>>> 449c93a1fcff3a8c916a5d469cfe118d062181e8
            }
        }
        out.print(")");
    }

<<<<<<< HEAD
    // visits literal integer value
    public void visit(IntegerLiteral ast) {
        out.print(ast.val);
    }

    // visits boolean true literal
=======
    public void visit(IntegerLiteral ast) {
        out.print(ast.value);
    }

>>>>>>> 449c93a1fcff3a8c916a5d469cfe118d062181e8
    public void visit(True ast) {
        out.print("true");
    }

<<<<<<< HEAD
    // visits boolean false literal
=======
>>>>>>> 449c93a1fcff3a8c916a5d469cfe118d062181e8
    public void visit(False ast) {
        out.print("false");
    }

<<<<<<< HEAD
    // visits variable identifier expression
=======
>>>>>>> 449c93a1fcff3a8c916a5d469cfe118d062181e8
    public void visit(IdentifierExp ast) {
        out.print(ast.st);
    }

<<<<<<< HEAD
    // visits current object reference expression
=======
>>>>>>> 449c93a1fcff3a8c916a5d469cfe118d062181e8
    public void visit(This ast) {
        out.print("this");
    }

<<<<<<< HEAD
    // visits array allocation expression
    public void visit(NewArray ast) {
        out.print("new ");
        if (ast.type != null) ((Visitable) ast.type).accept(this);
        out.print("[");
        if (ast.size != null) ast.size.accept(this);
        out.print("]");
    }

    // visits object instantiation expression
    public void visit(NewObject ast) {
        out.print("new " + ast.type + "()");
    }

    // visits null object reference literal
=======
    public void visit(NewArray ast) {
        out.print("new int[");
        if (ast.ex != null) ast.ex.accept(this);
        out.print("]");
    }

    public void visit(NewObject ast) {
        out.print("new " + ast.id.s + "()");
    }

>>>>>>> 449c93a1fcff3a8c916a5d469cfe118d062181e8
    public void visit(NullExpr ast) {
        out.print("null");
    }

<<<<<<< HEAD
    // visits equality comparison expression
    public void visit(EqualExpr ast) {
        out.print("(");
        if (ast.e1 != null) ast.e1.accept(this);
        out.print(" == ");
        if (ast.e2 != null) ast.e2.accept(this);
        out.print(")");
    }

    // visits inequality comparison expression
    public void visit(NotEqExpr ast) {
        out.print("(");
        if (ast.e1 != null) ast.e1.accept(this);
        out.print(" != ");
        if (ast.e2 != null) ast.e2.accept(this);
        out.print(")");
    }

    // visits unary arithmetic negation expression
    public void visit(NegExpr ast) {
        out.print("-");
        if (ast.e != null) ast.e.accept(this);
    }

    // visits literal string value
    public void visit(StringLiteral ast) {
        out.print("\"" + ast.val + "\"");
    }

    // visits xinu system call expression returning value
    public void visit(XinuCallExpr ast) {
        out.print("xinuCall(");
=======
    public void visit(EqualExpr ast) {
        out.print("(");
        if (ast.ex1 != null) ast.ex1.accept(this);
        out.print(" == ");
        if (ast.ex2 != null) ast.ex2.accept(this);
        out.print(")");
    }

    public void visit(NotEqExpr ast) {
        out.print("(");
        if (ast.ex1 != null) ast.ex1.accept(this);
        out.print(" != ");
        if (ast.ex2 != null) ast.ex2.accept(this);
        out.print(")");
    }

    public void visit(NegExpr ast) {
        out.print("-");
        if (ast.ex1 != null) ast.ex1.accept(this);
    }

    public void visit(StringLiteral ast) {
        out.print("\"" + ast.value + "\"");
    }

    public void visit(XinuCallExpr ast) {
        out.print("Xinu." + ast.method + "(");
>>>>>>> 449c93a1fcff3a8c916a5d469cfe118d062181e8
        if (ast.args != null) {
            for (int i = 0; i < ast.args.size(); i++) {
                if (i > 0) out.print(", ");
                if (ast.args.get(i) != null) ((Visitable) ast.args.get(i)).accept(this);
            }
        }
        out.print(")");
    }

<<<<<<< HEAD
    // visits standalone identifier node
=======
>>>>>>> 449c93a1fcff3a8c916a5d469cfe118d062181e8
    public void visit(Identifier ast) {
        out.print(ast.s);
    }

<<<<<<< HEAD
    // visits generic list wrapper node
=======
>>>>>>> 449c93a1fcff3a8c916a5d469cfe118d062181e8
    public void visit(java.util.AbstractList list) {}
}