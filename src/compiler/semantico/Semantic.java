package compiler.semantico;

import compiler.lexico.Lexer;
import compiler.lexico.Tag;
import compiler.lexico.Word;

public class Semantic {

    public Semantic() {
    }

    public boolean estaDeclarado(String lexema) {
        if (Lexer.tabelaDeSimbolos.containsKey(lexema) && Lexer.tabelaDeSimbolos.get(lexema).tag == Tag.IDENTIFICADOR) {
            return true;
        }
        return false;
    }

    public boolean osTiposSaoIguais(Word w1, Word w2) {
        if (w1.tipo == w2.tipo) {
            return true;
        }
        return false;
    }

    public void adicionarValor(String lexema, String valor) {
        Word w = Lexer.tabelaDeSimbolos.get(lexema);
        w.valor = valor;
        Lexer.tabelaDeSimbolos.put(lexema, w);
    }

    public void adicionarTipo(String lexeme, int tipo) {
        Word w = Lexer.tabelaDeSimbolos.get(lexeme);
        w.tipo = tipo;
        Lexer.tabelaDeSimbolos.put(lexeme, w);
    }

    public String getValorDoIdentificador(String lexeme) {
        Word w = Lexer.tabelaDeSimbolos.get(lexeme);
        return w.valor;
    }

    public int getTipoDoIdentificador(String lexeme) {
        Word w = Lexer.tabelaDeSimbolos.get(lexeme);
        return w.tipo;
    }
}
