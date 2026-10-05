import Absyn.Program;
import Parse.MiniJavaParser;
import Parse.ParseException;
import Types.GlobalSymbolTable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class main{
    public static void main(String[] args){
        try {
            InputStream in = System.in;
            if(args.length > 0){
                in = new FileInputStream(args[0]);
            }
            MiniJavaParser parser = new MiniJavaParser(in);
            Program ast = parser.Goal();
            GlobalSymbolTable global = new GlobalSymbolTable();

            ast.accept(new BuildSymbolVisitor(global));
            ast.accept(new TypeCheckVisitor(global));

            //Printwriter out = new PrintWriter(System.out);
            //ast.accept(new PrintVisitor(out));
            //out.flush();

        } catch (ParseException e) {
            System.out.println("Parse Error: " + e.getMessage());
        } catch (FileNotFoundException e){
            System.out.println("Error: File not found. ");
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}