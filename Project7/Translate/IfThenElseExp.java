/* Copyright (C) 1997-2003, Purdue Research Foundation of Purdue University.
 * All rights reserved.  */
package Translate;
import Temp.Label;
import Temp.Temp;

/**
 * Represents an If-Then-Else Expression, a special case that can be 
 * an expression or statement depending on it's use.
 */

public class IfThenElseExp extends Exp {
    Exp cond, trueExp, falseExp;

    IfThenElseExp(Exp cExp, Exp tExp, Exp fExp) {
	cond = cExp; 
	trueExp = tExp; 
	falseExp = fExp;
    }
	private static Tree.Stm SEQ(Tree.Stm left, Tree.Stm right){
		if(left == null){
			return right;
		}
		if(right == null){
			return left;
		}
		return new Tree.SEQ(left,right);
	}
	private static Tree.Stm LABEL(Label l) { 
		return new Tree.LABEL(l); 
	} 
	private static Tree.Stm JUMP(Label l){
		return new Tree.JUMP(l);
	}
	private static Tree.Stm MOVE(Tree.Exp e, Tree.Exp s) {
		return new Tree.MOVE(e,s);
	}

	private static Tree.Exp TEMP(Temp t){
		return new Tree.TEMP(t);
	}
	private static Tree.Exp ESEQ(Tree.Stm s, Tree.Exp e){
		return new Tree.ESEQ(s,e);
	}
	private static Label target(Tree.Stm jump){
		return ((Tree.JUMP) jump).targets.getFirst();
	}

    Tree.Stm unCx(Label tt, Label ff) {

	// TODO: Check for optimizations

	Tree.Stm tStm = trueExp.unCx(tt, ff);
	Tree.Stm fStm = falseExp.unCx(tt, ff);
	boolean tJump = tStm instanceof Tree.JUMP;
	boolean fJump = fStm instanceof Tree.JUMP;
	if (tJump && fJump)
	    return cond.unCx(target(tStm), target(fStm));
	if (tJump) {
	    Label f = new Label();
	    return SEQ(cond.unCx(target(tStm), f), SEQ(LABEL(f), fStm));
	}
	if (fJump) {
	    Label t = new Label();
	    return SEQ(cond.unCx(t, target(fStm)), SEQ(LABEL(t), tStm));
	}
	Label t = new Label();
	Label f = new Label();
	return SEQ(cond.unCx(t, f),
		   SEQ(SEQ(LABEL(t), tStm),
		       SEQ(LABEL(f), fStm)));
    }

    Tree.Exp unEx() {
	// TODO: Check for optimizations
    Tree.Exp tExp = trueExp.unEx();
	Tree.Exp fExp = falseExp.unEx();
	Temp r = new Temp();
	Label t = new Label();
	Label f = new Label();
	Label join = new Label();
	return ESEQ(SEQ(SEQ(cond.unCx(t, f),
			    SEQ(SEQ(LABEL(t),
				    SEQ(MOVE(TEMP(r), tExp),
					JUMP(join))),
				SEQ(LABEL(f),
				    SEQ(MOVE(TEMP(r), fExp),
					JUMP(join))))),
			LABEL(join)),
		    TEMP(r));
    }

    Tree.Stm unNx() {
    Tree.Stm tStm = trueExp.unNx();
    Tree.Stm fStm = falseExp.unNx();
	Label t, f, join;
	t = new Label();
	f = new Label();
	join = new Label();
	tStm = SEQ(SEQ(LABEL(t), tStm), JUMP(join));
	fStm = SEQ(SEQ(LABEL(f), fStm), JUMP(join));
	return SEQ(SEQ(cond.unCx(t, f), SEQ(tStm, fStm)), LABEL(join));
    }
}