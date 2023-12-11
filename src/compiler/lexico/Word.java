package compiler.lexico;

public class Word extends Token{
    private String lexeme = null;
    public String valor;
    public int tipo;

    // Palavras reservadas
    public static final Word class_word = new Word ("class", Tag.CLASS);
    public static final Word if_word = new Word ("if", Tag.IF);
    public static final Word else_word = new Word ("else", Tag.ELSE);
    public static final Word do_word = new Word ("do", Tag.DO);
    public static final Word while_word = new Word ("while", Tag.WHILE);
    public static final Word read_word = new Word ("read", Tag.READ);
    public static final Word write_word = new Word ("write", Tag.WRITE);

    // Tipos
    public static final Word int_word = new Word ("int", Tag.INT);
    public static final Word string_word = new Word ("string", Tag.STRING);
    public static final Word float_word = new Word ("float", Tag.FLOAT);

    // Pontuação
    public static final Word ponto = new Word (".", Tag.PONTO);
    public static final Word ponto_e_virgula = new Word (";", Tag.PONTO_E_VIRGULA);
    public static final Word virgula = new Word (",", Tag.VIRGULA);
    public static final Word abre_chaves = new Word ("{", Tag.ABRE_CHAVES);
    public static final Word fecha_chaves = new Word ("}", Tag.FECHA_CHAVES);
    public static final Word abre_parenteses = new Word ("(", Tag.ABRE_PARENTESES);
    public static final Word fecha_parenteses = new Word (")", Tag.FECHA_PARENTESES);

    // Operadores relacionais
    public static final Word and = new Word ("&&", Tag.AND);
    public static final Word or = new Word ("||", Tag.OR);

    // Operadores aritméticos
    public static final Word adicao = new Word ("+", Tag.ADICAO);
    public static final Word subtracao = new Word ("-", Tag.SUBTRACAO);
    public static final Word multiplicacao = new Word ("*", Tag.MULTIPLICACAO);
    public static final Word divisao = new Word ("/", Tag.DIVISAO);

    // Operadores de comparação
    public static final Word maior = new Word (">", Tag.MAIOR);
    public static final Word maior_igual = new Word (">=", Tag.MAIOR_IGUAL);
    public static final Word menor = new Word ("<", Tag.MENOR);
    public static final Word menor_igual = new Word ("<=", Tag.MENOR_IGUAL);
    public static final Word diferente = new Word ("!=", Tag.DIFERENTE);
    public static final Word igual = new Word ("==", Tag.IGUAL);

    // Outros operadores
    public static final Word atribuicao = new Word ("=", Tag.ATRIBUICAO);
    public static final Word exclamacao = new Word ("!", Tag.EXCLAMACAO);
    public static final Word comentario_de_uma_linha = new Word ("//", Tag.COMENTARIO_DE_UMA_LINHA);
    public static final Word abre_comentario = new Word ("/*", Tag.ABRE_COMENTARIO);
    public static final Word fecha_comentario = new Word ("*/", Tag.FECHA_COMENTARIO);
    public static final Word concatenacao = new Word ("+", Tag.CONCATENACAO);
    public static final Word aspas = new Word ("\"", Tag.ASPAS);

    public Word (String s, int tag){
        super (tag);
        lexeme = s;
    }
    public String toString(){
        return "<" + lexeme + ", " + tag + ">";
    }

    public String getLexeme() {
        return lexeme;
    }
}
