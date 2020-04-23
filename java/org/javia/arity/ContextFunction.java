package org.javia.arity;

public abstract class ContextFunction extends Function {
    private static final double[] NO_ARGS = new double[0];
    private static final Complex[] NO_ARGS_COMPLEX = new Complex[0];
    private static EvalContext context = new EvalContext();

    public double eval() {
        return eval(NO_ARGS);
    }

    public double eval(double x) {
        double eval;
        synchronized (context) {
            eval = eval(x, context);
        }
        return eval;
    }

    public double eval(double x, double y) {
        double eval;
        synchronized (context) {
            eval = eval(x, y, context);
        }
        return eval;
    }

    public double eval(double x, double y, EvalContext context) {
        double[] args = context.args2;
        args[0] = x;
        args[1] = y;
        return eval(args, context);
    }

    public double eval(double x, EvalContext context) {
        double[] args = context.args1;
        args[0] = x;
        return eval(args, context);
    }

    public double eval(double[] args) {
        double eval;
        synchronized (context) {
            eval = eval(args, context);
        }
        return eval;
    }

    public abstract double eval(double[] dArr, EvalContext evalContext);

    public Complex eval(Complex x) {
        Complex eval;
        synchronized (context) {
            eval = eval(x, context);
        }
        return eval;
    }

    public Complex eval(Complex x, Complex y) {
        Complex eval;
        synchronized (context) {
            eval = eval(x, y, context);
        }
        return eval;
    }

    public Complex eval(Complex x, Complex y, EvalContext context) {
        Complex[] args = context.args2c;
        args[0] = x;
        args[1] = y;
        return eval(args, context);
    }

    public Complex eval(Complex x, EvalContext context) {
        Complex[] args = context.args1c;
        args[0] = x;
        return eval(args, context);
    }

    public Complex eval(Complex[] args) {
        Complex eval;
        synchronized (context) {
            eval = eval(args, context);
        }
        return eval;
    }

    public abstract Complex eval(Complex[] complexArr, EvalContext evalContext);

    public Complex evalComplex() {
        return eval(NO_ARGS_COMPLEX);
    }

    Complex[] toComplex(double[] args, EvalContext context) {
        Complex[] argsC;
        switch (args.length) {
            case 0:
                return NO_ARGS_COMPLEX;
            case 1:
                argsC = context.args1c;
                argsC[0].set(args[0], 0.0d);
                return argsC;
            case 2:
                argsC = context.args2c;
                argsC[0].set(args[0], 0.0d);
                argsC[1].set(args[1], 0.0d);
                return argsC;
            default:
                argsC = new Complex[args.length];
                for (int i = 0; i < args.length; i++) {
                    argsC[i] = new Complex(args[i], 0.0d);
                }
                return argsC;
        }
    }
}
