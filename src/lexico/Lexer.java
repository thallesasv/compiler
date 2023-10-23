package lexico;

import java.io.*;
import java.util.*;

public class Lexer {
    public static int line = 1; // Contador de linhas
    private char ch = ' '; // Caractere lido do arquivo
    private char charAnterior = ' '; // Caractere anterior lido do arquivo
    private FileReader file;
    public static Hashtable<String, Word> tabelaDeSimbolos = new Hashtable<String, Word>();
    public static Hashtable<Token, Integer> tableDeErros = new Hashtable<Token, Integer>();

    // Método para inserir palavras reservadas na HashTable
    private void reserve(Word w){
        tabelaDeSimbolos.put(w.getLexeme(), w); // Lexema é a chave de entrada na HashTable
    }

    // Método construtor
    public Lexer(String fileName) throws FileNotFoundException{
        try{
            file = new FileReader (fileName);
        }
        catch(FileNotFoundException e){
            System.out.println("Arquivo não encontrado");
            throw e;
        }

        //Insere palavras reservadas na HashTable
        reserve(Word.class_word);
        reserve(Word.if_word);
        reserve(Word.else_word);
        reserve(Word.do_word);
        reserve(Word.while_word);
        reserve(Word.read_word);
        reserve(Word.write_word);

        // Insere tipos na HashTable
        reserve(Word.int_word);
        reserve(Word.string_word);
        reserve(Word.float_word);

        // Insere pontuação na HashTable
        reserve(Word.ponto);
        reserve(Word.ponto_e_virgula);
        reserve(Word.virgula);
        reserve(Word.abre_chaves);
        reserve(Word.fecha_chaves);
        reserve(Word.abre_parenteses);
        reserve(Word.fecha_parenteses);

        // Insere operadores relacionais na HashTable
        reserve(Word.and);
        reserve(Word.or);

        // Insere operadores aritméticos na HashTable
        reserve(Word.adicao);
        reserve(Word.subtracao);
        reserve(Word.multiplicacao);
        reserve(Word.divisao);

        // Insere operadores de comparação na HashTable
        reserve(Word.maior);
        reserve(Word.maior_igual);
        reserve(Word.menor);
        reserve(Word.menor_igual);
        reserve(Word.diferente);
        reserve(Word.igual);

        // Insere outros operadores na HashTable
        reserve(Word.atribuicao);
        reserve(Word.exclamacao);
        reserve(Word.comentario_de_uma_linha);
        reserve(Word.abre_comentario);
        reserve(Word.fecha_comentario);
        reserve(Word.concatenacao);
        reserve(Word.aspas);
    }

    // Lê o próximo caractere do arquivo
    private void readch() throws IOException{
        ch = (char) file.read();
    }

    // Lê o próximo caractere do arquivo e verifica se é igual ao char c
    private boolean readch(char c) throws IOException{
        readch();
        if (ch != c) return false;
        ch = ' ';
        return true;
    }

    public Token scan() throws IOException{

        // Desconsidera delimitadores na entrada
        for (;; readch()) {
            if (ch == ' ' || ch == '\t' || ch == '\r' || ch == '\b') continue;
            else if (ch == '\n') line++; // Conta linhas
            else break;
        }

        // Comentários
        int contador_auxiliar_de_linha = 0;
        if (ch == '/') {
            readch();
            if (ch == '/') { // Identifica o comentário de uma linha
                do {
                    readch();
                    if (ch == '\n') { // Identifica que o comentário de uma linha acabou
                        readch();
                        line++;
                        break;
                    }
                } while ((int) ch != Tag.FINAL_DE_ARQUIVO);
            } else if (ch == '*') { // Identifica início do comentário de várias linhas
                contador_auxiliar_de_linha = line;
                do {
                    readch();
                    if (ch == '\n') { // Identifica a quebra de linha
                        line++;
                    }
                    if (ch == '*') {
                        readch();
                        if (ch == '/') { // Identifica o final do comentário de várias linhas
                            readch();
                            break;
                        }
                    }
                } while ((int) ch != Tag.FINAL_DE_ARQUIVO);
            } else {
                return Word.divisao;
            }

            if ((int) ch == Tag.FINAL_DE_ARQUIVO) {
                Token t = new Token(Tag.FINAL_DE_ARQUIVO);
                tableDeErros.put(t, contador_auxiliar_de_linha);
                return t;
            }

            if (ch == ' ' || ch == '\t' || ch == '\r' || ch == '\b' || ch == '\n') {
                return scan();
            }
        }

        switch(ch){

            case '&':
                if (readch('&')) return Word.and;
                else {
                    Token t = new Token('&');
                    tableDeErros.put(t, line);
                    return t;
                }
            case '|':
                if (readch('|')) return Word.or;
                else {
                    Token t = new Token('|');
                    tableDeErros.put(t, line);
                    return t;
                }
            case '=':
                if (readch('=')) return Word.igual;
                else return Word.atribuicao;
            case '<':
                if (readch('=')) return Word.menor_igual;
                else return Word.menor;
            case '>':
                if (readch('=')) return Word.maior_igual;
                else return Word.maior;
            case '.':
                return Word.ponto;
            case ';':
                return Word.ponto_e_virgula;
            case ',':
                return Word.virgula;
            case '{':
                return Word.abre_chaves;
            case '}':
                return Word.fecha_chaves;
            case '(':
                return Word.abre_parenteses;
            case ')':
                return Word.fecha_parenteses;
            case '+':
                if (readch('"')) return Word.concatenacao;
                else return Word.adicao;
            case '-':
                return Word.subtracao;
            case '*':
                if (readch('/')) return Word.fecha_comentario;
                else return Word.multiplicacao;
            case '/':
                if (readch('/')) return Word.comentario_de_uma_linha;
                else if (readch('*')) return Word.abre_comentario;
                else return Word.divisao;
            case '!':
                if (readch('=')) return Word.diferente;
                else return Word.exclamacao;
        }

        // Números
        if (Character.isDigit(ch)) {
            int value = 0;
            do {
                value = 10 * value + Character.digit(ch, 10);
                readch();
            } while (Character.isDigit(ch));

            if (ch != '.') {
                return new Int(value); // Inteiro
            } else {
                readch();
                float numeroFloat = value;
                float unidadeDecimal = 10;
                do {
                    numeroFloat = numeroFloat + Character.digit(ch, 10) / unidadeDecimal;
                    unidadeDecimal = unidadeDecimal * 10;
                    readch();
                } while (Character.isDigit(ch));
                return new Float(numeroFloat); // numero de ponto flutuante literal
            }
        }

        // Literais
        if (charAnterior == '"') {
            StringBuffer sb = new StringBuffer();
            do {
                sb.append(ch);
                readch();
                if (ch == '\n') {
                    line++;
                }
            } while (ch != '"' && (int) ch != Tag.FINAL_DE_ARQUIVO);
            sb.append(ch);
            readch();
            return new Literal(sb.toString());
        }

        // Identificadores
        if (Character.isLetter(ch)){
            StringBuffer sb = new StringBuffer();
            do{
                sb.append(ch);
                readch();
            }while(Character.isLetterOrDigit(ch));
            String s = sb.toString();
            Word w = (Word)tabelaDeSimbolos.get(s);
            if (w != null) return w; // Palavra já existe na HashTable
            w = new Word (s, Tag.IDENTIFICADOR);
            tabelaDeSimbolos.put(s, w);
            return w;
        }

        // Caracteres não especificados
        Token t = new Token(ch);
        ch = ' ';

        if (t.tag != Tag.FINAL_DE_ARQUIVO)
            tableDeErros.put(t, line);
        ch = ' ';

        return t;
    }
}
