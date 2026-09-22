/* Copyright (C) 2007, Marquette University.  All rights reserved. */
package Absyn;
import java.io.PrintWriter;

/**
 * Visitor prints AST in reparseable form.
 */
public class PrintVisitor implements Visitor
{
    PrintWriter out;
    public int indentCount = 0;

    public PrintVisitor(PrintWriter out)
    {
        this.out = out;
    }

    public PrintVisitor()
    {
        this.out = new PrintWriter(System.out);
    }

    private void indent()
    {
        out.print('\n');
        for(int i = 0; i < indentCount; i++)
        { out.print(' '); }
    }

    /** Visitor pattern dispatch. */
    //public void visit(Absyn ast) {}

    public void visit(Program ast)
    {
        out.print("Program(");
        indentCount++;
        visit(ast.classes);
        indentCount--;
        out.println(")");
        out.flush();
    }

    public void visit(java.util.AbstractList list)
    {
        if (null == list)
        {
            indent();
            out.print("null");
            return;
        }
        indent();
        out.print("AbstractList(");
        indentCount++;
        for (Object o : list)
        {
            if (null == o)
            {   indent();   out.print("null");   }
            else
            {   ((Visitable)o).accept(this);    }   
        }
        out.print(")");
        indentCount--;
    }

    public void visit(ClassDecl ast)
    {
        indent();
        out.print("ClassDecl(");
        indentCount++;
        out.print(ast.name + " " + ast.parent); 
        visit(ast.fields);
        visit(ast.methods);
        indentCount--;
        out.print(")");
    }

    public void visit(MethodDecl ast)
    {
        indent();
        out.print("MethodDecl(");
        indentCount++;
        if (null != ast.returnType)
        { ast.returnType.accept(this); }
        else
        { out.print("public_static_void"); }
        if (ast.synced) { out.print(" synchronized"); }
        out.print(" " + ast.name);
        visit(ast.params);
        visit(ast.locals);
        visit(ast.stmts);
        ast.returnVal.accept(this);
        indentCount--;
        out.print(")");
    }
    
    public void visit(Formal ast)
    {
        indent();
        out.print("Formal(");
        ast.type.accept(this);
        out.print(" " + ast.name + ")");
    }

    public void visit(IdentifierType ast)
    {
        out.print("IdentifierType(" + ast.id + ")");
    }

    public void visit(VarDecl ast)
    {
        indent();
        out.print("VarDecl(");
        ast.type.accept(this);
        out.print(" " + ast.name);
        if (null == ast.init)
        {   out.print(" null");   } 
        else
        { ast.init.accept(this); }
        out.print(")");
    }

    public void visit(XinuCallStmt ast)
    {
        indent();
        out.print("XinuCallStmt(");
        indentCount++;
        out.print(ast.method);
        visit(ast.args);
        indentCount--;
        out.print(")");
    }

    public void visit(IntegerLiteral ast)
    {
        indent();
        out.print("IntegerLiteral(" + ast.value + ")");
    }

    public void visit(StringLiteral ast)
    {
        indent();
        out.print("StringLiteral(" + ast.value + ")");
    }

    public void visit(ArrayType ast)
    {
        out.print("ArrayType(");
        ast.base.accept(this);
        out.print(")");
    }
    
    public void visit(Identifier n){
        System.out.print(n.s);
    }

    public void visit(IdentifierExp n){
        System.out.print(n.st);
    }

    public void visit(True n){
        System.out.print("true");
    }

    public void visit(False n){
        System.out.print("false");
    }

    public void visit(This n){
        System.out.print("this");
    }

    public void visit(Plus n){
        System.out.print("(");
        n.e1.accept(this);
        System.out.print(" + ");
        n.e2.accept(this);
        System.out.print(")");
    }

    public void visit(Minus n){
        System.out.print("(");
        n.e1.accept(this);
        System.out.print(" - ");
        n.e2.accept(this);
        System.out.print(")");
    }

    public void visit(Times n){
        System.out.print("(");
        n.e1.accept(this);
        System.out.print(" * ");
        n.e2.accept(this);
        System.out.print(")");
    }

    public void visit(Divide n){
        System.out.print("(");
        n.e1.accept(this);
        System.out.print(" / ");
        n.e2.accept(this);
        System.out.print(")");
    }

    public void visit(LessThan n){
        System.out.print("(");
        n.e1.accept(this);
        System.out.print(" < ");
        n.e2.accept(this);
        System.out.print(")");
    }

    public void visit(GreaterThan n){
        System.out.print("(");
        n.e1.accept(this);
        System.out.print(" > ");
        n.e2.accept(this);
        System.out.print(")");
    }

    public void visit(AndExpression n){
        System.out.print("(");
        n.e1.accept(this);
        System.out.print(" && ");
        n.e2.accept(this);
        System.out.print(")");
    }

    public void visit(OrExpression n){
        System.out.print("(");
        n.e1.accept(this);
        System.out.print(" || ");
        n.e2.accept(this);
        System.out.print(")");
    }

    public void visit(Not n){
        System.out.print("(!");
        n.e.accept(this);
        System.out.print(")");
    }

    public void visit(Assign n){
        n.id.accept(this);
        System.out.print(" = ");
        n.ex.accept(this);
        System.out.print(";");
    }

    public void visit(ArrayAssign n){
        n.i.accept(this);
        System.out.print("[");
        n.e1.accept(this);
        System.out.print("] = ");
        n.e2.accept(this);
        System.out.print(";");
    }

    public void visit(If n){
        System.out.print("if (");
        n.ex.accept(this);
        System.out.print(") ");
        n.st1.accept(this);
        if(n.st2 != null){
            System.out.print(" else ");
            n.st2.accept(this);
        }
    }

    public void visit(While n){
        System.out.print("while (");
        n.ex.accept(this);
        System.out.print(") ");
        n.st.accept(this);
    }

    public void visit(Block n){
        System.out.println("{");
        for(Stmt s : n.sl){
            if(s != null){
                s.accept(this);
            }
        }
        System.out.println("}");
    }

    public void visit(ArrayLookup n){
        n.ex1.accept(this);
        System.out.print("[");
        n.ex2.accept(this);
        System.out.print("]");
    }

    public void visit(ArrayLength n){
        n.ex.accept(this);
        System.out.print(".length");
    }

    public void visit(Call n){
        n.ex.accept(this);
        System.out.print(".");
        n.id.accept(this);
        System.out.print("(");
        if (n.li != null) {
            for (int i = 0; i < n.li.size(); i++) {
                ((Expr)n.li.get(i)).accept(this);
                if (i < n.li.size() - 1) {
                    System.out.print(", ");
                }
            }
        }
        System.out.print(")");
    }

    public void visit(NewArray n){
        System.out.print("new int[");
        n.ex.accept(this);
        System.out.print("]");
    }

    public void visit(NewObject n){
        System.out.print("new ");
        n.id.accept(this);
        System.out.print("()");
    }

    public void visit(IntegerType n){
        System.out.print("int");
    }

    public void visit(BooleanType n){
        System.out.print("boolean");
    }
}