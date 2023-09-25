import lexico.Lexer;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        try {
            Lexer lexer = new Lexer("../../tests/" + args[0] + ".txt");
            System.out.println("Análise concluída!");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}