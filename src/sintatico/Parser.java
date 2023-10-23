package sintatico;

import lexico.Lexer;
import lexico.Token;
import lexico.Tag;

public class Parser {

    private Lexer lexer;
    private Token token;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    public void start() {
        advance();
        program();
    }

    private void advance() {
        try {
            token = lexer.scan(); // Lê próximo token
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void eat(int tag) {
        if (token.tag == tag)
            advance();
        else {
            error("Erro de sintaxe: Token desconhecido ou token inválido: " + tag);
        }
    }

    private void error(String mensagemDeErro) {
        throw new Error("Erro na linha " + Lexer.line + "\n" +
                "Mensagem de erro: " + mensagemDeErro + "\n" +
                " Token que gerou o erro: " + token.toString() + "\n");
    }

    private void program() {
        switch (token.tag) {
            case Tag.CLASS:
                eat(Tag.CLASS);
                identifier();
                if (token.tag == Tag.INT || token.tag == Tag.FLOAT || token.tag == Tag.STRING) {
                    decl_list();
                }
                body();
                break;
            default:
                error("Erro de sintaxe no método 'program': class ou identificador não encontrado");
        }
    }

    private void decl_list() {
        switch (token.tag) {
            case Tag.INT:
            case Tag.FLOAT:
            case Tag.STRING:
                decl();
                eat(Tag.PONTO_E_VIRGULA);
                while (token.tag == Tag.INT || token.tag == Tag.FLOAT || token.tag == Tag.STRING) {
                    decl();
                    eat(Tag.PONTO_E_VIRGULA);
                }
                break;
            default:
                error("Erro de sintaxe no método 'decl': tipo (int, float ou string) ou ';' não encontrado");
        }
    }

    private void decl() {
        switch (token.tag) {
            case Tag.INT:
            case Tag.FLOAT:
            case Tag.STRING:
                type();
                ident_list();
                break;
            default:
                error("Erro de sintaxe no método 'decl': tipo (int, float ou string) não encontrado");
        }
    }

    private void ident_list() {
        switch (token.tag) {
            case Tag.IDENTIFICADOR:
                identifier();
                while (token.tag == Tag.VIRGULA) {
                    eat(Tag.VIRGULA);
                    identifier();
                }
                break;
            default:
                error("Erro de sintaxe no método 'ident_list': identificador ou ',' não encontrado");
        }
    }

    private void type() {
        switch (token.tag) {
            case Tag.INT:
                eat(Tag.INT);
                break;
            case Tag.FLOAT:
                eat(Tag.FLOAT);
                break;
            case Tag.STRING:
                eat(Tag.STRING);
                break;
            default:
                error("Erro de sintaxe no método 'type': tipo (int, float ou string) não encontrado");
        }
    }

    private void body() {
        switch (token.tag) {
            case Tag.ABRE_CHAVES:
                eat(Tag.ABRE_CHAVES);
                stmt_list();
                eat(Tag.FECHA_CHAVES);
                break;
            default:
                error("Erro de sintaxe no método 'body': '{' ou '}' não encontrado");
        }
    }

    private void stmt_list() {
        switch (token.tag) {
            case Tag.IDENTIFICADOR:
            case Tag.IF:
            case Tag.DO:
            case Tag.READ:
            case Tag.WRITE:
                stmt();
                while (token.tag == Tag.IDENTIFICADOR || token.tag == Tag.IF || token.tag == Tag.DO || token.tag == Tag.READ || token.tag == Tag.WRITE) {
                    stmt();
                    eat(Tag.PONTO_E_VIRGULA);
                }
                break;
            default:
                error("Erro de sintaxe no método 'stmt_list': identificador, 'if', 'do', 'read' ou 'write' não encontrado");
        }
    }

    private void stmt() {
        switch (token.tag) {
            case Tag.IDENTIFICADOR:
                assign_stmt();
                break;
            case Tag.IF:
                if_stmt();
                break;
            case Tag.DO:
                do_stmt();
                break;
            case Tag.READ:
                read_stmt();
                break;
            case Tag.WRITE:
                write_stmt();
                break;
            default:
                error("Erro de sintaxe no método 'stmt': identificador, 'if', 'do', 'read' ou 'write' não encontrado");
        }
    }

    private void assign_stmt() {
        switch (token.tag) {
            case Tag.IDENTIFICADOR:
                identifier();
                eat(Tag.ATRIBUICAO);
                simple_expr();
                break;
            default:
                error("Erro de sintaxe no método 'assign_stmt': identificador ou '=' não encontrado");
        }
    }

    private void if_stmt() {
        switch (token.tag) {
            case Tag.IF:
                eat(Tag.IF);
                eat(Tag.ABRE_PARENTESES);
                condition();
                eat(Tag.FECHA_PARENTESES);
                eat(Tag.ABRE_CHAVES);
                stmt_list();
                eat(Tag.FECHA_CHAVES);
                if_stmt_linha();
                break;
            default:
                error("Erro de sintaxe no método 'if_stmt': 'if', '(', ')', '{' ou '}' não encontrado");
        }
    }

    private void if_stmt_linha() {
        switch (token.tag) {
            case Tag.ELSE:
                eat(Tag.ELSE);
                eat(Tag.ABRE_CHAVES);
                stmt_list();
                eat(Tag.FECHA_CHAVES);
                break;
            default:
                break;
        }
    }

    private void condition() {
        switch (token.tag) {
            case Tag.IDENTIFICADOR:
            case Tag.INT:
            case Tag.FLOAT:
            case Tag.ASPAS:
            case Tag.ABRE_PARENTESES:
            case Tag.EXCLAMACAO:
            case Tag.SUBTRACAO:
                expression();
                break;
            default:
                error("Erro de sintaxe no método 'condition': idenfiticador, constante, '!' ou '-' não encontrado");
        }
    }

    private void do_stmt() {
        switch (token.tag) {
            case Tag.DO:
                eat(Tag.DO);
                eat(Tag.ABRE_CHAVES);
                stmt_list();
                eat(Tag.FECHA_CHAVES);
                do_sufix();
                break;
            default:
                error("Erro de sintaxe no método 'do_stmt': 'do' não encontrado");
        }
    }

    private void do_sufix() {
        switch (token.tag) {
            case Tag.WHILE:
                eat(Tag.WHILE);
                eat(Tag.ABRE_PARENTESES);
                condition();
                eat(Tag.FECHA_PARENTESES);
                break;
            default:
                error("Erro de sintaxe no método 'do_sufix': 'while' não encontrado");
        }
    }

    private void read_stmt() {
        switch (token.tag) {
            case Tag.READ:
                eat(Tag.READ);
                eat(Tag.ABRE_PARENTESES);
                identifier();
                eat(Tag.FECHA_PARENTESES);
                break;
            default:
                error("Erro de sintaxe no método 'read_stmt': 'read' não encontrado");
        }
    }

    private void write_stmt() {
        switch (token.tag) {
            case Tag.WRITE:
                eat(Tag.WRITE);
                eat(Tag.ABRE_PARENTESES);
                writable();
                eat(Tag.FECHA_PARENTESES);
                break;
            default:
                error("Erro de sintaxe no método 'write_stmt': 'write' não encontrado");
        }
    }

    private void writable() {
        switch (token.tag) {
            case Tag.IDENTIFICADOR:
            case Tag.INT:
            case Tag.FLOAT:
            case Tag.ASPAS:
            case Tag.ABRE_PARENTESES:
            case Tag.EXCLAMACAO:
            case Tag.SUBTRACAO:
                simple_expr();
                break;
            default:
                error("Erro de sintaxe no método 'writable': idenfiticador, constante, '!' ou '-' não encontrado");
        }
    }

    private void expression() {
        switch (token.tag) {
            case Tag.IDENTIFICADOR:
            case Tag.INT:
            case Tag.FLOAT:
            case Tag.ASPAS:
            case Tag.ABRE_PARENTESES:
            case Tag.EXCLAMACAO:
            case Tag.SUBTRACAO:
                simple_expr();
                expression_linha(); // Possui recursão à esquerda, então foi necessário separar
                break;
            default:
                error("Erro de sintaxe no método 'expression': idenfiticador, constante, '!' ou '-' não encontrado");
        }
    }

    private void expression_linha() {
        switch (token.tag) {
            case Tag.MAIOR:
            case Tag.MAIOR_IGUAL:
            case Tag.MENOR:
            case Tag.MENOR_IGUAL:
            case Tag.DIFERENTE:
            case Tag.IGUAL:
                relop();
                simple_expr();
                break;
            default:
                error("Erro de sintaxe no método 'expression_linha': operador relacional não encontrado em expression_linha");
        }
    }

    private void simple_expr() {
        switch (token.tag) {
            case Tag.IDENTIFICADOR:
            case Tag.INT:
            case Tag.FLOAT:
            case Tag.ASPAS:
            case Tag.ABRE_PARENTESES:
            case Tag.EXCLAMACAO:
            case Tag.SUBTRACAO:
                term();
                simple_expr_linha(); // Possui recursão à esquerda, então foi necessário separar
                break;
            default:
                error("Erro de sintaxe no método 'simple_expr': idenfiticador, constante, '!' ou '-' não encontrado");
        }
    }

    private void simple_expr_linha() {
        switch (token.tag) {
            case Tag.ADICAO:
            case Tag.SUBTRACAO:
            case Tag.OR:
                addop();
                term();
                if (Tag.ADICAO == token.tag || Tag.SUBTRACAO == token.tag || Tag.OR == token.tag) {
                    simple_expr_linha();
                }
                break;
            default:
                error("Erro de sintaxe no método 'simple_expr_linha': operador de adição ('+' ou '-') ou '||' não encontrado");
        }
    }

    private void term() {
        switch (token.tag) {
            case Tag.IDENTIFICADOR:
            case Tag.INT:
            case Tag.FLOAT:
            case Tag.ASPAS:
            case Tag.ABRE_PARENTESES:
            case Tag.EXCLAMACAO:
            case Tag.SUBTRACAO:
                factor_a();
                term_linha(); // Possui recursão à esquerda, então foi necessário separar
                break;
            default:
                error("Erro de sintaxe no método 'term': idenfiticador, constante, '!' ou '-' não encontrado");
        }
    }

    private void term_linha() {
        switch (token.tag) {
            case Tag.MULTIPLICACAO:
            case Tag.DIVISAO:
            case Tag.AND:
                mulop();
                factor_a();
                if (Tag.MULTIPLICACAO == token.tag || Tag.DIVISAO == token.tag || Tag.AND == token.tag) {
                    term_linha();
                }
                break;
            default:
                error("Erro de sintaxe no método 'term_linha': operador de multiplicação ('*' ou '/') ou '&&' não encontrado");
        }
    }

    private void factor_a() {
        switch (token.tag) {
            case Tag.IDENTIFICADOR:
            case Tag.INT:
            case Tag.FLOAT:
            case Tag.ASPAS:
            case Tag.ABRE_PARENTESES:
                factor();
                break;
            case Tag.EXCLAMACAO:
            case Tag.SUBTRACAO:
                eat(token.tag);
                factor();
                break;
            default:
                error("Erro de sintaxe no método 'factor_a': idenfiticador, constante, '!' ou '-' não encontrado");
        }
    }

    private void factor() {
        switch (token.tag) {
            case Tag.IDENTIFICADOR:
                identifier();
                break;
            case Tag.INT:
            case Tag.FLOAT:
            case Tag.ASPAS:
                constant();
                break;
            case Tag.ABRE_PARENTESES:
                eat(Tag.ABRE_PARENTESES);
                expression();
                eat(Tag.FECHA_PARENTESES);
                break;
            default:
                error("Erro de sintaxe no método 'factor': idenfiticador, constante ou abre parênteses não encontrado");
        }
    }

    private void relop() {
        switch (token.tag) {
            case Tag.MAIOR:
                eat(Tag.MAIOR);
                break;
            case Tag.MAIOR_IGUAL:
                eat(Tag.MAIOR_IGUAL);
                break;
            case Tag.MENOR:
                eat(Tag.MENOR);
                break;
            case Tag.MENOR_IGUAL:
                eat(Tag.MENOR_IGUAL);
                break;
            case Tag.DIFERENTE:
                eat(Tag.DIFERENTE);
                break;
            case Tag.IGUAL:
                eat(Tag.IGUAL);
                break;
            default:
                error("Erro de sintaxe no método 'relop': operador relacional não encontrado");
        }
    }

    private void addop() {
        switch (token.tag) {
            case Tag.ADICAO:
                eat(Tag.ADICAO);
                break;
            case Tag.SUBTRACAO:
                eat(Tag.SUBTRACAO);
                break;
            case Tag.OR:
                eat(Tag.OR);
                break;
            default:
                error("Erro de sintaxe no método 'addop': operador de adição ('+' ou '-') ou '||' não encontrado");
        }
    }

    private void mulop() {
        switch (token.tag) {
            case Tag.MULTIPLICACAO:
                eat(Tag.MULTIPLICACAO);
                break;
            case Tag.DIVISAO:
                eat(Tag.DIVISAO);
                break;
            case Tag.AND:
                eat(Tag.AND);
                break;
            default:
                error("Erro de sintaxe no método 'mulop': operador de multiplicação ('*' ou '/') ou '&&' não encontrado");
        }
    }

    private void constant() {
        switch (token.tag) {
            case Tag.INT:
                integer_const();
                break;
            case Tag.FLOAT:
                real_const();
                break;
            case Tag.ABRE_COMENTARIO:
                literal();
                break;
            default:
                error("Erro de sintaxe no método 'constant': constante (inteiro, float ou literal) não encontrada");
        }
    }

    private void integer_const() {
        switch (token.tag) {
            case Tag.INT:
                eat(Tag.INT);
                break;
            default:
                error("Erro de sintaxe no método 'integer_const': inteiro não encontrado");
        }
    }

    private void real_const() {
        switch (token.tag) {
            case Tag.FLOAT:
                eat(Tag.FLOAT);
                break;
            default:
                error("Erro de sintaxe no método 'real_const': float não encontrado");
        }
    }

    private void literal() {
        switch (token.tag) {
            case Tag.ABRE_COMENTARIO:
                eat(Tag.ASPAS);
                eat(Tag.STRING);
                eat(Tag.ASPAS);
                break;
            default:
                error("Erro de sintaxe no método 'literal': Abre aspas, string ou fecha aspas não encontrado");
        }
    }

    private void identifier() {
        switch (token.tag) {
            case Tag.IDENTIFICADOR:
                eat(Tag.IDENTIFICADOR);
                break;
            default:
                error("Erro de sintaxe no método 'identifier': Identificador não encontrado");
        }
    }

}
