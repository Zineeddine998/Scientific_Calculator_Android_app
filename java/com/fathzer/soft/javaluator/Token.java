package com.fathzer.soft.javaluator;

import com.fathzer.soft.javaluator.Operator.Associativity;

public class Token {
    static final Token FUNCTION_ARG_SEPARATOR = new Token(Kind.FUNCTION_SEPARATOR, null);
    private Object content;
    private Kind kind;

    private enum Kind {
        OPEN_BRACKET,
        CLOSE_BRACKET,
        FUNCTION_SEPARATOR,
        FUNCTION,
        OPERATOR,
        LITERAL
    }

    private Token(Kind kind, Object content) {
        if ((!kind.equals(Kind.OPERATOR) || (content instanceof Operator)) && ((!kind.equals(Kind.FUNCTION) || (content instanceof Function)) && (!kind.equals(Kind.LITERAL) || (content instanceof String)))) {
            this.kind = kind;
            this.content = content;
            return;
        }
        throw new IllegalArgumentException();
    }

    static Token buildCloseToken(BracketPair pair) {
        return new Token(Kind.CLOSE_BRACKET, pair);
    }

    static Token buildFunction(Function function) {
        return new Token(Kind.FUNCTION, function);
    }

    static Token buildLiteral(String literal) {
        return new Token(Kind.LITERAL, literal);
    }

    static Token buildOpenToken(BracketPair pair) {
        return new Token(Kind.OPEN_BRACKET, pair);
    }

    static Token buildOperator(Operator ope) {
        return new Token(Kind.OPERATOR, ope);
    }

    Associativity getAssociativity() {
        return getOperator().getAssociativity();
    }

    BracketPair getBrackets() {
        return (BracketPair) this.content;
    }

    Function getFunction() {
        return (Function) this.content;
    }

    Kind getKind() {
        return this.kind;
    }

    String getLiteral() {
        if (this.kind.equals(Kind.LITERAL)) {
            return (String) this.content;
        }
        throw new IllegalArgumentException();
    }

    Operator getOperator() {
        return (Operator) this.content;
    }

    int getPrecedence() {
        return getOperator().getPrecedence();
    }

    public boolean isCloseBracket() {
        return this.kind.equals(Kind.CLOSE_BRACKET);
    }

    public boolean isFunction() {
        return this.kind.equals(Kind.FUNCTION);
    }

    public boolean isFunctionArgumentSeparator() {
        return this.kind.equals(Kind.FUNCTION_SEPARATOR);
    }

    public boolean isLiteral() {
        return this.kind.equals(Kind.LITERAL);
    }

    public boolean isOpenBracket() {
        return this.kind.equals(Kind.OPEN_BRACKET);
    }

    public boolean isOperator() {
        return this.kind.equals(Kind.OPERATOR);
    }
}
