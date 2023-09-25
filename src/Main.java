import lexico.Lexer;
import lexico.Word;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        String fileName = args[0];

        Lexer lexer;

        try {
            lexer = new Lexer(fileName);

            System.out.println("\nAnálise concluída!\n\n");

            System.out.println("Tabela de símbolos:\n");
            System.out.println("\n******************************\n");

            HashMap<String,Word> tabelaDeSimbolos = lexer.getWords();
            for (Map.Entry<String, Word> entry : tabelaDeSimbolos.entrySet()) {
                String chave = entry.getKey();
                Word valor = entry.getValue();
                System.out.println("{ " + chave + " }" + " | " + valor.getLexeme() + "\n");
            }

            System.out.println("\n******************************\n");
            System.out.println("Fim da Tabela de símbolos\n");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}