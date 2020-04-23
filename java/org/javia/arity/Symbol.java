package org.javia.arity;

public class Symbol {
    static final int CONST_ARITY = -3;
    private int arity;
    Function fun;
    boolean isConst;
    private String name;
    byte op;
    double valueIm;
    double valueRe;

    Symbol(String name, double re, double im, boolean isConst) {
        this.isConst = false;
        setKey(name, CONST_ARITY);
        this.valueRe = re;
        this.valueIm = im;
        this.isConst = isConst;
    }

    Symbol(String name, double re, boolean isConst) {
        this(name, re, 0.0d, isConst);
    }

    private Symbol(String name, int arity, byte op, boolean isConst, int dummy) {
        this.isConst = false;
        setKey(name, arity);
        this.op = op;
        this.isConst = isConst;
    }

    Symbol(String name, Function fun) {
        this.isConst = false;
        setKey(name, fun.arity());
        this.fun = fun;
    }

    static Symbol makeArg(String name, int order) {
        return new Symbol(name, CONST_ARITY, (byte) (order + 38), false, 0);
    }

    static Symbol makeVmOp(String name, int op) {
        return new Symbol(name, VM.arity[op], (byte) op, true, 0);
    }

    static Symbol newEmpty(Symbol s) {
        return new Symbol(s.name, s.arity, (byte) 0, false, 0);
    }

    public boolean equals(Object other) {
        Symbol symbol = (Symbol) other;
        return this.name.equals(symbol.name) && this.arity == symbol.arity;
    }

    public int getArity() {
        return this.arity == CONST_ARITY ? 0 : this.arity;
    }

    public String getName() {
        return this.name;
    }

    public int hashCode() {
        return this.name.hashCode() + this.arity;
    }

    boolean isEmpty() {
        return this.op == (byte) 0 && this.fun == null && this.valueRe == 0.0d && this.valueIm == 0.0d;
    }

    Symbol setKey(String name, int arity) {
        this.name = name;
        this.arity = arity;
        return this;
    }

    public String toString() {
        return "Symbol '" + this.name + "' arity " + this.arity + " val " + this.valueRe + " op " + this.op;
    }
}
