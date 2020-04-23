package org.javia.arity;

abstract class TokenConsumer {
    TokenConsumer() {
    }

    abstract void push(Token token) throws SyntaxException;

    void start() {
    }
}
