package org.javia.arity;

public class Constant extends Function {
    private Complex value;

    public Constant(Complex o) {
        this.value = new Complex(o);
    }

    public int arity() {
        return 0;
    }

    public double eval() {
        return this.value.asReal();
    }

    public Complex evalComplex() {
        return this.value;
    }

    public String toString() {
        return this.value.toString();
    }
}
