import lexico.Lexer;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws Exception {
        String fileName = args[0];

        Lexer lexer;

        try {
            lexer = new Lexer(fileName);

            System.out.println("Análise concluída!");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}