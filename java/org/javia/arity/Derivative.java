package org.javia.arity;

public class Derivative extends Function {
    private static final double H = 1.0E-12d;
    private static final double INVH = 1.0E12d;
    private Complex c = new Complex();
    private final Function f;

    public Derivative(Function f) throws ArityException {
        this.f = f;
        f.checkArity(1);
    }

    public int arity() {
        return 1;
    }

    public double eval(double x) {
        return this.f.eval(this.c.set(x, H)).im * INVH;
    }
}
