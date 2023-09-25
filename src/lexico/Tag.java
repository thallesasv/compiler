package lexico;

public class Tag {
    public final static int

        // Palavras reservadas
        CLASS = 256,
        IF = 257,
        ELSE = 258,
        DO = 259,
        WHILE = 260,
        READ = 261,
        WRITE = 262,

        // Tipos
        INT = 263,
        STRING = 264,
        FLOAT = 265,

        // Pontuação
        PONTO = 291,
        PONTO_E_VIRGULA = 266,
        VIRGULA = 267,
        ABRE_CHAVES = 268,
        FECHA_CHAVES = 269,
        ABRE_PARENTESES = 270,
        FECHA_PARENTESES = 271,


        // Operadores relacionais
        OR = 272,
        AND = 273,

        // Operadores aritméticos
        ADICAO = 274,
        SUBTRACAO = 275,
        MULTIPLICACAO = 276,
        DIVISAO = 277,

        // Operadores de comparação
        MAIOR = 278,
        MAIOR_IGUAL = 279,
        MENOR = 280,
        MENOR_IGUAL = 281,
        DIFERENTE = 282,
        IGUAL = 283,

        // Outros operadores
        ATRIBUICAO = 284,
        EXCLAMACAO = 285,
        COMENTARIO_DE_UMA_LINHA = 286,
        ABRE_COMENTARIO = 287,
        FECHA_COMENTARIO = 288,
        CONCATENACAO = 289,

        // Identificador
        IDENTIFICADOR = 290,

        // Final de arquivo
        FINAL_DE_ARQUIVO = 999;
}
