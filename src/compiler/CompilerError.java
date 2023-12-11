package compiler;

import compiler.lexico.*;

public abstract class CompilerError {

        public static void error(String mensagemDoErro, Token tokenDoErro) {
            throw new Error("\nErro na linha " + Lexer.line + ": " + mensagemDoErro + "\nToken do erro: " + tokenDoErro.toString() + "\n");
        }
}