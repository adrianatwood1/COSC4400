/* Copyright (C) 1997-2003, Purdue Research Foundation of Purdue University.
 * All rights reserved.  */


/**
 * COSC 4400 - Project 7
 * Tree Translation 
 * @adrian atwood pj panarese
 * Dr.Brylow
 * TA-BOT:MAILTO adrian.atwood-langeler@marquette.edu patrick.panarese@marquette.edu
 */


package Translate;
import Parse.MiniJavaParser;
import Parse.ParseException;
import Types.GlobalSymbolTable;
import java.io.PrintWriter;



public class Main
{
    public static void main(String[] args)
    {
        try
        {
            MiniJavaParser parser = new MiniJavaParser(System.in);
            Absyn.Program ast = parser.Goal();

            GlobalSymbolTable global = new GlobalSymbolTable();
            ast.accept(new Semant.BuildSymbolVisitor(global));
            for (String className : global.classes.keySet())
                global.getClass(className).assembleInheritance(global);
            Semant.Main.hasError = false;
            ast.accept(new Semant.TypeCheckVisitor(global));

            if (Semant.Main.hasError){
				return;
			} 

            Translate translate = new Translate(new Riscv.RiscvFrame(), global);
            ast.accept(translate);

            PrintWriter writer = new PrintWriter(System.out);
            for (Frag fra : translate.results())
            {
                if (fra instanceof DataFrag)
                {
                    writer.println("DataFrag(");
                    writer.println(fra);
                    writer.println(")");
                }
                else
                {
                    ProcFrag proc = (ProcFrag) fra;
                    writer.println("ProcFrag(");
                    proc.frame.printFrame(writer);
                    if (proc.body != null)
                    {
                        new Tree.Print(writer, proc.body);
                        writer.println();
                    }
                    writer.println(")");
                }
            }
            writer.flush();
        }
        catch (ParseException e)
        {
            System.err.println("Parse Error: " + e.getMessage());
        }
    }
}