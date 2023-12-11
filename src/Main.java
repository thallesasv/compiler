import compiler.lexico.*;
import compiler.semantico.Semantic;
import compiler.sintatico.Parser;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String fileName = args[0];

        try {
            Lexer lexer = new Lexer(fileName);
            Semantic semantic = new Semantic();
            Parser parser = new Parser(lexer, semantic);
            parser.start();
            System.out.println("\nAnálise concluída!\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}