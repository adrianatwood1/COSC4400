/* Copyright (C) 2007, Marquette University.  All rights reserved. */
package Absyn;

/**
 * Interface for Visitor Pattern traversals.
 */

public interface TreeVisitor
{
    /** Visitor pattern dispatch. */
    public Translate.Exp visit(Plus ast);
    public Translate.Exp visit(Minus ast);
    public Translate.Exp visit(Times ast);
    public Translate.Exp visit(Divide ast);
    public Translate.Exp visit(AndExpression ast);
    public Translate.Exp visit(OrExpression ast);
    public Translate.Exp visit(LessThan ast);
    public Translate.Exp visit(GreaterThan ast);
    public Translate.Exp visit(EqualExpr ast);
    public Translate.Exp visit(NotEqExpr ast);
    public Translate.Exp visit(Not ast);
    public Translate.Exp visit(NegExpr ast);
    public Translate.Exp visit(ArrayLookup ast);
    public Translate.Exp visit(ArrayLength ast);   
    public Translate.Exp visit(ArrayAssign ast);
    public Translate.Exp visit(Assign ast);
    public Translate.Exp visit(Block ast);
    public Translate.Exp visit(If ast);
    public Translate.Exp visit(While ast);
    public Translate.Exp visit(Call ast);
    public Translate.Exp visit(NewArray ast);
    public Translate.Exp visit(NewObject ast);
    public Translate.Exp visit(IdentifierExp ast);
    public Translate.Exp visit(IntegerLiteral ast);
    public Translate.Exp visit(StringLiteral ast);
    public Translate.Exp visit(True ast);
    public Translate.Exp visit(False ast);
    public Translate.Exp visit(NullExpr ast);
    public Translate.Exp visit(This ast);
    public Translate.Exp visit(XinuCallExpr ast);
    public Translate.Exp visit(XinuCallStmt ast);
    public Translate.Exp visit(Program ast);
    public Translate.Exp visit(ClassDecl ast);
    public Translate.Exp visit(MethodDecl ast);
    public Translate.Exp visit(Formal ast);
    public Translate.Exp visit(VarDecl ast);
    public Translate.Exp visit(ArrayType ast);
    public Translate.Exp visit(BooleanType ast);
    public Translate.Exp visit(IntegerType ast);
    public Translate.Exp visit(IdentifierType ast);
}