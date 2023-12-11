package compiler.sintatico;

import compiler.CompilerError;
import compiler.lexico.*;
import compiler.semantico.Semantic;

import java.lang.Float;
import java.util.Scanner;

public class Parser {

    private Lexer lexer;
    private Semantic semantic;
    private Token token;

    private String valorAtual;
    private int tipoAtual;

    Scanner input = new Scanner(System.in);

    public Parser(Lexer lexer, Semantic semantic) {
        this.lexer = lexer;
        this.semantic = semantic;
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
            CompilerError.error("Erro de sintaxe: Token desconhecido com tag de valor \"" + tag + "\" ou token inválido: ", token);
        }
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
                eat(Tag.FINAL_DE_ARQUIVO);
                break;
            default:
                CompilerError.error("Erro de sintaxe no método 'program': class ou identificador não encontrado", token);
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
                CompilerError.error("Erro de sintaxe no método 'decl': tipo (int, float ou string) ou ';' não encontrado", token);
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
                CompilerError.error("Erro de sintaxe no método 'decl': tipo (int, float ou string) não encontrado", token);
        }
    }

    private void ident_list() {
        switch (token.tag) {
            case Tag.IDENTIFICADOR:
                identifier();
                if (semantic.estaDeclarado(valorAtual)) {
                    CompilerError.error("Erro semantico no método 'ident_list': Variavel " + valorAtual + " ja esta declarada", token);
                } else {
                    Lexer.tabelaDeSimbolos.put(valorAtual, new Word(valorAtual, Tag.IDENTIFICADOR));
                    semantic.adicionarTipo(valorAtual, tipoAtual);
                }
                while (token.tag == Tag.VIRGULA) {
                    eat(Tag.VIRGULA);
                    identifier();
                    if (semantic.estaDeclarado(valorAtual)) {
                        CompilerError.error("Erro semantico no método 'ident_list': Variavel " + valorAtual + " ja esta declarada", token);
                    } else {
                        Lexer.tabelaDeSimbolos.put(valorAtual, new Word(valorAtual, Tag.IDENTIFICADOR));
                        semantic.adicionarTipo(valorAtual, tipoAtual);
                    }
                }
                break;
            default:
                CompilerError.error("Erro de sintaxe no método 'ident_list': identificador ou ',' não encontrado", token);
        }
    }

    private void type() {
        switch (token.tag) {
            case Tag.INT:
                tipoAtual = Type.INT;
                eat(Tag.INT);
                break;
            case Tag.FLOAT:
                tipoAtual = Type.FLOAT;
                eat(Tag.FLOAT);
                break;
            case Tag.STRING:
                tipoAtual = Type.STRING;
                eat(Tag.STRING);
                break;
            default:
                CompilerError.error("Erro de sintaxe no método 'type': tipo (int, float ou string) não encontrado", token);
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
                CompilerError.error("Erro de sintaxe no método 'body': '{' ou '}' não encontrado", token);
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
                CompilerError.error("Erro de sintaxe no método 'stmt_list': identificador, 'if', 'do', 'read' ou 'write' não encontrado", token);
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
                CompilerError.error("Erro de sintaxe no método 'stmt': identificador, 'if', 'do', 'read' ou 'write' não encontrado", token);
        }
    }

    private void assign_stmt() {
        switch (token.tag) {
            case Tag.IDENTIFICADOR:
                identifier();
                if (semantic.estaDeclarado(valorAtual)) {
                    int tipoDoLadoEsquerdo = semantic.getTipoDoIdentificador(valorAtual);
                    String valorDoLadoEsquerdo = valorAtual;
                    eat(Tag.ATRIBUICAO);
                    simple_expr();
                    int tipoDoLadoDireito = tipoAtual;
                    String valorDoLadoDireito = "";
                    if (semantic.estaDeclarado(valorAtual)) {
                        valorDoLadoDireito = semantic.getValorDoIdentificador(valorAtual);
                    } else {
                        valorDoLadoDireito = valorAtual;
                    }
                    if (tipoDoLadoEsquerdo != tipoDoLadoDireito) {
                        CompilerError.error("Erro semantico no método 'assign_stmt': tipo incompativel", token);
                    } else {
                        semantic.adicionarValor(valorDoLadoEsquerdo, valorDoLadoDireito);
                    }
                } else {
                    CompilerError.error("Erro semantico no método 'assign_stmt': Identificado nao declarado", token);
                }
                break;
            default:
                CompilerError.error("Erro de sintaxe no método 'assign_stmt': identificador ou '=' não encontrado", token);
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
                CompilerError.error("Erro de sintaxe no método 'if_stmt': 'if', '(', ')', '{' ou '}' não encontrado", token);
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
                CompilerError.error("Erro de sintaxe no método 'condition': idenfiticador, constante, '!' ou '-' não encontrado", token);
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
                CompilerError.error("Erro de sintaxe no método 'do_stmt': 'do' não encontrado", token);
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
                CompilerError.error("Erro de sintaxe no método 'do_sufix': 'while' não encontrado", token);
        }
    }

    private void read_stmt() {
        switch (token.tag) {
            case Tag.READ:
                eat(Tag.READ);
                eat(Tag.ABRE_PARENTESES);
                identifier();
                if (semantic.estaDeclarado(valorAtual)) {
                    semantic.adicionarValor(valorAtual, input.nextLine());
                    eat(Tag.FECHA_PARENTESES);
                } else {
                    CompilerError.error("Erro de semantico no método 'read_stmt': Identificador nao declarado", token);
                }
                break;
            default:
                CompilerError.error("Erro de sintaxe no método 'read_stmt': 'read' não encontrado", token);
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
                CompilerError.error("Erro de sintaxe no método 'write_stmt': 'write' não encontrado", token);
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
                CompilerError.error("Erro de sintaxe no método 'writable': idenfiticador, constante, '!' ou '-' não encontrado", token);
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
                CompilerError.error("Erro de sintaxe no método 'expression': idenfiticador, constante, '!' ou '-' não encontrado", token);
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
                int tipoDoLadoEsquerdo = tipoAtual;
                String valorDoLadoEsquerdo = "";
                if (semantic.estaDeclarado(valorAtual)) {
                    valorDoLadoEsquerdo = semantic.getValorDoIdentificador(valorAtual);
                } else {
                    valorDoLadoEsquerdo = valorAtual;
                }
                relop();
                String op = valorAtual;
                simple_expr();
                int tipoDoLadoDireito = tipoAtual;
                String valorDoLadoDireito = "";
                if (semantic.estaDeclarado(valorAtual)) {
                    valorDoLadoDireito = semantic.getValorDoIdentificador(valorAtual);
                } else {
                    valorDoLadoDireito = valorAtual;
                }
                if (tipoDoLadoEsquerdo != tipoDoLadoDireito) {
                    CompilerError.error("Erro semantico no método 'expression_linha': tipo incompativel", token);
                } else {
                    switch (op) {
                        case "!=":
                            if (tipoAtual == Type.INT)
                                valorAtual = String.valueOf(Integer.parseInt(valorDoLadoEsquerdo) != Integer.parseInt(valorDoLadoDireito));
                            else if (tipoAtual == Type.FLOAT)
                                valorAtual = String.valueOf(Float.parseFloat(valorDoLadoEsquerdo) != Float.parseFloat(valorDoLadoDireito));
                            else if (tipoAtual == Type.STRING)
                                valorAtual = String.valueOf(!valorDoLadoEsquerdo.equals(valorDoLadoDireito));
                            break;
                        case "==":
                            if (tipoAtual == Type.INT)
                                valorAtual = String.valueOf(Integer.parseInt(valorDoLadoEsquerdo) == Integer.parseInt(valorDoLadoDireito));
                            else if (tipoAtual == Type.FLOAT)
                                valorAtual = String.valueOf(Float.parseFloat(valorDoLadoEsquerdo) == Float.parseFloat(valorDoLadoDireito));
                            else if (tipoAtual == Type.STRING)
                                valorAtual = String.valueOf(valorDoLadoEsquerdo.equals(valorDoLadoDireito));
                            break;
                        case ">=":
                            if (tipoAtual == Type.INT)
                                valorAtual = String.valueOf(Integer.parseInt(valorDoLadoEsquerdo) >= Integer.parseInt(valorDoLadoDireito));
                            else if (tipoAtual == Type.FLOAT)
                                valorAtual = String.valueOf(Float.parseFloat(valorDoLadoEsquerdo) >= Float.parseFloat(valorDoLadoDireito));
                            else if (tipoAtual == Type.STRING)
                                CompilerError.error("Erro semantico no método 'expression_linha': Operador invalido para o tipo", token);
                            break;
                        case ">":
                            if (tipoAtual == Type.INT)
                                valorAtual = String.valueOf(Integer.parseInt(valorDoLadoEsquerdo) > Integer.parseInt(valorDoLadoDireito));
                            else if (tipoAtual == Type.FLOAT)
                                valorAtual = String.valueOf(Float.parseFloat(valorDoLadoEsquerdo) > Float.parseFloat(valorDoLadoDireito));
                            else if (tipoAtual == Type.STRING)
                                CompilerError.error("Erro semantico no método 'expression_linha': Operador invalido para o tipo", token);
                            break;
                        case "<=":
                            if (tipoAtual == Type.INT)
                                valorAtual = String.valueOf(Integer.parseInt(valorDoLadoEsquerdo) <= Integer.parseInt(valorDoLadoDireito));
                            else if (tipoAtual == Type.FLOAT)
                                valorAtual = String.valueOf(Float.parseFloat(valorDoLadoEsquerdo) <= Float.parseFloat(valorDoLadoDireito));
                            else if (tipoAtual == Type.STRING)
                                CompilerError.error("Erro semantico no método 'expression_linha': Operador invalido para o tipo", token);
                            break;
                        case "<":
                            if (tipoAtual == Type.INT)
                                valorAtual = String.valueOf(Integer.parseInt(valorDoLadoEsquerdo) < Integer.parseInt(valorDoLadoDireito));
                            else if (tipoAtual == Type.FLOAT)
                                valorAtual = String.valueOf(Float.parseFloat(valorDoLadoEsquerdo) < Float.parseFloat(valorDoLadoDireito));
                            else if (tipoAtual == Type.STRING)
                                CompilerError.error("Erro semantico no método 'expression_linha': Operador invalido para o tipo", token);
                            break;
                        default:
                            CompilerError.error("Erro semantico no método 'expression_linha': Operador invalido", token);
                            break;
                    }
                }
                break;
            default:
                CompilerError.error("Erro de sintaxe no método 'expression_linha': operador relacional não encontrado em expression_linha", token);
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
                CompilerError.error("Erro de sintaxe no método 'simple_expr': idenfiticador, constante, '!' ou '-' não encontrado", token);
        }
    }

    private void simple_expr_linha() {
        switch (token.tag) {
            case Tag.ADICAO:
            case Tag.SUBTRACAO:
            case Tag.OR:
                int tipoDoLadoEsquerdo = tipoAtual;
                String valorDoLadoEsquerdo = "";
                if (semantic.estaDeclarado(valorAtual)) {
                    valorDoLadoEsquerdo = semantic.getValorDoIdentificador(valorAtual);
                } else {
                    valorDoLadoEsquerdo = valorAtual;
                }
                addop();
                String operador = valorAtual;
                term();
                int tipoDoLadoDireito = tipoAtual;
                String valorDoLadoDireito = "";
                if (semantic.estaDeclarado(valorAtual)) {
                    valorDoLadoDireito = semantic.getValorDoIdentificador(valorAtual);
                } else {
                    valorDoLadoDireito = valorAtual;
                }
                if (tipoDoLadoEsquerdo != tipoDoLadoDireito) {
                    CompilerError.error("Erro semantico no método 'simple_expr_linha': tipo incompativel", token);
                } else {
                    switch (operador) {
                        case "+":
                            if (tipoAtual == Type.INT)
                                valorAtual = String.valueOf(Integer.parseInt(valorDoLadoEsquerdo) + Integer.parseInt(valorDoLadoDireito));
                            else if (tipoAtual == Type.FLOAT)
                                valorAtual = String.valueOf(Float.parseFloat(valorDoLadoEsquerdo) + Float.parseFloat(valorDoLadoDireito));
                            else if (tipoAtual == Type.STRING)
                                valorAtual = valorDoLadoEsquerdo + valorDoLadoDireito;
                            break;
                        case "-":
                            if (tipoAtual == Type.INT)
                                valorAtual = String.valueOf(Integer.parseInt(valorDoLadoEsquerdo) - Integer.parseInt(valorDoLadoDireito));
                            else if (tipoAtual == Type.FLOAT)
                                valorAtual = String.valueOf(Float.parseFloat(valorDoLadoEsquerdo) - Float.parseFloat(valorDoLadoDireito));
                            else if (tipoAtual == Type.STRING)
                                CompilerError.error("Erro semantico no método 'simple_expr_linha': Operador invalido para o tipo", token);
                            break;
                        case "||":
                            valorAtual = String.valueOf(Boolean.parseBoolean(valorDoLadoEsquerdo) || Boolean.parseBoolean(valorDoLadoDireito));
                            break;
                        default:
                            CompilerError.error("Erro semantico no método 'simple_expr_linha': Operador invalido", token);
                            break;
                    }
                }
                if (Tag.ADICAO == token.tag || Tag.SUBTRACAO == token.tag || Tag.OR == token.tag) {
                    simple_expr_linha();
                }
                break;
            default:
                CompilerError.error("Erro de sintaxe no método 'simple_expr_linha': operador de adição ('+' ou '-') ou '||' não encontrado", token);
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
                CompilerError.error("Erro de sintaxe no método 'term': idenfiticador, constante, '!' ou '-' não encontrado", token);
        }
    }

    private void term_linha() {
        switch (token.tag) {
            case Tag.MULTIPLICACAO:
            case Tag.DIVISAO:
            case Tag.AND:
                /*mulop();
                factor_a();
                if (Tag.MULTIPLICACAO == token.tag || Tag.DIVISAO == token.tag || Tag.AND == token.tag) {
                    term_linha();
                }*/
                int tipoDoLadoEsquerdo = tipoAtual;
                String valorDoLadoEsquerdo = "";
                if (semantic.estaDeclarado(valorAtual)) {
                    valorDoLadoEsquerdo = semantic.getValorDoIdentificador(valorAtual);
                } else {
                    valorDoLadoEsquerdo = valorAtual;
                }
                mulop();
                String operador = valorAtual;
                factor_a();
                int tipoDoLadoDireito = tipoAtual;
                String valorDoLadoDireito = "";
                if (semantic.estaDeclarado(valorAtual)) {
                    valorDoLadoDireito = semantic.getValorDoIdentificador(valorAtual);
                } else {
                    valorDoLadoDireito = valorAtual;
                }
                if (tipoDoLadoEsquerdo != tipoDoLadoDireito) {
                    CompilerError.error("Erro semantico no método 'term_linha': tipo incompativel", token);
                } else {
                    switch (operador) {
                        case "*":
                            if (tipoAtual == Type.INT)
                                valorAtual = String.valueOf(Integer.parseInt(valorDoLadoEsquerdo) + Integer.parseInt(valorDoLadoDireito));
                            else if (tipoAtual == Type.FLOAT)
                                valorAtual = String.valueOf(Float.parseFloat(valorDoLadoEsquerdo) + Float.parseFloat(valorDoLadoDireito));
                            else if (tipoAtual == Type.STRING)
                                CompilerError.error("Erro semantico no método 'term_linha': Operador invalido para o tipo", token);
                            break;
                        case "/":
                            if (tipoAtual == Type.INT)
                                valorAtual = String.valueOf(Integer.parseInt(valorDoLadoEsquerdo) - Integer.parseInt(valorDoLadoDireito));
                            else if (tipoAtual == Type.FLOAT)
                                valorAtual = String.valueOf(Float.parseFloat(valorDoLadoEsquerdo) - Float.parseFloat(valorDoLadoDireito));
                            else if (tipoAtual == Type.STRING)
                                CompilerError.error("Erro semantico no método 'term_linha': Operador invalido para o tipo", token);
                            break;
                        case "&&":
                            valorAtual = String.valueOf(Boolean.parseBoolean(valorDoLadoEsquerdo) || Boolean.parseBoolean(valorDoLadoDireito));
                            break;
                        default:
                            CompilerError.error("Erro semantico no método 'term_linha': Operador invalido", token);
                            break;
                    }
                }
                if (Tag.MULTIPLICACAO == token.tag || Tag.DIVISAO == token.tag || Tag.AND == token.tag) {
                    term_linha();
                }
                break;
            default:
                CompilerError.error("Erro de sintaxe no método 'term_linha': operador de multiplicação ('*' ou '/') ou '&&' não encontrado", token);
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
                eat(Tag.EXCLAMACAO);
                factor();
                valorAtual = String.valueOf(!Boolean.parseBoolean(valorAtual));
            case Tag.SUBTRACAO:
                eat(token.tag);
                factor();
                if (tipoAtual == Type.INT)
                    valorAtual = String.valueOf(-Integer.parseInt(valorAtual));
                else if (tipoAtual == Type.FLOAT)
                    valorAtual = String.valueOf(-Float.parseFloat(valorAtual));
                else if (tipoAtual == Type.STRING)
                    CompilerError.error("Erro semantico no método 'factor_a': Operador invalido para o tipo", token);
                break;
            default:
                CompilerError.error("Erro de sintaxe no método 'factor_a': idenfiticador, constante, '!' ou '-' não encontrado", token);
        }
    }

    private void factor() {
        switch (token.tag) {
            case Tag.IDENTIFICADOR:
                identifier();
                if (semantic.estaDeclarado(valorAtual)) {
                    tipoAtual = semantic.getTipoDoIdentificador(valorAtual);
                    break;
                } else {
                    CompilerError.error("Erro semantico no método 'factor': Identificador '" + valorAtual + "' nao declarado", token);
                }
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
                CompilerError.error("Erro de sintaxe no método 'factor': idenfiticador, constante ou abre parênteses não encontrado", token);
        }
    }

    private void relop() {
        switch (token.tag) {
            case Tag.MAIOR:
                valorAtual = ">";
                eat(Tag.MAIOR);
                break;
            case Tag.MAIOR_IGUAL:
                valorAtual = ">=";
                eat(Tag.MAIOR_IGUAL);
                break;
            case Tag.MENOR:
                valorAtual = "<";
                eat(Tag.MENOR);
                break;
            case Tag.MENOR_IGUAL:
                valorAtual = "<=";
                eat(Tag.MENOR_IGUAL);
                break;
            case Tag.DIFERENTE:
                valorAtual = "!=";
                eat(Tag.DIFERENTE);
                break;
            case Tag.IGUAL:
                valorAtual = "==";
                eat(Tag.IGUAL);
                break;
            default:
                CompilerError.error("Erro de sintaxe no método 'relop': operador relacional não encontrado", token);
        }
    }

    private void addop() {
        switch (token.tag) {
            case Tag.ADICAO:
                valorAtual = "+";
                eat(Tag.ADICAO);
                break;
            case Tag.SUBTRACAO:
                valorAtual = "-";
                eat(Tag.SUBTRACAO);
                break;
            case Tag.OR:
                valorAtual = "||";
                eat(Tag.OR);
                break;
            default:
                CompilerError.error("Erro de sintaxe no método 'addop': operador de adição ('+' ou '-') ou '||' não encontrado", token);
        }
    }

    private void mulop() {
        switch (token.tag) {
            case Tag.MULTIPLICACAO:
                valorAtual = "*";
                eat(Tag.MULTIPLICACAO);
                break;
            case Tag.DIVISAO:
                valorAtual = "/";
                eat(Tag.DIVISAO);
                break;
            case Tag.AND:
                valorAtual = "&&";
                eat(Tag.AND);
                break;
            default:
                CompilerError.error("Erro de sintaxe no método 'mulop': operador de multiplicação ('*' ou '/') ou '&&' não encontrado", token);
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
                CompilerError.error("Erro de sintaxe no método 'constant': constante (inteiro, float ou literal) não encontrada", token);
        }
    }

    private void integer_const() {
        switch (token.tag) {
            case Tag.INT:
                if (token.tag == Tag.INT) {
                    valorAtual = Integer.toString(((compiler.lexico.Int) token).value);
                    tipoAtual = Type.INT;
                    eat(Tag.INT);
                } else {
                    CompilerError.error("Erro semantico no método 'integer_const': Constante inteira faltando", token);
                }
                break;
            default:
                CompilerError.error("Erro de sintaxe no método 'integer_const': inteiro não encontrado", token);
        }
    }

    private void real_const() {
        switch (token.tag) {
            case Tag.FLOAT:
                if (token.tag == Tag.FLOAT) {
                    valorAtual = Float.toString(((compiler.lexico.Float) token).value);
                    tipoAtual = Type.FLOAT;
                    eat(Tag.FLOAT);
                } else {
                    CompilerError.error("Erro semantico no método 'real_const': Constante float faltando", token);
                }
                break;
            default:
                CompilerError.error("Erro de sintaxe no método 'real_const': float não encontrado", token);
        }
    }

    private void literal() {
        switch (token.tag) {
            case Tag.ABRE_COMENTARIO:
                eat(Tag.ASPAS);
                if (token.tag == Tag.STRING) {
                    valorAtual = ((compiler.lexico.Literal) token).value;
                    tipoAtual = Type.STRING;
                    eat(Tag.STRING);
                } else {
                    CompilerError.error("Erro semantico no método 'literal': Constante string faltando", token);
                }
                eat(Tag.ASPAS);
                break;
            default:
                CompilerError.error("Erro de sintaxe no método 'literal': Abre aspas, string ou fecha aspas não encontrado", token);
        }
    }

    private void identifier() {
        switch (token.tag) {
            case Tag.IDENTIFICADOR:
                valorAtual = ((Word) token).getLexeme();
                eat(Tag.IDENTIFICADOR);
                break;
            default:
                CompilerError.error("Erro de sintaxe no método 'identifier': Identificador não encontrado", token);
        }
    }
}