package org.javia.arity;

import java.util.Vector;

class DeclarationParser extends TokenConsumer {
    static final int MAX_ARITY = 5;
    static final String[] NO_ARGS = new String[0];
    static final int UNKNOWN_ARITY = -2;
    Vector<String> args = new Vector();
    int arity;
    private SyntaxException exception;
    String name;

    DeclarationParser(SyntaxException e) {
        this.exception = e;
    }

    String[] argNames() {
        if (this.arity <= 0) {
            return NO_ARGS;
        }
        String[] argNames = new String[this.arity];
        this.args.copyInto(argNames);
        return argNames;
    }

    void push(Token token) throws SyntaxException {
        switch (token.id) {
            case 10:
                if (this.name == null) {
                    this.name = token.name;
                    this.arity = -2;
                    return;
                } else if (this.arity >= 0) {
                    this.args.addElement(token.name);
                    this.arity++;
                    if (this.arity > 5) {
                        throw this.exception.set("Arity too large " + this.arity, token.position);
                    }
                    return;
                } else {
                    throw this.exception.set("Invalid declaration", token.position);
                }
            case 11:
                if (this.name == null) {
                    this.name = token.name;
                    this.arity = 0;
                    return;
                }
                throw this.exception.set("repeated CALL in declaration", token.position);
            case 12:
            case 14:
            case 15:
                return;
            default:
                throw this.exception.set("invalid token in declaration", token.position);
        }
    }

    void start() {
        this.name = null;
        this.arity = -2;
        this.args.setSize(0);
    }
}
