package org.javia.arity;

public class EvalContext {
    static final int MAX_STACK_SIZE = 128;
    double[] args1 = new double[1];
    Complex[] args1c;
    double[] args2 = new double[2];
    Complex[] args2c;
    int stackBase = 0;
    final Complex[] stackComplex = new Complex[128];
    double[] stackRe = new double[128];

    public EvalContext() {
        for (int i = 0; i < 128; i++) {
            this.stackComplex[i] = new Complex();
        }
        this.args1c = new Complex[]{new Complex()};
        this.args2c = new Complex[]{new Complex(), new Complex()};
    }
}
