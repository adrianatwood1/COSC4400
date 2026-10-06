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
    public PrintVisitor(PrintWriter out) {
        this.out = out;
    }

    // helper method to print indentation spaces based on current depth
    private void indent() {
        for (int i = 0; i < indentLevel; i++) {
            out.print("  ");
        }
    }

    // Declarations
    // visits root program node and iterates through all class declarations
    public void visit(Program ast) {
        if (ast == null) return;
        for (int i = 0; i < ast.classes.size(); i++) {
            if (ast.classes.get(i) != null) {
                ((Visitable) ast.classes.get(i)).accept(this);
                out.println();
            }
        }
    }

    // visits class declaration printing class name inheritance fields and methods
    public void visit(ClassDecl ast) {
        if (ast == null) return;
        indent();
        out.print("class " + ast.name);
        // prints extended parent class if inheritance exists
        if (ast.parent != null) {
            out.print(" extends " + ast.parent);
        }
        out.println(" {");
        indentLevel++;

        // visits all field declarations in the class
        for (int i = 0; i < ast.fields.size(); i++) {
            if (ast.fields.get(i) != null) {
                ((Visitable) ast.fields.get(i)).accept(this);
            }
        }

        // visits all method declarations in the class
        for (int i = 0; i < ast.methods.size(); i++) {
            if (ast.methods.get(i) != null) {
                ((Visitable) ast.methods.get(i)).accept(this);
            }
        }

        indentLevel--;
        indent();
        out.println("}");
    }

    // visits method declaration printing return type parameters locals and body
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

        // visits parameter list
        for (int i = 0; i < ast.params.size(); i++) {
            if (i > 0) out.print(", ");
            if (ast.params.get(i) != null) {
                ((Visitable) ast.params.get(i)).accept(this);
            }
        }
        out.println(") {");
        indentLevel++;

        // visits local variable declarations
        for (int i = 0; i < ast.locals.size(); i++) {
            if (ast.locals.get(i) != null) {
                ((Visitable) ast.locals.get(i)).accept(this);
            }
        }

        // visits method statements
        for (int i = 0; i < ast.stmts.size(); i++) {
            if (ast.stmts.get(i) != null) {
                ((Visitable) ast.stmts.get(i)).accept(this);
            }
        }

        // visits return expression if present
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

    // visits variable declaration printing data type and identifier
    public void visit(VarDecl ast) {
        if (ast == null) return;
        indent();
        if (ast.type != null) {
            ((Visitable) ast.type).accept(this);
            out.print(" ");
        }
        out.println(ast.name + ";");
    }

    // visits formal parameter printing data type and parameter name
    public void visit(Formal ast) {
        if (ast == null) return;
        if (ast.type != null) {
            ((Visitable) ast.type).accept(this);
            out.print(" ");
        }
        out.print(ast.name);
    }

    // Types
    // visits custom user defined class or object type
    public void visit(IdentifierType ast) {
        if (ast != null) out.print(ast.id);
    }

    // visits primitive integer type
    public void visit(IntegerType ast) {
        out.print("int");
    }

    // visits primitive boolean type
    public void visit(BooleanType ast) {
        out.print("boolean");
    }

    // visits array type printing base type followed by brackets
    public void visit(ArrayType ast) {
        if (ast == null) return;
        if (ast.base != null) {
            ((Visitable) ast.base).accept(this);
        }
        out.print("[]");
    }

    // Statements
    // visits code block wrapped in curly braces
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

    // visits if condition and handles optional else branch
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

    // visits while loop printing condition and loop body
    public void visit(While ast) {
        if (ast == null) return;
        indent();
        out.print("while (");
        if (ast.ex != null) ast.ex.accept(this);
        out.println(")");
        if (ast.st != null) ast.st.accept(this);
    }

    // visits standard variable assignment statement
    public void visit(Assign ast) {
        if (ast == null) return;
        indent();
        out.print(ast.id.s + " = ");
        if (ast.ex != null) ast.ex.accept(this);
        out.println(";");
    }

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
        if (ast.args != null) {
            for (int i = 0; i < ast.args.size(); i++) {
                if (i > 0) out.print(", ");
                if (ast.args.get(i) != null) ((Visitable) ast.args.get(i)).accept(this);
            }
        }
        out.println(");");
    }

    // Expressions
    // visits binary addition expression
    public void visit(Plus ast) {
        out.print("(");
        if (ast.e1 != null) ast.e1.accept(this);
        out.print(" + ");
        if (ast.e2 != null) ast.e2.accept(this);
        out.print(")");
    }

    // visits binary subtraction expression
    public void visit(Minus ast) {
        out.print("(");
        if (ast.e1 != null) ast.e1.accept(this);
        out.print(" - ");
        if (ast.e2 != null) ast.e2.accept(this);
        out.print(")");
    }

    // visits binary multiplication expression
    public void visit(Times ast) {
        out.print("(");
        if (ast.e1 != null) ast.e1.accept(this);
        out.print(" * ");
        if (ast.e2 != null) ast.e2.accept(this);
        out.print(")");
    }

    // visits binary division expression
    public void visit(Divide ast) {
        out.print("(");
        if (ast.e1 != null) ast.e1.accept(this);
        out.print(" / ");
        if (ast.e2 != null) ast.e2.accept(this);
        out.print(")");
    }

    // visits less than comparison expression
    public void visit(LessThan ast) {
        out.print("(");
        if (ast.e1 != null) ast.e1.accept(this);
        out.print(" < ");
        if (ast.e2 != null) ast.e2.accept(this);
        out.print(")");
    }

    // visits greater than comparison expression
    public void visit(GreaterThan ast) {
        out.print("(");
        if (ast.e1 != null) ast.e1.accept(this);
        out.print(" > ");
        if (ast.e2 != null) ast.e2.accept(this);
        out.print(")");
    }

    // visits logical and expression
    public void visit(AndExpression ast) {
        out.print("(");
        if (ast.e1 != null) ast.e1.accept(this);
        out.print(" && ");
        if (ast.e2 != null) ast.e2.accept(this);
        out.print(")");
    }

    // visits logical or expression
    public void visit(OrExpression ast) {
        out.print("(");
        if (ast.e1 != null) ast.e1.accept(this);
        out.print(" || ");
        if (ast.e2 != null) ast.e2.accept(this);
        out.print(")");
    }

    // visits logical not expression
    public void visit(Not ast) {
        out.print("!");
        if (ast.e != null) ast.e.accept(this);
    }

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
            }
        }
        out.print(")");
    }

    // visits literal integer value
    public void visit(IntegerLiteral ast) {
        out.print(ast.val);
    }

    // visits boolean true literal
    public void visit(True ast) {
        out.print("true");
    }

    // visits boolean false literal
    public void visit(False ast) {
        out.print("false");
    }

    // visits variable identifier expression
    public void visit(IdentifierExp ast) {
        out.print(ast.st);
    }

    // visits current object reference expression
    public void visit(This ast) {
        out.print("this");
    }

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
    public void visit(NullExpr ast) {
        out.print("null");
    }

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
        if (ast.args != null) {
            for (int i = 0; i < ast.args.size(); i++) {
                if (i > 0) out.print(", ");
                if (ast.args.get(i) != null) ((Visitable) ast.args.get(i)).accept(this);
            }
        }
        out.print(")");
    }

    // visits standalone identifier node
    public void visit(Identifier ast) {
        out.print(ast.s);
    }

    // visits generic list wrapper node
    public void visit(java.util.AbstractList list) {}
}