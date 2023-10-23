import lexico.*;
import sintatico.Parser;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String fileName = args[0];

        /*try {
            Lexer lexer = new Lexer(fileName);

            System.out.println("\n******************************\n");
            System.out.println("Tokens lidos:\n");
            System.out.println("\n******************************\n");
            while (true) {
                Token tokenDoScan = lexer.scan();
                if (tokenDoScan instanceof Literal) {
                    Literal token = (Literal) tokenDoScan;
                    System.out.print(token.toString());
                } else if (tokenDoScan instanceof Int) {
                    Int token = (Int) tokenDoScan;
                    System.out.print(token.toString());
                } else if (tokenDoScan instanceof Float) {
                    Float token = (Float) tokenDoScan;
                    System.out.print(token.toString());
                } else if (tokenDoScan.tag == Tag.FINAL_DE_ARQUIVO) {
                    break;
                } else {
                    System.out.print(tokenDoScan.toString());
                }
            }

            System.out.println("\n******************************\n");
            System.out.println("Fim dos tokens lidos\n");

            System.out.println("\n\n******************************\n\n");

            System.out.println("Tabela de símbolos:\n");
            System.out.println("\n******************************\n");

            Set<String> lexema = Lexer.tabelaDeSimbolos.keySet();
            for (String simbolo : lexema) {
                System.out.print("[Token: " + Lexer.tabelaDeSimbolos.get(simbolo).toString() + ", Lexema: " + simbolo + "]   ");
            }

            System.out.println("\n******************************\n");
            System.out.println("Fim da tabela de símbolos\n");

            System.out.println("\n\n******************************\n\n");

            Set<Token> listaDeErros = Lexer.tableDeErros.keySet();
            if (listaDeErros.size() > 0) {

                System.out.println("Lista de erros:\n");
                System.out.println("\n******************************\n");

                for (Token erro : listaDeErros) {
                    System.out.print(
                            "Erro: Símbolo " + (char) erro.tag + " não reconhecido na linha " + Lexer.tableDeErros.get(erro));
                    System.out.println();
                }

                System.out.println("Fim da lista de erros\n");
                System.out.println("\n******************************\n");
            } else
                System.out.println("\nAnálise concluída!\n");
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        try {
            Lexer lexer = new Lexer(fileName);
            Parser parser = new Parser(lexer);
            parser.start();
            System.out.println("\nAnálise concluída!\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}