package org.javia.arity;

/* compiled from: EvalCase */
class TestEval {
    private static final double ONE_SQRT2 = 0.7071067811865475d;
    static EvalCase[] cases = new EvalCase[]{new EvalCase(".", 0.0d), new EvalCase("1+.", 1.0d), new EvalCase("1", 1.0d), new EvalCase("π", 3.141592653589793d), new EvalCase("2×3", 6.0d), new EvalCase("1+√9*2", 7.0d), new EvalCase("3√ 4", 6.0d), new EvalCase("√16sin(2π/4)", 4.0d), new EvalCase("1+", -2.0d), new EvalCase("1+1", 2.0d), new EvalCase("1+-1", 0.0d), new EvalCase("-0.5", -0.5d), new EvalCase("+1e2", 100.0d), new EvalCase("1e-1", 0.1d), new EvalCase("1e−2", 0.01d), new EvalCase("-2^3!", -64.0d), new EvalCase("(-2)^3!", 64.0d), new EvalCase("-2^1^2", -2.0d), new EvalCase("--1", 1.0d), new EvalCase("-3^--2", -9.0d), new EvalCase("1+2)(2+3", 15.0d), new EvalCase("1+2)!^-2", 0.027777777777777776d), new EvalCase("sin(0)", 0.0d), new EvalCase("cos(0)", 1.0d), new EvalCase("sin(-1--1)", 0.0d), new EvalCase("-(2+1)*-(4/2)", 6.0d), new EvalCase("-.5E-1", -0.05d), new EvalCase("1E1.5", -2.0d), new EvalCase("2 3 4", 24.0d), new EvalCase("pi", 3.141592653589793d), new EvalCase("e", 2.718281828459045d), new EvalCase("sin(pi/2)", 1.0d), new EvalCase("f=sin(2x)", -3.0d), new EvalCase("f(pi/2)", 0.0d), new EvalCase("a=3", 3.0d), new EvalCase("b=a+1", 4.0d), new EvalCase("f(x, y) = x*(y+1)", -3.0d), new EvalCase("=", -2.0d), new EvalCase("f(a, b-a)", 6.0d), new EvalCase(" f(a pi/4)", -1.0d), new EvalCase("f (  1  +  1  , a+1)", 10.0d), new EvalCase("g(foo) = f (f(foo, 1)pi/2)", -3.0d), new EvalCase("g(.5*2)", 0.0d), new EvalCase("NaN", Double.NaN), new EvalCase("Inf", Double.POSITIVE_INFINITY), new EvalCase("Infinity", Double.POSITIVE_INFINITY), new EvalCase("-Inf", Double.NEGATIVE_INFINITY), new EvalCase("0/0", Double.NaN), new EvalCase("comb(11, 9)", 55.0d), new EvalCase("perm(11, 2)", 110.0d), new EvalCase("comb(1000, 999)", 1000.0d), new EvalCase("perm(1000, 1)", 1000.0d), new EvalCase("c(x)=1+x^2", -3.0d), new EvalCase("c(3-1)", 5.0d), new EvalCase("abs(3-4i)", 5.0d), new EvalCase("exp(pi*i)", -1.0d), new EvalCase("5%", 0.05d), new EvalCase("200+5%", 210.0d), new EvalCase("200-5%", 190.0d), new EvalCase("100/200%", 50.0d), new EvalCase("100+200%+5%", 315.0d), new EvalCase("p1(x)=200+5%+x", -3.0d), new EvalCase("p1(0)", 210.0d), new EvalCase("p2(x,y)=x+y%+(2*y)%", -3.0d), new EvalCase("p2(200,5)", 231.0d), new EvalCase("mod(5,3)", 2.0d), new EvalCase("5.2 # 3.2", 2.0d), new EvalCase("f(x)=3", -3.0d), new EvalCase("g(x)=f(x)", -3.0d), new EvalCase("g(1)", 3.0d), new EvalCase("a(x)=i+x-x", -3.0d), new EvalCase("b(x)=a(x)*a(x)", -3.0d), new EvalCase("b(5)", -1.0d), new EvalCase("h(x)=sqrt(-1+x-x)", -3.0d), new EvalCase("k(x)=h(x)*h(x)", -3.0d), new EvalCase("k(5)", -1.0d), new EvalCase("pi=4", 4.0d), new EvalCase("pi", 3.141592653589793d), new EvalCase("fc(x)=e^(i*x^2", -3.0d), new EvalCase("fc(0)", 1.0d), new EvalCase("aa(x)=sin(x)^1+sin(x)^0", -3.0d), new EvalCase("aa(0)", 1.0d), new EvalCase("null(x)=0", -3.0d), new EvalCase("n(x)=null(sin(x))", -3.0d), new EvalCase("n(1)", 0.0d), new EvalCase("(2,", -2.0d), new EvalCase("100.1-100-.1", 0.0d), new EvalCase("1.1-1+(-.1)", 0.0d), new EvalCase("log(2,8)", 3.0d), new EvalCase("log(9,81)", 2.0d), new EvalCase("log(4,2)", 0.5d), new EvalCase("sin'(0)", 1.0d), new EvalCase("cos'(0)", 0.0d), new EvalCase("cos'(pi/2)", -1.0d), new EvalCase("f(x)=2*x^3+x^2+100", -3.0d), new EvalCase("f'(1)", 8.0d), new EvalCase("f'(2)", 28.0d), new EvalCase("abs'(2)", 1.0d), new EvalCase("abs'(-3)", -1.0d), new EvalCase("0x0", 0.0d), new EvalCase("0x100", 256.0d), new EvalCase("0X10", 16.0d), new EvalCase("0b10", 2.0d), new EvalCase("0o10", 8.0d), new EvalCase("0o8", -2.0d), new EvalCase("0xg", -2.0d), new EvalCase("0b20", -2.0d), new EvalCase("sin(0x1*pi/2)", 1.0d), new EvalCase("ln(e)", 1.0d), new EvalCase("log(10)", 1.0d), new EvalCase("log10(100)", 2.0d), new EvalCase("lg(.1)", -1.0d), new EvalCase("log2(2)", 1.0d), new EvalCase("lb(256)", 8.0d), new EvalCase("rnd()*0", 0.0d), new EvalCase("rnd(5)*0", 0.0d), new EvalCase("max(2,3)", 3.0d), new EvalCase("min(2,3)", 2.0d), new EvalCase("fm(x)=max(2, x)", -3.0d), new EvalCase("fm(6)", 6.0d), new EvalCase("fmin(x)=min(2, x)", -3.0d), new EvalCase("fmin(1)", 1.0d), new EvalCase("fmin(3)", 2.0d), new EvalCase("cbrt(8)", 2.0d), new EvalCase("cbrt(-8)", -2.0d), new EvalCase("s=sign(x)", -3.0d), new EvalCase("s(2)", 1.0d), new EvalCase("s(-2)", -1.0d), new EvalCase("s(0)", 0.0d), new EvalCase("s(nan)", Double.NaN), new EvalCase("real(8.123)", 8.123d), new EvalCase("imag(8.123)", 0.0d), new EvalCase("im(sqrt(-1))", 1.0d), new EvalCase("im(nan)", Double.NaN)};
    static EvalCase[] casesComplex = new EvalCase[]{new EvalCase("sqrt(-1)^2", new Complex(-1.0d, 0.0d)), new EvalCase("i", new Complex(0.0d, 1.0d)), new EvalCase("sqrt(-1)", new Complex(0.0d, 1.0d)), new EvalCase("c(2+0i)", new Complex(5.0d, 0.0d)), new EvalCase("c(1+i)", new Complex(1.0d, 2.0d)), new EvalCase("ln(-1)", new Complex(0.0d, -3.141592653589793d)), new EvalCase("i^i", new Complex(0.20787957635076193d, 0.0d)), new EvalCase("gcd(135-14i, 155+34i)", new Complex(12.0d, -5.0d)), new EvalCase("comb(1+.5i, 1)", new Complex(1.0d, 0.5d)), new EvalCase("perm(2+i, 2)", new Complex(1.0d, 3.0d)), new EvalCase("fc(2)", new Complex(-0.6536436208636119d, -0.7568024953079282d)), new EvalCase("sign(2i)", new Complex(0.0d, 1.0d)), new EvalCase("sign(-i)", new Complex(0.0d, -1.0d)), new EvalCase("sign(nan)", new Complex(Double.NaN, 0.0d)), new EvalCase("sign(nan i)", new Complex(Double.NaN, 0.0d)), new EvalCase("sign(0)", new Complex(0.0d, 0.0d)), new EvalCase("sign(2-2i)", new Complex(ONE_SQRT2, -0.7071067811865475d)), new EvalCase("real(8.123)", new Complex(8.123d, 0.0d)), new EvalCase("imag(8.123)", new Complex(0.0d, 0.0d)), new EvalCase("real(1+3i)", new Complex(1.0d, 0.0d)), new EvalCase("imag(1+3i)", new Complex(3.0d, 0.0d)), new EvalCase("re(1+3i)", new Complex(1.0d, 0.0d)), new EvalCase("im(1+3i)", new Complex(3.0d, 0.0d))};

    TestEval() {
    }

    static boolean testEval() throws ArityException {
        String spaces = "                                           ";
        boolean allOk = true;
        Symbols symbols = new Symbols();
        for (EvalCase c : cases) {
            String strResult;
            boolean ok = true;
            double actual2 = 0.0d;
            try {
                double actual;
                Complex complex2 = new Complex();
                FunctionAndName fan = symbols.compileWithName(c.expr);
                Function f = fan.function;
                symbols.define(fan);
                if (f.arity() == 0) {
                    actual = f.eval();
                    Complex complex = f.evalComplex();
                    ok = 1 != null && UnitTest.equal(actual, complex);
                    strResult = Util.doubleToString(actual, 1);
                    if (!Symbols.isDefinition(c.expr)) {
                        actual2 = symbols.eval(c.expr);
                        complex2 = symbols.evalComplex(c.expr);
                        ok = ok && UnitTest.equal(actual, actual2) && complex.equals(complex2);
                    }
                    if (!ok) {
                        System.out.println("**** failed: " + actual + ' ' + actual2 + ' ' + complex + ' ' + complex2);
                    }
                } else {
                    actual = -3.0d;
                    strResult = f.toString();
                }
                ok = ok && UnitTest.equal(c.result, actual);
            } catch (SyntaxException e) {
                strResult = e.toString();
                ok = c.result == -2.0d;
            }
            System.out.println((ok ? "" : "failed (expected " + c.result + "): ") + c.expr + "                                           ".substring(0, Math.max(15 - c.expr.length(), 0)) + " = " + strResult);
            if (!ok) {
                allOk = false;
            }
        }
        for (EvalCase c2 : casesComplex) {
            try {
                Complex result = symbols.evalComplex(c2.expr);
                if (UnitTest.equal(c2.cResult, result)) {
                    System.out.println("" + c2.expr + " = " + Util.complexToString(result, 40, 0));
                } else {
                    System.out.println("failed " + c2.expr + " expected " + c2.cResult + " got " + result);
                    allOk = false;
                }
            } catch (SyntaxException e2) {
            }
        }
        return allOk;
    }
}
