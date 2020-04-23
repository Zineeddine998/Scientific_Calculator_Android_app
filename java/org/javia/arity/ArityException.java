package org.javia.arity;

public class ArityException extends RuntimeException {
    public ArityException(int nArgs) {
        this("Didn't expect " + nArgs + " arguments");
    }

    public ArityException(String mes) {
        super(mes);
    }
}
