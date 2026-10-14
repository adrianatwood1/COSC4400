package Semant;
import Absyn.Program;
import Parse.MiniJavaParser;
import Parse.ParseException;
import Types.GlobalSymbolTable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * COSC 4400 - Project 4
 * Creating a type checker.
 * @authors adrian atwood and pj panarese
 * Instructor Dr.Brylow
 * TA-BOT:MAILTO adrian.atwood-langeler@marquette.edu  patrick.panarese@marquette.edu
 */
public class Main {
    public static boolean hasError = false; 
    public static void main(String[] args) {
        hasError = false;
        try {
            InputStream in = System.in;
            if(args.length > 0){
                in = new FileInputStream(args[0]);
            }
            
            MiniJavaParser parser = new MiniJavaParser(in);
            Program ast = parser.Goal();
            
            GlobalSymbolTable global = new GlobalSymbolTable();
            
            ast.accept(new BuildSymbolVisitor(global));
            
            for (String className : global.classes.keySet()) {
                global.getClass(className).assembleInheritance(global);
            }
            
            ast.accept(new TypeCheckVisitor(global));

            if (!hasError){
                ast.accept(new Absyn.PrintVisitor());
            }

            

        } catch (ParseException e) {
            System.err.println("Parse Error: " + e.getMessage());
        } catch (FileNotFoundException e){
            System.err.println("Error: File not found.");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}