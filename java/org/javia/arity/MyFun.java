package org.javia.arity;

/* compiled from: UnitTest */
class MyFun extends Function {
    Function f;
    Symbols symbols = new Symbols();

    MyFun() {
        try {
            this.f = this.symbols.compile("1-x");
        } catch (SyntaxException e) {
            System.out.println("" + e);
        }
    }

    public int arity() {
        return 1;
    }

    public double eval(double x) {
        return this.f.eval(x);
    }
}
