package org.javia.arity;

public class SyntaxException extends Exception {
    public String expression;
    public String message;
    public int position;

    SyntaxException set(String str, int pos) {
        this.message = str;
        this.position = pos;
        fillInStackTrace();
        return this;
    }

    public String toString() {
        return "SyntaxException: " + this.message + " in '" + this.expression + "' at position " + this.position;
    }
}
