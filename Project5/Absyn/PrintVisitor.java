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
        out.print("ClassDecl(" + ast.name + " " + ast.parent); 
        indentCount++;
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
        out.print("XinuCallStmt(" + ast.method);
        indentCount++;
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

    public void visit(IntegerType ast) { out.print("IntegerType"); }
    public void visit(BooleanType ast) { out.print("BooleanType"); }
    
    public void visit(Identifier n){
        indent();
        out.print("Identifier(" + n.s + ")");
    }

    public void visit(IdentifierExp n){
        indent();
        out.print("IdentifierExpr(" + n.st + ")");
    }

    public void visit(True n){
        indent();
        out.print("TrueExpr");
    }

    public void visit(False n){
        indent();
        out.print("FalseExpr");
    }

    public void visit(This n){
        indent();
        out.print("ThisExpr");
    }

    private void printBinaryExpr(String name, Expr e1, Expr e2) {
        indent();
        out.print(name + "(");
        indentCount++;
        e1.accept(this);
        e2.accept(this);
        indentCount--;
        out.print(")");
    }

    public void visit(Plus n){ printBinaryExpr("AddExpr", n.e1, n.e2); }
    public void visit(Minus n){ printBinaryExpr("SubExpr", n.e1, n.e2); }
    public void visit(Times n){ printBinaryExpr("MulExpr", n.e1, n.e2); }
    public void visit(Divide n){ printBinaryExpr("DivExpr", n.e1, n.e2); }
    public void visit(LessThan n){ printBinaryExpr("LesserExpr", n.e1, n.e2); }
    public void visit(GreaterThan n){ printBinaryExpr("GreaterExpr", n.e1, n.e2); }
    public void visit(AndExpression n){ printBinaryExpr("AndExpr", n.e1, n.e2); }
    public void visit(OrExpression n){ printBinaryExpr("OrExpr", n.e1, n.e2); }

    public void visit(Not n){
        indent();
        out.print("NotExpr(");
        indentCount++;
        n.e.accept(this);
        indentCount--;
        out.print(")");
    }

    public void visit(Assign n){
        indent();
        out.print("AssignStmt(");
        indentCount++;
        indent();
        out.print("IdentifierExpr(" + n.id.s + ")");
        n.ex.accept(this);
        indentCount--;
        out.print(")");
    }

    public void visit(ArrayAssign n){
        indent();
        out.print("AssignStmt(");
        indentCount++;
        indent();
        out.print("ArrayExpr(");
        indentCount++;
        indent();
        out.print("IdentifierExpr(" + n.i.s + ")");
        n.e1.accept(this);
        indentCount--;
        out.print(")");
        n.e2.accept(this);
        indentCount--;
        out.print(")");
    }

    public void visit(If n){
        indent();
        out.print("IfStmt(");
        indentCount++;
        n.ex.accept(this);
        n.st1.accept(this);
        if(n.st2 != null){
            n.st2.accept(this);
        }
        indentCount--;
        out.print(")");
    }

    public void visit(While n){
        indent();
        out.print("WhileStmt(");
        indentCount++;
        n.ex.accept(this);
        n.st.accept(this);
        indentCount--;
        out.print(")");
    }

    public void visit(Block n){
        indent();
        out.print("BlockStmt(");
        indentCount++;
        visit(n.sl);
        indentCount--;
        out.print(")");
    }

    public void visit(ArrayLookup n){
        indent();
        out.print("ArrayExpr(");
        indentCount++;
        n.ex1.accept(this);
        n.ex2.accept(this);
        indentCount--;
        out.print(")");
    }

    public void visit(ArrayLength n){
        indent();
        out.print("FieldExpr(");
        indentCount++;
        n.ex.accept(this);
        indent();
        out.print("length");
        indentCount--;
        out.print(")");
    }

    public void visit(Call n){
        indent();
        out.print("CallExpr(");
        indentCount++;
        n.ex.accept(this);
        indent();
        out.print(n.id.s); 
        visit(n.li);
        indentCount--;
        out.print(")");
    }

    public void visit(NewArray n){
        indent();
        out.print("NewArrayExpr(IntegerType");
        indentCount++;
        indent();
        out.print("AbstractList(");
        indentCount++;
        n.ex.accept(this);
        indentCount--;
        out.print(")");
        indentCount--;
        out.print(")");
    }

    public void visit(NewObject n){
        indent();
        out.print("NewObjectExpr(");
        indentCount++;
        out.print("IdentifierType(" + n.id.s + ")");
        indentCount--;
        out.print(")");

    }
    public void visit(NullExpr n){
        indent();
        out.print("NullExpr");
    }

    public void visit(EqualExpr n){
        printBinaryExpr("EqualExpr", n.ex1, n.ex2);
        }
    public void visit(NotEqExpr n){
        printBinaryExpr("EqualExpr", n.ex1, n.ex2);
    }
    public void visit(NegExpr n){
        indent();
        out.print("NegExpr(");
        indentCount++;
        n.ex1.accept(this);
        indentCount--;
        out.print(")");
    }
    public void visit(XinuCallExpr n) {
        indent();
        out.print("XinuCallExpr(" + n.method);
        indentCount++;
        visit(n.args);
        indentCount--;
        out.print(")");
    }



}