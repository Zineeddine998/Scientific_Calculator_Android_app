package org.javia.arity;

public class UnitTest {
    static boolean allOk = true;
    static int checkCounter = 0;
    private static final String[] profileCases = new String[]{"(100.5 + 20009.999)*(7+4+3)/(5/2)^3!)*2", "fun1(x)=(x+2)*(x+3)", "otherFun(x)=(fun1(x-1)*x+1)*(fun1(2-x)+10)", "log(x+30.5, 3)^.7*sin(x+.5)"};

    static void check(double v1, double v2) {
        checkCounter++;
        if (!equal(v1, v2)) {
            allOk = false;
            System.out.println("failed check #" + checkCounter + ": expected " + v2 + " got " + v1);
        }
    }

    static void check(Complex v1, Complex v2) {
        checkCounter++;
        if (!equal(v1.re, v2.re) || !equal(v1.im, v2.im)) {
            allOk = false;
            System.out.println("failed check #" + checkCounter + ": expected " + v2 + " got " + v1);
        }
    }

    static void check(boolean cond) {
        checkCounter++;
        if (!cond) {
            allOk = false;
        }
    }

    static boolean equal(double a, double b) {
        return a == b || ((Double.isNaN(a) && Double.isNaN(b)) || Math.abs((a - b) / b) < 1.0E-15d || Math.abs(a - b) < 1.0E-15d);
    }

    static boolean equal(double a, Complex c) {
        return equal(a, c.re) && (equal(0.0d, c.im) || (Double.isNaN(a) && Double.isNaN(c.im)));
    }

    static boolean equal(Complex a, Complex b) {
        return equal(a.re, b.re) && equal(a.im, b.im);
    }

    public static void main(String[] argv) throws SyntaxException, ArityException {
        int size = argv.length;
        Symbols symbols;
        int i;
        if (size == 0) {
            runUnitTests();
        } else if (!argv[0].equals("-profile")) {
            symbols = new Symbols();
            for (i = 0; i < size; i++) {
                FunctionAndName fan = symbols.compileWithName(argv[i]);
                symbols.define(fan);
                System.out.println(argv[i] + " : " + fan.function);
            }
        } else if (size == 1) {
            profile();
        } else {
            symbols = new Symbols();
            for (i = 1; i < size - 1; i++) {
                symbols.define(symbols.compileWithName(argv[i]));
            }
            profile(symbols, argv[size - 1]);
        }
    }

    private static void profile() {
        String[] cases = profileCases;
        Symbols symbols = new Symbols();
        int i = 0;
        while (i < cases.length) {
            try {
                symbols.define(symbols.compileWithName(cases[i]));
                profile(symbols, cases[i]);
                i++;
            } catch (SyntaxException e) {
                throw new Error("" + e);
            }
        }
    }

    static void profile(Symbols symbols, String str) throws SyntaxException, ArityException {
        int i;
        Function f = symbols.compile(str);
        System.out.println("\n" + str + ": " + f);
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        runtime.gc();
        long t1 = System.currentTimeMillis();
        for (i = 0; i < 1000; i++) {
            symbols.compile(str);
        }
        System.out.println("compilation time: " + (System.currentTimeMillis() - t1) + " us");
        double[] args = new double[f.arity()];
        runtime.gc();
        t1 = System.currentTimeMillis();
        for (i = 0; i < 100000; i++) {
            f.eval(args);
        }
        long delta = System.currentTimeMillis() - t1;
        System.out.println("execution time: " + (delta > 100 ? "" + (((double) delta) / 100.0d) + " us" : "" + delta + " ns"));
    }

    static void runUnitTests() {
        checkCounter = 0;
        check(Util.doubleToString(Double.NEGATIVE_INFINITY, 5).equals("-Infinity"));
        check(Util.doubleToString(Double.NaN, 5).equals("NaN"));
        Complex c = new Complex();
        Complex d = new Complex();
        Complex e = new Complex();
        check(Util.complexToString(c.set(0.0d, -1.0d), 10, 1).equals("-i"));
        check(Util.complexToString(c.set(2.123d, 0.0d), 3, 0).equals("2.1"));
        check(Util.complexToString(c.set(0.0d, 1.0000000000001d), 20, 3).equals("i"));
        check(Util.complexToString(c.set(1.0d, -1.0d), 10, 1).equals("1-i"));
        check(Util.complexToString(c.set(1.0d, 1.0d), 10, 1).equals("1+i"));
        check(Util.complexToString(c.set(1.12d, 1.12d), 9, 0).equals("1.12+1.1i"));
        check(Util.complexToString(c.set(1.12345d, -1.0d), 7, 0).equals("1.123-i"));
        check(c.set(-1.0d, 0.0d).pow(d.set(0.0d, 1.0d)), e.set(0.04321391826377226d, 0.0d));
        check(c.set(-1.0d, 0.0d).pow(d.set(1.0d, 1.0d)), e.set(-0.04321391826377226d, 0.0d));
        check(c.set(-1.0d, 0.0d).abs(), 1.0d);
        check(c.set(7.3890560989306495d, 0.0d).log(), d.set(2.0d, 0.0d));
        check(c.set(-1.0d, 0.0d).log(), d.set(0.0d, 3.141592653589793d));
        check(c.set(2.0d, 0.0d).exp(), d.set(7.3890560989306495d, 0.0d));
        check(c.set(0.0d, 3.141592653589793d).exp(), d.set(-1.0d, 0.0d));
        check(MoreMath.lgamma(1.0d), 0.0d);
        check(c.set(1.0d, 0.0d).lgamma(), d.set(0.0d, 0.0d));
        check(c.set(0.0d, 0.0d).factorial(), d.set(1.0d, 0.0d));
        check(c.set(1.0d, 0.0d).factorial(), d.set(1.0d, 0.0d));
        check(c.set(0.0d, 1.0d).factorial(), d.set(0.49801566811835596d, -0.1549498283018106d));
        check(c.set(-2.0d, 1.0d).factorial(), d.set(-0.17153291990834815d, 0.32648274821006623d));
        check(c.set(4.0d, 0.0d).factorial(), d.set(24.0d, 0.0d));
        check(c.set(4.0d, 3.0d).factorial(), d.set(0.016041882741649555d, -9.433293289755953d));
        check(Math.log(-1.0d), Double.NaN);
        check(Math.log(-0.03d), Double.NaN);
        check((double) MoreMath.intLog10(-0.03d), 0.0d);
        check((double) MoreMath.intLog10(0.03d), -2.0d);
        check(MoreMath.intExp10(3), 1000.0d);
        check(MoreMath.intExp10(-1), 0.1d);
        check(Util.shortApprox(1.235d, 0.02d), 1.24d);
        check(Util.shortApprox(1.235d, 0.4d), 1.2000000000000002d);
        check(Util.shortApprox(-1.235d, 0.02d), -1.24d);
        check(Util.shortApprox(-1.235d, 0.4d), -1.2000000000000002d);
        check(TestFormat.testFormat());
        check(TestEval.testEval());
        check(testRecursiveEval());
        check(testFrame());
        check(TestFormat.testSizeCases());
        if (allOk) {
            System.out.println("\n*** All tests passed OK ***\n");
            return;
        }
        System.out.println("\n*** Some tests FAILED ***\n");
        System.exit(1);
    }

    static boolean testFrame() {
        try {
            boolean ok;
            Symbols symbols = new Symbols();
            symbols.define("a", 1.0d);
            if (true && symbols.eval("a") == 1.0d) {
                ok = true;
            } else {
                ok = false;
            }
            symbols.pushFrame();
            if (ok && symbols.eval("a") == 1.0d) {
                ok = true;
            } else {
                ok = false;
            }
            symbols.define("a", 2.0d);
            if (ok && symbols.eval("a") == 2.0d) {
                ok = true;
            } else {
                ok = false;
            }
            symbols.define("a", 3.0d);
            if (ok && symbols.eval("a") == 3.0d) {
                ok = true;
            } else {
                ok = false;
            }
            symbols.popFrame();
            if (ok && symbols.eval("a") == 1.0d) {
                ok = true;
            } else {
                ok = false;
            }
            symbols = new Symbols();
            symbols.pushFrame();
            symbols.add(Symbol.makeArg("base", 0));
            symbols.add(Symbol.makeArg("x", 1));
            if (ok && symbols.lookupConst("x").op == (byte) 39) {
                ok = true;
            } else {
                ok = false;
            }
            symbols.pushFrame();
            if (ok && symbols.lookupConst("base").op == (byte) 38) {
                ok = true;
            } else {
                ok = false;
            }
            if (ok && symbols.lookupConst("x").op == (byte) 39) {
                ok = true;
            } else {
                ok = false;
            }
            symbols.popFrame();
            if (ok && symbols.lookupConst("base").op == (byte) 38) {
                ok = true;
            } else {
                ok = false;
            }
            if (ok && symbols.lookupConst("x").op == (byte) 39) {
                ok = true;
            } else {
                ok = false;
            }
            symbols.popFrame();
            if (ok && symbols.lookupConst("x").op == (byte) 38) {
                return true;
            }
            return false;
        } catch (SyntaxException e) {
            return false;
        }
    }

    static boolean testRecursiveEval() {
        Symbols symbols = new Symbols();
        symbols.define("myfun", new MyFun());
        try {
            Function f = symbols.compile("1+myfun(x)");
            if (f.eval(0.0d) == 2.0d && f.eval(1.0d) == 1.0d && f.eval(2.0d) == 0.0d && f.eval(3.0d) == -1.0d) {
                return true;
            }
            return false;
        } catch (SyntaxException e) {
            System.out.println("" + e);
            allOk = false;
            return false;
        }
    }
}
