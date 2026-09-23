/* Copyright (C) 2007, Marquette University.  All rights reserved. */
package Absyn;

/**
 * Interface for Visitor Pattern traversals.
 */

public interface Visitor
{
    /** Visitor pattern dispatch. */
    //    public void visit(Absyn ast);
    public void visit(java.util.AbstractList<Visitable> list);
    public void visit(ArrayType ast);
    public void visit(ClassDecl ast);
    public void visit(Formal ast);
    public void visit(IdentifierType ast);
    public void visit(IntegerLiteral ast);
    public void visit(MethodDecl ast);
    public void visit(Program ast);
    public void visit(StringLiteral ast);
    public void visit(VarDecl ast);
    public void visit(XinuCallStmt ast);
    public void visit(BooleanType ast);
    public void visit(IntegerType ast);
    public void visit(ArrayAssign ast);
    public void visit(Assign ast);
    public void visit(Block ast);
    public void visit(If ast);
    public void visit(While ast);
    public void visit(AndExpression ast);
    public void visit(ArrayLength ast);
    public void visit(ArrayLookup ast);
    public void visit(Call ast);
    public void visit(Divide ast);
    public void visit(False ast);
    public void visit(GreaterThan ast);
    public void visit(LessThan ast);
    public void visit(IdentifierExp ast);
    public void visit(Minus ast);
    public void visit(NewArray ast);
    public void visit(NewObject ast);
    public void visit(Not ast);
    public void visit(Times ast);
    public void visit(True ast);
    public void visit(Identifier ast);
    public void visit(This ast);
    public void visit(Plus ast);
    public void visit(OrExpression ast);
    public void visit(NullExpr ast);
    public void visit(NotEqExpr ast);
    public void visit(EqualExpr ast);
    public void visit(NegExpr ast);
    public void visit(XinuCallExpr ast);


}
