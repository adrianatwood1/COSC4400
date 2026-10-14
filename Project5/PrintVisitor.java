import Absyn.*;
import java.io.PrintWriter;

public class PrintVisitor implements Visitor {
    private PrintWriter out;
    private int indentLevel = 0;

    public PrintVisitor(PrintWriter out) {
        this.out = out;
    }

    private void indent() {
        for (int i = 0; i < indentLevel; i++) {
            out.print("  ");
        }
    }

    //declarations

    public void visit(Program ast) {
        if (ast == null) return;
        for (int i = 0; i < ast.classes.size(); i++) {
            if (ast.classes.get(i) != null) {
                ((Visitable) ast.classes.get(i)).accept(this);
                out.println();
            }
        }
    }

    public void visit(ClassDecl ast) {
        if (ast == null) return;
        indent();
        out.print("class " + ast.name);
        
        if (ast.parent != null) {
            out.print(" extends " + ast.parent);
        }
        out.println(" {");
        indentLevel++;

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

        indentLevel--;
        indent();
        out.println("}");
    }

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

        for (int i = 0; i < ast.params.size(); i++) {
            if (i > 0) out.print(", ");
            if (ast.params.get(i) != null) {
                ((Visitable) ast.params.get(i)).accept(this);
            }
        }
        out.println(") {");
        indentLevel++;

        for (int i = 0; i < ast.locals.size(); i++) {
            if (ast.locals.get(i) != null) {
                ((Visitable) ast.locals.get(i)).accept(this);
            }
        }

        for (int i = 0; i < ast.stmts.size(); i++) {
            if (ast.stmts.get(i) != null) {
                ((Visitable) ast.stmts.get(i)).accept(this);
            }
        }

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

    public void visit(VarDecl ast) {
        if (ast == null) return;
        indent();
        if (ast.type != null) {
            ((Visitable) ast.type).accept(this);
            out.print(" ");
        }
        out.println(ast.name + ";");
    }

    public void visit(Formal ast) {
        if (ast == null) return;
        if (ast.type != null) {
            ((Visitable) ast.type).accept(this);
            out.print(" ");
        }
        out.print(ast.name);
    }

    //types

    public void visit(IdentifierType ast) {
        if (ast != null) out.print(ast.id);
    }

    public void visit(IntegerType ast) {
        out.print("int");
    }

    public void visit(BooleanType ast) {
        out.print("boolean");
    }

    public void visit(ArrayType ast) {
        if (ast == null) return;
        if (ast.base != null) {
            ((Visitable) ast.base).accept(this);
        }
        out.print("[]");
    }

    //statements

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

    public void visit(While ast) {
        if (ast == null) return;
        indent();
        out.print("while (");
        if (ast.ex != null) ast.ex.accept(this);
        out.println(")");
        if (ast.st != null) ast.st.accept(this);
    }

    public void visit(Assign ast) {
        if (ast == null) return;
        indent();
        out.print(ast.id.s + " = ");
        if (ast.ex != null) ast.ex.accept(this);
        out.println(";");
    }

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
        if (ast.args != null) {
            for (int i = 0; i < ast.args.size(); i++) {
                if (i > 0) out.print(", ");
                if (ast.args.get(i) != null) ((Visitable) ast.args.get(i)).accept(this);
            }
        }
        out.println(");");
    }

    //expressions

    public void visit(Plus ast) {
        out.print("(");
        if (ast.e1 != null) ast.e1.accept(this);
        out.print(" + ");
        if (ast.e2 != null) ast.e2.accept(this);
        out.print(")");
    }

    public void visit(Minus ast) {
        out.print("(");
        if (ast.e1 != null) ast.e1.accept(this);
        out.print(" - ");
        if (ast.e2 != null) ast.e2.accept(this);
        out.print(")");
    }

    public void visit(Times ast) {
        out.print("(");
        if (ast.e1 != null) ast.e1.accept(this);
        out.print(" * ");
        if (ast.e2 != null) ast.e2.accept(this);
        out.print(")");
    }

    public void visit(Divide ast) {
        out.print("(");
        if (ast.e1 != null) ast.e1.accept(this);
        out.print(" / ");
        if (ast.e2 != null) ast.e2.accept(this);
        out.print(")");
    }

    public void visit(LessThan ast) {
        out.print("(");
        if (ast.e1 != null) ast.e1.accept(this);
        out.print(" < ");
        if (ast.e2 != null) ast.e2.accept(this);
        out.print(")");
    }

    public void visit(GreaterThan ast) {
        out.print("(");
        if (ast.e1 != null) ast.e1.accept(this);
        out.print(" > ");
        if (ast.e2 != null) ast.e2.accept(this);
        out.print(")");
    }

    public void visit(AndExpression ast) {
        out.print("(");
        if (ast.e1 != null) ast.e1.accept(this);
        out.print(" && ");
        if (ast.e2 != null) ast.e2.accept(this);
        out.print(")");
    }

    public void visit(OrExpression ast) {
        out.print("(");
        if (ast.e1 != null) ast.e1.accept(this);
        out.print(" || ");
        if (ast.e2 != null) ast.e2.accept(this);
        out.print(")");
    }

    public void visit(Not ast) {
        out.print("!");
        if (ast.e != null) ast.e.accept(this);
    }

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
            }
        }
        out.print(")");
    }

    public void visit(IntegerLiteral ast) {
        out.print(ast.value);
    }

    public void visit(True ast) {
        out.print("true");
    }

    public void visit(False ast) {
        out.print("false");
    }

    public void visit(IdentifierExp ast) {
        out.print(ast.st);
    }

    public void visit(This ast) {
        out.print("this");
    }

    public void visit(NewArray ast) {
        out.print("new int[");
        if (ast.ex != null) ast.ex.accept(this);
        out.print("]");
    }

    public void visit(NewObject ast) {
        out.print("new " + ast.id.s + "()");
    }

    public void visit(NullExpr ast) {
        out.print("null");
    }

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
        if (ast.args != null) {
            for (int i = 0; i < ast.args.size(); i++) {
                if (i > 0) out.print(", ");
                if (ast.args.get(i) != null) ((Visitable) ast.args.get(i)).accept(this);
            }
        }
        out.print(")");
    }

    public void visit(Identifier ast) {
        out.print(ast.s);
    }

    public void visit(java.util.AbstractList list) {}
}