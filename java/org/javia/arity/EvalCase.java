package org.javia.arity;

class EvalCase {
    static final double ERR = -2.0d;
    static final double FUN = -3.0d;
    Complex cResult;
    String expr;
    double result;

    EvalCase(String expr, double result) {
        this.expr = expr;
        this.result = result;
    }

    EvalCase(String expr, Complex result) {
        this.expr = expr;
        this.cResult = result;
    }
}
