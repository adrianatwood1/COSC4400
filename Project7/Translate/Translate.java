/* Copyright (C) 1997-2003, Purdue Research Foundation of Purdue University.
 * All rights reserved.  */
package Translate;
import java.util.*;
import Temp.Label;
import Temp.Temp;
import Symbol.Symbol;
import Symbol.Table;
import Types.ClassSymbolTable;
import Types.GlobalSymbolTable;
import Types.MethodSymbolTable;

/**
 * Implements translation of abstract syntax expressions
 * into intermediate trees.
 */

public class Translate implements Absyn.TreeVisitor 
{
    public Frame.Frame frame;
    private GlobalSymbolTable global;
    private ClassSymbolTable currentClass;

    public Translate(Frame.Frame frame, GlobalSymbolTable global) { 
        this.frame = frame;
        this.global = global;
    }

    private static Tree.Exp CONST(int value)  { return new Tree.CONST(value); }
    private static Tree.Exp NAME(Label label) {	return new Tree.NAME(label); }
    private static Tree.Exp TEMP(Temp temp)   {	return new Tree.TEMP(temp); }
    private static Tree.Exp BINOP(int binop, Tree.Exp left, Tree.Exp right) 
    {   return new Tree.BINOP(binop, left, right);   }
    private static Tree.Exp MEM(Tree.Exp exp) {	return new Tree.MEM(exp); }
    private static Tree.Exp CALL(Tree.Exp func, List<Tree.Exp> args) 
    {	return new Tree.CALL(func, args);   }
    private static Tree.Exp ESEQ(Tree.Stm stm, Tree.Exp exp)
    {
	if (stm == null) return exp;
	return new Tree.ESEQ(stm, exp);
    }

    private static Tree.Stm MOVE(Tree.Exp dst, Tree.Exp src) 
    {
	return new Tree.MOVE(dst, src);
    }
    private static Tree.Stm EXP(Tree.Exp exp) {	return new Tree.EXP(exp); }
    private static Tree.Stm JUMP(Label target) 
    {	return new Tree.JUMP(target);   }
    private static Tree.Stm CJUMP(int relop, Tree.Exp l, Tree.Exp r,
				  Label t, Label f)
    {	return new Tree.CJUMP(relop, l, r, t, f);   }
    private static Tree.Stm SEQ(Tree.Stm left, Tree.Stm right) 
    {
	if (left == null)
	    return right;
	if (right == null)
	    return left;
	return new Tree.SEQ(left, right);
    }
    private static Tree.Stm LABEL(Label label) 
    {
	return new Tree.LABEL(label);
    }

    private LinkedList<Frag> frags = new LinkedList<Frag>();
    public List<Frag> results() { return frags; }

    private HashMap<String, Label>  vtableEnv = new HashMap<String, Label>();
    private HashMap<String, Label>  strings   = new HashMap<String, Label>();
    private Table                   varEnv    = new Table();

    private Tree.Exp thisExp()
    {
	return ((Frame.Access) varEnv.get(Symbol.symbol("this"))).exp(frame);
    }

    private Tree.Exp field(Tree.Exp obj, int index)
    {
	Label bad = frame.badPtr();
	Label ok = new Label();
	Temp result = new Temp();
	return ESEQ(SEQ(MOVE(TEMP(result), obj),
			CJUMP(Tree.CJUMP.EQ, TEMP(result), CONST(0), bad, ok)),
		    ESEQ(LABEL(ok),
			 MEM(BINOP(Tree.BINOP.PLUS, TEMP(result),
				   CONST(index * frame.wordSize())))));
    }

    private Tree.Exp element(Tree.Exp base, Tree.Exp index)
    {
	Temp a = new Temp();
	Temp i = new Temp();
	Label badPtr = frame.badPtr();
	Label badSub = frame.badSub();
	Label ok1 = new Label();
	Label ok2 = new Label();
	Label ok3 = new Label();
	int w = frame.wordSize();
	return ESEQ(
	    SEQ(MOVE(TEMP(a), base),
	    SEQ(MOVE(TEMP(i), index),
	    SEQ(CJUMP(Tree.CJUMP.EQ, TEMP(a), CONST(0), badPtr, ok1),
	    SEQ(LABEL(ok1),
	    SEQ(CJUMP(Tree.CJUMP.LT, TEMP(i), CONST(0), badSub, ok2),
	    SEQ(LABEL(ok2),
		CJUMP(Tree.CJUMP.GE, TEMP(i),
		      MEM(BINOP(Tree.BINOP.PLUS, TEMP(a), CONST(-w))),
		      badSub, ok3))))))),
	    ESEQ(LABEL(ok3),
		 MEM(BINOP(Tree.BINOP.PLUS, TEMP(a),
			   BINOP(Tree.BINOP.MUL, TEMP(i), CONST(w))))));
    }

    private Tree.Exp variable(String name)
    {
	Frame.Access a = (Frame.Access) varEnv.get(Symbol.symbol(name));
	if (a != null) return a.exp(frame);
	return field(thisExp(), currentClass.getFieldIndex(name));
    }

    private Tree.Exp xinuCall(String method, List<Absyn.Expr> args)
    {
	LinkedList<Tree.Exp> list = new LinkedList<Tree.Exp>();
	if (args != null)
	    for (Absyn.Expr a : args)
		list.add(a.accept(this).unEx());
	return frame.extCall(Symbol.symbol(method), list);
    }

    private void buildVtable(Absyn.ClassDecl c)
    {
	ClassSymbolTable cst = global.getClass(c.name);
	Label vtable = new Label(c.name + "__vtable");
	vtableEnv.put(c.name, vtable);
	List<Label> entries = new LinkedList<Label>();
	for (MethodSymbolTable m : cst.objectMethods.values())
	    entries.add(new Label(m.parentClass.name + "__" + m.name));
	frags.add(new DataFrag(frame.record(vtable, entries)));
    }

    public Exp visit(Absyn.Program n)
    {
	// TODO
	for (Absyn.ClassDecl c : n.classes)
	    buildVtable(c);
	for (Absyn.ClassDecl c : n.classes)
	    c.accept(this);
	return null;
    }
    
    public Exp visit(Absyn.ClassDecl c) 
    {
	// TODO
	currentClass = global.getClass(c.name);
	for (Absyn.MethodDecl m : c.methods)
	    m.accept(this);
	return null;
    }

    public Exp visit(Absyn.MethodDecl m) 
    {
	// TODO
	Frame.Frame saved = frame;
	String name = m.name.equals("main") ? "main"
					    : currentClass.name + "__" + m.name;
	frame = saved.newFrame(Symbol.symbol(name));
	varEnv.beginScope();
	varEnv.put(Symbol.symbol("this"), frame.allocFormal());
	for (Absyn.Formal f : m.params)
	    varEnv.put(Symbol.symbol(f.name), frame.allocFormal());

	Tree.Stm body = null;
	for (Absyn.VarDecl v : m.locals)
	{
	    Frame.Access a = frame.allocLocal();
	    varEnv.put(Symbol.symbol(v.name), a);
	    Tree.Exp init = (v.init == null) ? CONST(0)
					     : v.init.accept(this).unEx();
	    body = SEQ(body, MOVE(a.exp(frame), init));
	}
	for (Absyn.Stmt s : m.stmts)
	    body = SEQ(body, s.accept(this).unNx());
	body = SEQ(body, MOVE(TEMP(frame.RV()),
			      m.returnVal.accept(this).unEx()));

	frags.add(new ProcFrag(body, frame));
	varEnv.endScope();
	frame = saved;
	return null;
    }

    public Exp visit(Absyn.Formal f) 
    {
	// TODO
	return null;
    }

    public Exp visit(Absyn.VarDecl v) 
    {
	// TODO
	return null;
    }

    public Exp visit(Absyn.BooleanType  t)   { return new Nx(null); }
    public Exp visit(Absyn.IntegerType  t)   { return new Nx(null); }
    public Exp visit(Absyn.ArrayType t)      { return new Nx(null); }
    public Exp visit(Absyn.IdentifierType t) { return new Nx(null); }

    public Exp visit(Absyn.Assign s) 
    {
	// TODO
	Tree.Exp dst = variable(s.id.s);
	Tree.Exp src = s.ex.accept(this).unEx();
	return new Nx(MOVE(dst, src));
    }

    public Exp visit(Absyn.ArrayAssign s) 
    {
	Tree.Exp base = variable(s.i.s);
	Tree.Exp index = s.e1.accept(this).unEx();
	Tree.Exp dst = element(base, index);
	Tree.Exp src = s.e2.accept(this).unEx();
	return new Nx(MOVE(dst, src));
    }
	
    public Exp visit(Absyn.Block b)
    {
	// TODO
	Tree.Stm result = null;
	for (Absyn.Stmt s : b.sl)
	    result = SEQ(result, s.accept(this).unNx());
	return new Nx(result);
    }

    public Exp visit(Absyn.If s)
    {
	// TODO
	Exp cond = s.ex.accept(this);
	Exp t = s.st1.accept(this);
	Exp f = (s.st2 == null) ? new Nx(null) : s.st2.accept(this);
	return new IfThenElseExp(cond, t, f);
    }

    public Exp visit(Absyn.While s)
    {
	// TODO
	Exp cond = s.ex.accept(this);
	Exp stm = s.st.accept(this);
	Label done = new Label();
	Label test = new Label();
	Label body = new Label();
	return new Nx(SEQ(SEQ(SEQ(LABEL(test), cond.unCx(body, done)),
			      SEQ(SEQ(LABEL(body), stm.unNx()),
				  JUMP(test))),
			  LABEL(done)));
    }

    private Exp binop(int op, Absyn.Expr l, Absyn.Expr r) 
    {
	Tree.Exp left = l.accept(this).unEx();
	Tree.Exp right = r.accept(this).unEx();
	return new Ex(BINOP(op, left, right));
    }

    public Exp visit(Absyn.Plus e)   { return binop(Tree.BINOP.PLUS,  e.e1, e.e2); }
    public Exp visit(Absyn.Divide e) { return binop(Tree.BINOP.DIV,   e.e1, e.e2); }
    public Exp visit(Absyn.Times e)  { return binop(Tree.BINOP.MUL,   e.e1, e.e2); }
    public Exp visit(Absyn.Minus e)  { return binop(Tree.BINOP.MINUS, e.e1, e.e2); }

    public Exp visit(Absyn.NegExpr e)
    {
	return new Ex(BINOP(Tree.BINOP.MINUS, 
			    CONST(0), e.ex1.accept(this).unEx()));
    }

    public Exp visit(Absyn.AndExpression e) 
    {
        Exp e1 = e.e1.accept(this);
	Exp e2 = e.e2.accept(this);
	// Short-circuit Boolean AND, with false == 0.
	return new IfThenElseExp(e1, e2, new Ex(CONST(0)));
    }

    public Exp visit(Absyn.OrExpression e)
    {
	// TODO
	Exp e1 = e.e1.accept(this);
	Exp e2 = e.e2.accept(this);
	return new IfThenElseExp(e1, new Ex(CONST(1)), e2);
    }

    private Exp relcx(int op, Absyn.Expr l, Absyn.Expr r)
    {
	Tree.Exp left = l.accept(this).unEx();
	Tree.Exp right = r.accept(this).unEx();
	return new RelCx(op, left, right);
    }

    public Exp visit(Absyn.GreaterThan e)  { return relcx(Tree.CJUMP.GT, e.e1, e.e2); }
    public Exp visit(Absyn.LessThan e)     { return relcx(Tree.CJUMP.LT, e.e1, e.e2); }
    public Exp visit(Absyn.EqualExpr e)    { return relcx(Tree.CJUMP.EQ, e.ex1, e.ex2); }
    public Exp visit(Absyn.NotEqExpr e)    { return relcx(Tree.CJUMP.NE, e.ex1, e.ex2); }

    public Exp visit(Absyn.Not e)
    {
        Exp exp = e.e.accept(this);
        return new IfThenElseExp(exp, new Ex(CONST(0)), new Ex(CONST(1)));
    }

    public Exp visit(Absyn.NewArray e) 
    {
	// TODO
	Tree.Exp size = e.ex.accept(this).unEx();
	Temp t = new Temp();
	LinkedList<Tree.Exp> args = new LinkedList<Tree.Exp>();
	args.add(TEMP(t));
	args.add(TEMP(t));
	return new Ex(ESEQ(MOVE(TEMP(t), size),
			   frame.extCall(Symbol.symbol("new"), args)));
    }

    public Exp visit(Absyn.XinuCallStmt s)
    {
	// TODO
	return new Nx(EXP(xinuCall(s.method, s.args)));
    }

    public Exp visit(Absyn.XinuCallExpr e)
    {
	// TODO
	return new Ex(xinuCall(e.method, e.args));
    }

    public Exp visit(Absyn.Call e) 
    {
	// TODO
	Tree.Exp obj = e.ex.accept(this).unEx();
	Temp r = new Temp();
	Label ok = new Label();
	LinkedList<Tree.Exp> args = new LinkedList<Tree.Exp>();
	args.add(TEMP(r));
	if (e.li != null)
	    for (Object a : e.li)
		args.add(((Absyn.Expr) a).accept(this).unEx());
	int w = frame.wordSize();
	Tree.Exp vtable = MEM(BINOP(Tree.BINOP.PLUS, TEMP(r), CONST(-w)));
	Tree.Exp fn = MEM(BINOP(Tree.BINOP.PLUS, vtable, CONST(e.typeIndex * w)));
	return new Ex(ESEQ(SEQ(MOVE(TEMP(r), obj),
			       SEQ(CJUMP(Tree.CJUMP.EQ, TEMP(r), CONST(0),
					 frame.badPtr(), ok),
				   LABEL(ok))),
			   CALL(fn, args)));
    }

    public Exp visit(Absyn.False e)
    {   return new Ex(CONST(0));  }

    public Exp visit(Absyn.IntegerLiteral e)    
    {   return new Ex(CONST(e.value));   }

    public Exp visit(Absyn.NullExpr e)
    {   return new Ex(CONST(0));   }

    public Exp visit (Absyn.NewObject e) 
    {
    	// TODO
	ClassSymbolTable cst = global.getClass(e.id.s);
	LinkedList<Tree.Exp> args = new LinkedList<Tree.Exp>();
	args.add(CONST(cst.objectFields.size()));
	args.add(NAME(vtableEnv.get(e.id.s)));
	return new Ex(frame.extCall(Symbol.symbol("new"), args));
    }

    public Exp visit(Absyn.StringLiteral e) 
    {
	String result = e.value.intern();
	Label lab = strings.get(result);
	if (lab != null) return new Ex(NAME(lab));
	lab = new Label();
	strings.put(result, lab);
	
	DataFrag frag = new DataFrag(frame.string(lab, result.intern()));
	frags.add(frag);
	return new Ex(NAME(lab));
    }

    public Exp visit(Absyn.This e) 
    {
    	// TODO
	return new Ex(thisExp());
    }
    
    public Exp visit(Absyn.True e)
    {   return new Ex(CONST(1));   }

    public Exp visit(Absyn.ArrayLength e) 
    {
    	// TODO
	return new Ex(field(e.ex.accept(this).unEx(), e.typeIndex));
    }

    public Exp visit(Absyn.IdentifierExp e) 
    {
    	// TODO
	return new Ex(variable(e.st));
    }

    public Exp visit(Absyn.ArrayLookup e)
    {
    	// TODO
	Tree.Exp base = e.ex1.accept(this).unEx();
	Tree.Exp index = e.ex2.accept(this).unEx();
	return new Ex(element(base, index));
    }
}