package Parse;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.FileNotFoundException;

/*
 * COSC 4400 - Project 3
 * Building a Parser for our mini java syntax
 * @authors Adrian Atwood and PJ panarese
 * Instructor Dr.Brylow
 * TA-BOT:MAILTO adrian.atwood-langeler@marquette.edu patrick.panarese@marquette.edu
 * 
 * Gemini was used to help clean up constant formatting issues within print.visitor file, prompt was to give a guide to the syntax of the different methods that were used. All code was changed by us.
 */



public class Main
{   
    public static void main(String [] args) 
    {
	InputStreamReader isr =	new InputStreamReader(System.in);
        Reader reader = new BufferedReader(isr);

	try{
		if(args.length > 0){
			reader = new BufferedReader(new FileReader(args[0]));
		} else {
			reader = new BufferedReader(new InputStreamReader(System.in));
		}
	
	Absyn.Program parse = new MiniJavaParser(reader).Goal();
	PrintWriter writer = new PrintWriter(System.out);
	Absyn.PrintVisitor pv =	new Absyn.PrintVisitor(writer);
	pv.visit(parse);
	writer.flush();
	} catch (FileNotFoundException e){
		System.out.println("Error: File not found -> " + (args.length > 0 ? args[0] : "unknown"));
		System.exit(-1);
	} catch (Exception e){
		System.out.println("Parse/Compilation Error: " + e.toString());
		e.printStackTrace();
		System.exit(-1);
	}
    }
}
