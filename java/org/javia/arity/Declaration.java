package org.javia.arity;

class Declaration {
    private static final String[] NO_ARGS = new String[0];
    String[] args;
    int arity;
    String expression;
    String name;

    Declaration() {
    }

    void parse(String source, Lexer lexer, DeclarationParser declParser) throws SyntaxException {
        int equalPos = source.indexOf(61);
        if (equalPos == -1) {
            this.expression = source;
            this.name = null;
            this.args = NO_ARGS;
            this.arity = -2;
            return;
        }
        String decl = source.substring(0, equalPos);
        this.expression = source.substring(equalPos + 1);
        lexer.scan(decl, declParser);
        this.name = declParser.name;
        this.args = declParser.argNames();
        this.arity = declParser.arity;
    }
}
