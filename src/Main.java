import lexico.*;
import lexico.Float;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        String fileName = args[0];

        try {
            Lexer lexer = new Lexer(fileName);
            Token tokenDoScan = lexer.scan();
           
            System.out.println("\n******************************\n");
            System.out.println("Tokens lidos:\n");
            System.out.println("\n******************************\n");



            while (tokenDoScan.getTag() != Tag.FINAL_DE_ARQUIVO) {

                if (tokenDoScan.getTag() == Tag.INT) {
                    Int token = (Int) tokenDoScan;
                    System.out.print(token.toString());
                } else if (tokenDoScan.getTag() == Tag.FLOAT) {
                    Float token = (Float) tokenDoScan;
                    System.out.print(token.toString());
                }else if (tokenDoScan.getTag() == Tag.STRING) {
                    Literal token = (Literal) tokenDoScan;
                    System.out.print(token.toString());
                } else {
                    System.out.print(tokenDoScan.toString() + "\n");
                }
            }

            System.out.println("\n******************************\n");
            System.out.println("Fim dos tokens lidos\n");

            System.out.println("\n\n******************************\n\n");

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
            System.out.println("\n******************************\n");

            System.out.println("\nAnálise concluída!\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}