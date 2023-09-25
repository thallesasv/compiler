package lexico;

import java.io.*;
import java.util.*;

public class Lexer {
    public static int line = 1; // Contador de linhas
    private char ch = ' '; // Caractere lido do arquivo
    private FileReader file;

    private Hashtable words = new Hashtable();

    // Método para inserir palavras reservadas na HashTable
    private void reserve(Word w){
        words.put(w.getLexeme(), w); // Lexema é a chave de entrada na HashTable
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

        switch(ch){

            // Operadores
            case '&':
                if (readch('&')) return Word.and;
                else return new Token('&');
            case '|':
                if (readch('|')) return Word.or;
                else return new Token('|');
            case '=':
                if (readch('=')) return Word.igual;
                else return new Token('=');

            case '<':
                if (readch('=')) return Word.menor_igual;
                else return new Token('<');
            case '>':
                if (readch('=')) return Word.maior_igual;
                else return new Token('>');
        }

        // Números
        if (Character.isDigit(ch)){
            int value=0;
            do{
                value = 10*value + Character.digit(ch,10);
                readch();
            }while(Character.isDigit(ch));
            return new Int(value);
        }

        // Identificadores
        if (Character.isLetter(ch)){
            StringBuffer sb = new StringBuffer();
            do{
                sb.append(ch);
                readch();
            }while(Character.isLetterOrDigit(ch));
            String s = sb.toString();
            Word w = (Word)words.get(s);
            if (w != null) return w; // Palavra já existe na HashTable
            w = new Word (s, Tag.IDENTIFICADOR);
            words.put(s, w);
            return w;
        }

        // Caracteres não especificados
        Token t = new Token(ch);
        ch = ' ';
        return t;
    }
}
