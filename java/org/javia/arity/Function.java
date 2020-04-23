package org.javia.arity;

public abstract class Function {
    private Function cachedDerivate = null;
    String comment;

    public abstract int arity();

    public void checkArity(int nArgs) throws ArityException {
        if (arity() != nArgs) {
            throw new ArityException("Expected " + arity() + " arguments, got " + nArgs);
        }
    }

    public double eval() {
        throw new ArityException(0);
    }

    public double eval(double x) {
        throw new ArityException(1);
    }

    public double eval(double x, double y) {
        throw new ArityException(2);
    }

    public double eval(double[] args) {
        switch (args.length) {
            case 0:
                return eval();
            case 1:
                return eval(args[0]);
            case 2:
                return eval(args[0], args[1]);
            default:
                throw new ArityException(args.length);
        }
    }

    public Complex eval(Complex x) {
        checkArity(1);
        return new Complex(x.im == 0.0d ? eval(x.re) : Double.NaN, 0.0d);
    }

    public Complex eval(Complex x, Complex y) {
        checkArity(2);
        double eval = (x.im == 0.0d && y.im == 0.0d) ? eval(x.re, y.re) : Double.NaN;
        return new Complex(eval, 0.0d);
    }

    public Complex eval(Complex[] args) {
        switch (args.length) {
            case 0:
                return evalComplex();
            case 1:
                return eval(args[0]);
            case 2:
                return eval(args[0], args[1]);
            default:
                int len = args.length;
                checkArity(len);
                double[] reArgs = new double[len];
                for (int i = args.length - 1; i >= 0; i--) {
                    if (args[i].im != 0.0d) {
                        return new Complex(Double.NaN, 0.0d);
                    }
                    reArgs[i] = args[i].re;
                }
                return new Complex(eval(reArgs), 0.0d);
        }
    }

    public Complex evalComplex() {
        checkArity(0);
        return new Complex(eval(), 0.0d);
    }

    public Function getDerivative() {
        if (this.cachedDerivate == null) {
            this.cachedDerivate = new Derivative(this);
        }
        return this.cachedDerivate;
    }

    void setDerivative(Function deriv) {
        this.cachedDerivate = deriv;
    }
}
