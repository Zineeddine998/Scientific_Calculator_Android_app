package org.javia.arity;

import java.util.Random;

public class CompiledFunction extends ContextFunction {
    private static final double[] EMPTY_DOUBLE = new double[0];
    private static final Function[] EMPTY_FUN = new Function[0];
    private static final IsComplexException IS_COMPLEX = new IsComplexException();
    private static final Complex ONE_THIRD = new Complex(0.3333333333333333d, 0.0d);
    private static final Random random = new Random();
    private final int arity;
    private final byte[] code;
    private final double[] constsIm;
    private final double[] constsRe;
    private final Function[] funcs;

    CompiledFunction(int arity, byte[] code, double[] constsRe, double[] constsIm, Function[] funcs) {
        this.arity = arity;
        this.code = code;
        this.constsRe = constsRe;
        this.constsIm = constsIm;
        this.funcs = funcs;
    }

    private double evalComplexToReal(double[] args, EvalContext context) {
        return eval(toComplex(args, context), context).asReal();
    }

    private int execComplex(EvalContext context, int p) {
        int expected = p + 1;
        p = execWithoutCheckComplex(context, p, -2);
        if (p != expected) {
            throw new Error("Stack pointer after exec: expected " + expected + ", got " + p);
        }
        context.stackComplex[p - this.arity].set(context.stackComplex[p]);
        return p - this.arity;
    }

    private int execReal(EvalContext context, int p) throws IsComplexException {
        int expected = p + 1;
        p = execWithoutCheck(context, p);
        if (p != expected) {
            throw new Error("Stack pointer after exec: expected " + expected + ", got " + p);
        }
        context.stackRe[p - this.arity] = context.stackRe[p];
        return p - this.arity;
    }

    static Function makeOpFunction(int op) {
        if (VM.arity[op] != (byte) 1) {
            throw new Error("makeOpFunction expects arity 1, found " + VM.arity[op]);
        }
        CompiledFunction fun = new CompiledFunction(VM.arity[op], new byte[]{(byte) 38, (byte) op}, EMPTY_DOUBLE, EMPTY_DOUBLE, EMPTY_FUN);
        if (op == 29) {
            fun.setDerivative(new Function() {
                public int arity() {
                    return 1;
                }

                public double eval(double x) {
                    if (x > 0.0d) {
                        return 1.0d;
                    }
                    return x < 0.0d ? -1.0d : 0.0d;
                }
            });
        }
        return fun;
    }

    public int arity() {
        return this.arity;
    }

    public double eval(double[] args, EvalContext context) {
        if (this.constsIm != null) {
            return evalComplexToReal(args, context);
        }
        checkArity(args.length);
        System.arraycopy(args, 0, context.stackRe, context.stackBase, args.length);
        try {
            execReal(context, (context.stackBase + args.length) - 1);
            return context.stackRe[context.stackBase];
        } catch (IsComplexException e) {
            return evalComplexToReal(args, context);
        }
    }

    public Complex eval(Complex[] args, EvalContext context) {
        checkArity(args.length);
        Complex[] stack = context.stackComplex;
        int base = context.stackBase;
        for (int i = 0; i < args.length; i++) {
            stack[i + base].set(args[i]);
        }
        execComplex(context, (args.length + base) - 1);
        return stack[base];
    }

    int execWithoutCheck(EvalContext context, int p) throws IsComplexException {
        if (this.constsIm != null) {
            throw IS_COMPLEX;
        }
        Object s = context.stackRe;
        int stackBase = p - this.arity;
        int codeLen = this.code.length;
        int percentPC = -2;
        int pc = 0;
        int funp = 0;
        int constp = 0;
        while (pc < codeLen) {
            int constp2;
            int funp2;
            int opcode = this.code[pc];
            double a;
            double res;
            double v;
            switch (opcode) {
                case 1:
                    p++;
                    constp2 = constp + 1;
                    s[p] = this.constsRe[constp];
                    funp2 = funp;
                    break;
                case 2:
                    funp2 = funp + 1;
                    Function f = this.funcs[funp];
                    if (f instanceof CompiledFunction) {
                        p = ((CompiledFunction) f).execReal(context, p);
                        constp2 = constp;
                        break;
                    }
                    int arity = f.arity();
                    p -= arity;
                    int prevBase = context.stackBase;
                    try {
                        double result;
                        context.stackBase = p + 1;
                        switch (arity) {
                            case 0:
                                result = f.eval();
                                break;
                            case 1:
                                result = f.eval(s[p + 1]);
                                break;
                            case 2:
                                result = f.eval(s[p + 1], s[p + 2]);
                                break;
                            default:
                                double[] args = new double[arity];
                                System.arraycopy(s, p + 1, args, 0, arity);
                                result = f.eval(args);
                                break;
                        }
                        context.stackBase = prevBase;
                        p++;
                        s[p] = result;
                        constp2 = constp;
                        break;
                    } catch (Throwable th) {
                        context.stackBase = prevBase;
                    }
                case 3:
                    p--;
                    a = s[p];
                    res = a + (percentPC == pc + -1 ? s[p] * s[p + 1] : s[p + 1]);
                    if (Math.abs(res) < Math.ulp(a) * 1024.0d) {
                        res = 0.0d;
                    }
                    s[p] = res;
                    funp2 = funp;
                    constp2 = constp;
                    break;
                case 4:
                    p--;
                    a = s[p];
                    res = a - (percentPC == pc + -1 ? s[p] * s[p + 1] : s[p + 1]);
                    if (Math.abs(res) < Math.ulp(a) * 1024.0d) {
                        res = 0.0d;
                    }
                    s[p] = res;
                    funp2 = funp;
                    constp2 = constp;
                    break;
                case 5:
                    p--;
                    s[p] = s[p] * s[p + 1];
                    funp2 = funp;
                    constp2 = constp;
                    break;
                case 6:
                    p--;
                    s[p] = s[p] / s[p + 1];
                    funp2 = funp;
                    constp2 = constp;
                    break;
                case 7:
                    p--;
                    s[p] = s[p] % s[p + 1];
                    funp2 = funp;
                    constp2 = constp;
                    break;
                case 8:
                    p++;
                    s[p] = random.nextDouble();
                    funp2 = funp;
                    constp2 = constp;
                    break;
                case 9:
                    s[p] = -s[p];
                    funp2 = funp;
                    constp2 = constp;
                    break;
                case 10:
                    p--;
                    s[p] = Math.pow(s[p], s[p + 1]);
                    funp2 = funp;
                    constp2 = constp;
                    break;
                case 11:
                    s[p] = MoreMath.factorial(s[p]);
                    funp2 = funp;
                    constp2 = constp;
                    break;
                case 12:
                    s[p] = s[p] * 0.01d;
                    percentPC = pc;
                    funp2 = funp;
                    constp2 = constp;
                    break;
                case 13:
                    v = s[p];
                    if (v >= 0.0d) {
                        s[p] = Math.sqrt(v);
                        funp2 = funp;
                        constp2 = constp;
                        break;
                    }
                    throw IS_COMPLEX;
                case 14:
                    s[p] = Math.cbrt(s[p]);
                    funp2 = funp;
                    constp2 = constp;
                    break;
                case 15:
                    s[p] = Math.exp(s[p]);
                    funp2 = funp;
                    constp2 = constp;
                    break;
                case 16:
                    s[p] = Math.log(s[p]);
                    funp2 = funp;
                    constp2 = constp;
                    break;
                case 17:
                    s[p] = MoreMath.sin(s[p]);
                    funp2 = funp;
                    constp2 = constp;
                    break;
                case 18:
                    s[p] = MoreMath.cos(s[p]);
                    funp2 = funp;
                    constp2 = constp;
                    break;
                case 19:
                    s[p] = MoreMath.tan(s[p]);
                    funp2 = funp;
                    constp2 = constp;
                    break;
                case 20:
                    v = s[p];
                    if (v >= -1.0d && v <= 1.0d) {
                        s[p] = Math.asin(v);
                        funp2 = funp;
                        constp2 = constp;
                        break;
                    }
                    throw IS_COMPLEX;
                case 21:
                    v = s[p];
                    if (v >= -1.0d && v <= 1.0d) {
                        s[p] = Math.acos(v);
                        funp2 = funp;
                        constp2 = constp;
                        break;
                    }
                    throw IS_COMPLEX;
                case 22:
                    s[p] = Math.atan(s[p]);
                    funp2 = funp;
                    constp2 = constp;
                    break;
                case 23:
                    s[p] = Math.sinh(s[p]);
                    funp2 = funp;
                    constp2 = constp;
                    break;
                case 24:
                    s[p] = Math.cosh(s[p]);
                    funp2 = funp;
                    constp2 = constp;
                    break;
                case 25:
                    s[p] = Math.tanh(s[p]);
                    funp2 = funp;
                    constp2 = constp;
                    break;
                case 26:
                    s[p] = MoreMath.asinh(s[p]);
                    funp2 = funp;
                    constp2 = constp;
                    break;
                case 27:
                    s[p] = MoreMath.acosh(s[p]);
                    funp2 = funp;
                    constp2 = constp;
                    break;
                case 28:
                    s[p] = MoreMath.atanh(s[p]);
                    funp2 = funp;
                    constp2 = constp;
                    break;
                case 29:
                    s[p] = Math.abs(s[p]);
                    funp2 = funp;
                    constp2 = constp;
                    break;
                case 30:
                    s[p] = Math.floor(s[p]);
                    funp2 = funp;
                    constp2 = constp;
                    break;
                case 31:
                    s[p] = Math.ceil(s[p]);
                    funp2 = funp;
                    constp2 = constp;
                    break;
                case 32:
                    v = s[p];
                    long j = v > 0.0d ? 4607182418800017408L : v < 0.0d ? -4616189618054758400L : v == 0.0d ? 0 : 9221120237041090560L;
                    s[p] = j;
                    funp2 = funp;
                    constp2 = constp;
                    break;
                case 33:
                    p--;
                    s[p] = Math.min(s[p], s[p + 1]);
                    funp2 = funp;
                    constp2 = constp;
                    break;
                case 34:
                    p--;
                    s[p] = Math.max(s[p], s[p + 1]);
                    funp2 = funp;
                    constp2 = constp;
                    break;
                case 35:
                    p--;
                    s[p] = MoreMath.gcd(s[p], s[p + 1]);
                    funp2 = funp;
                    constp2 = constp;
                    break;
                case 36:
                    p--;
                    s[p] = MoreMath.combinations(s[p], s[p + 1]);
                    funp2 = funp;
                    constp2 = constp;
                    break;
                case 37:
                    p--;
                    s[p] = MoreMath.permutations(s[p], s[p + 1]);
                    funp2 = funp;
                    constp2 = constp;
                    break;
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                    p++;
                    s[p] = s[(stackBase + opcode) - 37];
                    funp2 = funp;
                    constp2 = constp;
                    break;
                case 43:
                    funp2 = funp;
                    constp2 = constp;
                    break;
                case 44:
                    if (!Double.isNaN(s[p])) {
                        s[p] = 0.0d;
                        funp2 = funp;
                        constp2 = constp;
                        break;
                    }
                    funp2 = funp;
                    constp2 = constp;
                    break;
                default:
                    throw new Error("Unknown opcode " + opcode);
            }
            pc++;
            funp = funp2;
            constp = constp2;
        }
        return p;
    }

    /* JADX WARNING: Missing block: B:111:0x0409, code:
            r17 = r18;
     */
    int execWithoutCheckComplex(org.javia.arity.EvalContext r31, int r32, int r33) {
        /*
        r30 = this;
        r0 = r31;
        r0 = r0.stackComplex;
        r23 = r0;
        r0 = r30;
        r0 = r0.arity;
        r25 = r0;
        r24 = r32 - r25;
        r15 = 0;
        r17 = 0;
        r0 = r30;
        r0 = r0.code;
        r25 = r0;
        r0 = r25;
        r14 = r0.length;
        r20 = 0;
        r18 = r17;
    L_0x001e:
        r0 = r20;
        if (r0 >= r14) goto L_0x0408;
    L_0x0022:
        r0 = r30;
        r0 = r0.code;
        r25 = r0;
        r19 = r25[r20];
        switch(r19) {
            case 1: goto L_0x004a;
            case 2: goto L_0x007b;
            case 3: goto L_0x0125;
            case 4: goto L_0x014f;
            case 5: goto L_0x0179;
            case 6: goto L_0x0188;
            case 7: goto L_0x0197;
            case 8: goto L_0x0112;
            case 9: goto L_0x01b5;
            case 10: goto L_0x01a6;
            case 11: goto L_0x01be;
            case 12: goto L_0x01c7;
            case 13: goto L_0x0255;
            case 14: goto L_0x025e;
            case 15: goto L_0x0243;
            case 16: goto L_0x024c;
            case 17: goto L_0x01d7;
            case 18: goto L_0x01e0;
            case 19: goto L_0x01e9;
            case 20: goto L_0x020d;
            case 21: goto L_0x0216;
            case 22: goto L_0x021f;
            case 23: goto L_0x01f2;
            case 24: goto L_0x01fb;
            case 25: goto L_0x0204;
            case 26: goto L_0x0228;
            case 27: goto L_0x0231;
            case 28: goto L_0x023a;
            case 29: goto L_0x028f;
            case 30: goto L_0x02a0;
            case 31: goto L_0x02b7;
            case 32: goto L_0x02ce;
            case 33: goto L_0x0330;
            case 34: goto L_0x035f;
            case 35: goto L_0x038e;
            case 36: goto L_0x039d;
            case 37: goto L_0x03ac;
            case 38: goto L_0x03bb;
            case 39: goto L_0x03bb;
            case 40: goto L_0x03bb;
            case 41: goto L_0x03bb;
            case 42: goto L_0x03bb;
            case 43: goto L_0x03cc;
            case 44: goto L_0x03ea;
            default: goto L_0x002d;
        };
    L_0x002d:
        r25 = new java.lang.Error;
        r26 = new java.lang.StringBuilder;
        r26.<init>();
        r27 = "Unknown opcode ";
        r26 = r26.append(r27);
        r0 = r26;
        r1 = r19;
        r26 = r0.append(r1);
        r26 = r26.toString();
        r25.<init>(r26);
        throw r25;
    L_0x004a:
        r32 = r32 + 1;
        r25 = r23[r32];
        r0 = r30;
        r0 = r0.constsRe;
        r26 = r0;
        r28 = r26[r15];
        r0 = r30;
        r0 = r0.constsIm;
        r26 = r0;
        if (r26 != 0) goto L_0x0072;
    L_0x005e:
        r26 = 0;
    L_0x0060:
        r0 = r25;
        r1 = r28;
        r3 = r26;
        r0.set(r1, r3);
        r15 = r15 + 1;
        r17 = r18;
    L_0x006d:
        r20 = r20 + 1;
        r18 = r17;
        goto L_0x001e;
    L_0x0072:
        r0 = r30;
        r0 = r0.constsIm;
        r26 = r0;
        r26 = r26[r15];
        goto L_0x0060;
    L_0x007b:
        r0 = r30;
        r0 = r0.funcs;
        r25 = r0;
        r17 = r18 + 1;
        r16 = r25[r18];
        r0 = r16;
        r0 = r0 instanceof org.javia.arity.CompiledFunction;
        r25 = r0;
        if (r25 == 0) goto L_0x009a;
    L_0x008d:
        r16 = (org.javia.arity.CompiledFunction) r16;
        r0 = r16;
        r1 = r31;
        r2 = r32;
        r32 = r0.execComplex(r1, r2);
        goto L_0x006d;
    L_0x009a:
        r11 = r16.arity();
        r32 = r32 - r11;
        r0 = r31;
        r0 = r0.stackBase;
        r21 = r0;
        r25 = r32 + 1;
        r0 = r25;
        r1 = r31;
        r1.stackBase = r0;	 Catch:{ all -> 0x010a }
        switch(r11) {
            case 0: goto L_0x00d8;
            case 1: goto L_0x00ea;
            case 2: goto L_0x00f7;
            default: goto L_0x00b1;
        };	 Catch:{ all -> 0x010a }
    L_0x00b1:
        r10 = new org.javia.arity.Complex[r11];	 Catch:{ all -> 0x010a }
        r25 = r32 + 1;
        r26 = 0;
        r0 = r23;
        r1 = r25;
        r2 = r26;
        java.lang.System.arraycopy(r0, r1, r10, r2, r11);	 Catch:{ all -> 0x010a }
        r0 = r16;
        r22 = r0.eval(r10);	 Catch:{ all -> 0x010a }
    L_0x00c6:
        r0 = r21;
        r1 = r31;
        r1.stackBase = r0;
        r32 = r32 + 1;
        r25 = r23[r32];
        r0 = r25;
        r1 = r22;
        r0.set(r1);
        goto L_0x006d;
    L_0x00d8:
        r22 = new org.javia.arity.Complex;	 Catch:{ all -> 0x010a }
        r26 = r16.eval();	 Catch:{ all -> 0x010a }
        r28 = 0;
        r0 = r22;
        r1 = r26;
        r3 = r28;
        r0.<init>(r1, r3);	 Catch:{ all -> 0x010a }
        goto L_0x00c6;
    L_0x00ea:
        r25 = r32 + 1;
        r25 = r23[r25];	 Catch:{ all -> 0x010a }
        r0 = r16;
        r1 = r25;
        r22 = r0.eval(r1);	 Catch:{ all -> 0x010a }
        goto L_0x00c6;
    L_0x00f7:
        r25 = r32 + 1;
        r25 = r23[r25];	 Catch:{ all -> 0x010a }
        r26 = r32 + 2;
        r26 = r23[r26];	 Catch:{ all -> 0x010a }
        r0 = r16;
        r1 = r25;
        r2 = r26;
        r22 = r0.eval(r1, r2);	 Catch:{ all -> 0x010a }
        goto L_0x00c6;
    L_0x010a:
        r25 = move-exception;
        r0 = r21;
        r1 = r31;
        r1.stackBase = r0;
        throw r25;
    L_0x0112:
        r32 = r32 + 1;
        r25 = r23[r32];
        r26 = random;
        r26 = r26.nextDouble();
        r28 = 0;
        r25.set(r26, r28);
        r17 = r18;
        goto L_0x006d;
    L_0x0125:
        r32 = r32 + -1;
        r26 = r23[r32];
        r25 = r20 + -1;
        r0 = r33;
        r1 = r25;
        if (r0 != r1) goto L_0x014a;
    L_0x0131:
        r25 = r32 + 1;
        r25 = r23[r25];
        r27 = r23[r32];
        r0 = r25;
        r1 = r27;
        r25 = r0.mul(r1);
    L_0x013f:
        r0 = r26;
        r1 = r25;
        r0.add(r1);
        r17 = r18;
        goto L_0x006d;
    L_0x014a:
        r25 = r32 + 1;
        r25 = r23[r25];
        goto L_0x013f;
    L_0x014f:
        r32 = r32 + -1;
        r26 = r23[r32];
        r25 = r20 + -1;
        r0 = r33;
        r1 = r25;
        if (r0 != r1) goto L_0x0174;
    L_0x015b:
        r25 = r32 + 1;
        r25 = r23[r25];
        r27 = r23[r32];
        r0 = r25;
        r1 = r27;
        r25 = r0.mul(r1);
    L_0x0169:
        r0 = r26;
        r1 = r25;
        r0.sub(r1);
        r17 = r18;
        goto L_0x006d;
    L_0x0174:
        r25 = r32 + 1;
        r25 = r23[r25];
        goto L_0x0169;
    L_0x0179:
        r32 = r32 + -1;
        r25 = r23[r32];
        r26 = r32 + 1;
        r26 = r23[r26];
        r25.mul(r26);
        r17 = r18;
        goto L_0x006d;
    L_0x0188:
        r32 = r32 + -1;
        r25 = r23[r32];
        r26 = r32 + 1;
        r26 = r23[r26];
        r25.div(r26);
        r17 = r18;
        goto L_0x006d;
    L_0x0197:
        r32 = r32 + -1;
        r25 = r23[r32];
        r26 = r32 + 1;
        r26 = r23[r26];
        r25.mod(r26);
        r17 = r18;
        goto L_0x006d;
    L_0x01a6:
        r32 = r32 + -1;
        r25 = r23[r32];
        r26 = r32 + 1;
        r26 = r23[r26];
        r25.pow(r26);
        r17 = r18;
        goto L_0x006d;
    L_0x01b5:
        r25 = r23[r32];
        r25.negate();
        r17 = r18;
        goto L_0x006d;
    L_0x01be:
        r25 = r23[r32];
        r25.factorial();
        r17 = r18;
        goto L_0x006d;
    L_0x01c7:
        r25 = r23[r32];
        r26 = 4576918229304087675; // 0x3f847ae147ae147b float:89128.96 double:0.01;
        r25.mul(r26);
        r33 = r20;
        r17 = r18;
        goto L_0x006d;
    L_0x01d7:
        r25 = r23[r32];
        r25.sin();
        r17 = r18;
        goto L_0x006d;
    L_0x01e0:
        r25 = r23[r32];
        r25.cos();
        r17 = r18;
        goto L_0x006d;
    L_0x01e9:
        r25 = r23[r32];
        r25.tan();
        r17 = r18;
        goto L_0x006d;
    L_0x01f2:
        r25 = r23[r32];
        r25.sinh();
        r17 = r18;
        goto L_0x006d;
    L_0x01fb:
        r25 = r23[r32];
        r25.cosh();
        r17 = r18;
        goto L_0x006d;
    L_0x0204:
        r25 = r23[r32];
        r25.tanh();
        r17 = r18;
        goto L_0x006d;
    L_0x020d:
        r25 = r23[r32];
        r25.asin();
        r17 = r18;
        goto L_0x006d;
    L_0x0216:
        r25 = r23[r32];
        r25.acos();
        r17 = r18;
        goto L_0x006d;
    L_0x021f:
        r25 = r23[r32];
        r25.atan();
        r17 = r18;
        goto L_0x006d;
    L_0x0228:
        r25 = r23[r32];
        r25.asinh();
        r17 = r18;
        goto L_0x006d;
    L_0x0231:
        r25 = r23[r32];
        r25.acosh();
        r17 = r18;
        goto L_0x006d;
    L_0x023a:
        r25 = r23[r32];
        r25.atanh();
        r17 = r18;
        goto L_0x006d;
    L_0x0243:
        r25 = r23[r32];
        r25.exp();
        r17 = r18;
        goto L_0x006d;
    L_0x024c:
        r25 = r23[r32];
        r25.log();
        r17 = r18;
        goto L_0x006d;
    L_0x0255:
        r25 = r23[r32];
        r25.sqrt();
        r17 = r18;
        goto L_0x006d;
    L_0x025e:
        r25 = r23[r32];
        r0 = r25;
        r0 = r0.im;
        r26 = r0;
        r28 = 0;
        r25 = (r26 > r28 ? 1 : (r26 == r28 ? 0 : -1));
        if (r25 != 0) goto L_0x0284;
    L_0x026c:
        r25 = r23[r32];
        r26 = r23[r32];
        r0 = r26;
        r0 = r0.re;
        r26 = r0;
        r26 = java.lang.Math.cbrt(r26);
        r0 = r26;
        r2 = r25;
        r2.re = r0;
        r17 = r18;
        goto L_0x006d;
    L_0x0284:
        r25 = r23[r32];
        r26 = ONE_THIRD;
        r25.pow(r26);
        r17 = r18;
        goto L_0x006d;
    L_0x028f:
        r25 = r23[r32];
        r26 = r23[r32];
        r26 = r26.abs();
        r28 = 0;
        r25.set(r26, r28);
        r17 = r18;
        goto L_0x006d;
    L_0x02a0:
        r25 = r23[r32];
        r26 = r23[r32];
        r0 = r26;
        r0 = r0.re;
        r26 = r0;
        r26 = java.lang.Math.floor(r26);
        r28 = 0;
        r25.set(r26, r28);
        r17 = r18;
        goto L_0x006d;
    L_0x02b7:
        r25 = r23[r32];
        r26 = r23[r32];
        r0 = r26;
        r0 = r0.re;
        r26 = r0;
        r26 = java.lang.Math.ceil(r26);
        r28 = 0;
        r25.set(r26, r28);
        r17 = r18;
        goto L_0x006d;
    L_0x02ce:
        r25 = r23[r32];
        r0 = r25;
        r6 = r0.re;
        r25 = r23[r32];
        r0 = r25;
        r12 = r0.im;
        r26 = 0;
        r25 = (r12 > r26 ? 1 : (r12 == r26 ? 0 : -1));
        if (r25 != 0) goto L_0x0308;
    L_0x02e0:
        r25 = r23[r32];
        r26 = 0;
        r26 = (r6 > r26 ? 1 : (r6 == r26 ? 0 : -1));
        if (r26 <= 0) goto L_0x02f3;
    L_0x02e8:
        r26 = 4607182418800017408; // 0x3ff0000000000000 float:0.0 double:1.0;
    L_0x02ea:
        r28 = 0;
        r25.set(r26, r28);
        r17 = r18;
        goto L_0x006d;
    L_0x02f3:
        r26 = 0;
        r26 = (r6 > r26 ? 1 : (r6 == r26 ? 0 : -1));
        if (r26 >= 0) goto L_0x02fc;
    L_0x02f9:
        r26 = -4616189618054758400; // 0xbff0000000000000 float:0.0 double:-1.0;
        goto L_0x02ea;
    L_0x02fc:
        r26 = 0;
        r26 = (r6 > r26 ? 1 : (r6 == r26 ? 0 : -1));
        if (r26 != 0) goto L_0x0305;
    L_0x0302:
        r26 = 0;
        goto L_0x02ea;
    L_0x0305:
        r26 = 9221120237041090560; // 0x7ff8000000000000 float:0.0 double:NaN;
        goto L_0x02ea;
    L_0x0308:
        r25 = r23[r32];
        r25 = r25.isNaN();
        if (r25 != 0) goto L_0x0323;
    L_0x0310:
        r25 = r23[r32];
        r8 = r25.abs();
        r25 = r23[r32];
        r26 = r6 / r8;
        r28 = r12 / r8;
        r25.set(r26, r28);
        r17 = r18;
        goto L_0x006d;
    L_0x0323:
        r25 = r23[r32];
        r26 = 9221120237041090560; // 0x7ff8000000000000 float:0.0 double:NaN;
        r28 = 0;
        r25.set(r26, r28);
        r17 = r18;
        goto L_0x006d;
    L_0x0330:
        r32 = r32 + -1;
        r25 = r32 + 1;
        r25 = r23[r25];
        r0 = r25;
        r0 = r0.re;
        r26 = r0;
        r25 = r23[r32];
        r0 = r25;
        r0 = r0.re;
        r28 = r0;
        r25 = (r26 > r28 ? 1 : (r26 == r28 ? 0 : -1));
        if (r25 < 0) goto L_0x0352;
    L_0x0348:
        r25 = r32 + 1;
        r25 = r23[r25];
        r25 = r25.isNaN();
        if (r25 == 0) goto L_0x0409;
    L_0x0352:
        r25 = r23[r32];
        r26 = r32 + 1;
        r26 = r23[r26];
        r25.set(r26);
        r17 = r18;
        goto L_0x006d;
    L_0x035f:
        r32 = r32 + -1;
        r25 = r23[r32];
        r0 = r25;
        r0 = r0.re;
        r26 = r0;
        r25 = r32 + 1;
        r25 = r23[r25];
        r0 = r25;
        r0 = r0.re;
        r28 = r0;
        r25 = (r26 > r28 ? 1 : (r26 == r28 ? 0 : -1));
        if (r25 < 0) goto L_0x0381;
    L_0x0377:
        r25 = r32 + 1;
        r25 = r23[r25];
        r25 = r25.isNaN();
        if (r25 == 0) goto L_0x0409;
    L_0x0381:
        r25 = r23[r32];
        r26 = r32 + 1;
        r26 = r23[r26];
        r25.set(r26);
        r17 = r18;
        goto L_0x006d;
    L_0x038e:
        r32 = r32 + -1;
        r25 = r23[r32];
        r26 = r32 + 1;
        r26 = r23[r26];
        r25.gcd(r26);
        r17 = r18;
        goto L_0x006d;
    L_0x039d:
        r32 = r32 + -1;
        r25 = r23[r32];
        r26 = r32 + 1;
        r26 = r23[r26];
        r25.combinations(r26);
        r17 = r18;
        goto L_0x006d;
    L_0x03ac:
        r32 = r32 + -1;
        r25 = r23[r32];
        r26 = r32 + 1;
        r26 = r23[r26];
        r25.permutations(r26);
        r17 = r18;
        goto L_0x006d;
    L_0x03bb:
        r32 = r32 + 1;
        r25 = r23[r32];
        r26 = r24 + r19;
        r26 = r26 + -37;
        r26 = r23[r26];
        r25.set(r26);
        r17 = r18;
        goto L_0x006d;
    L_0x03cc:
        r25 = r23[r32];
        r26 = r23[r32];
        r26 = r26.isNaN();
        if (r26 == 0) goto L_0x03e1;
    L_0x03d6:
        r26 = 9221120237041090560; // 0x7ff8000000000000 float:0.0 double:NaN;
    L_0x03d8:
        r28 = 0;
        r25.set(r26, r28);
        r17 = r18;
        goto L_0x006d;
    L_0x03e1:
        r26 = r23[r32];
        r0 = r26;
        r0 = r0.re;
        r26 = r0;
        goto L_0x03d8;
    L_0x03ea:
        r25 = r23[r32];
        r26 = r23[r32];
        r26 = r26.isNaN();
        if (r26 == 0) goto L_0x03ff;
    L_0x03f4:
        r26 = 9221120237041090560; // 0x7ff8000000000000 float:0.0 double:NaN;
    L_0x03f6:
        r28 = 0;
        r25.set(r26, r28);
        r17 = r18;
        goto L_0x006d;
    L_0x03ff:
        r26 = r23[r32];
        r0 = r26;
        r0 = r0.im;
        r26 = r0;
        goto L_0x03f6;
    L_0x0408:
        return r32;
    L_0x0409:
        r17 = r18;
        goto L_0x006d;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.javia.arity.CompiledFunction.execWithoutCheckComplex(org.javia.arity.EvalContext, int, int):int");
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        int cpos = 0;
        int fpos = 0;
        if (this.arity != 0) {
            buf.append("arity ").append(this.arity).append("; ");
        }
        for (byte op : this.code) {
            buf.append(VM.opcodeName[op]);
            if (op == (byte) 1) {
                buf.append(' ');
                if (this.constsIm == null) {
                    buf.append(this.constsRe[cpos]);
                } else {
                    buf.append('(').append(this.constsRe[cpos]).append(", ").append(this.constsIm[cpos]).append(')');
                }
                cpos++;
            } else if (op == (byte) 2) {
                fpos++;
            }
            buf.append("; ");
        }
        if (cpos != this.constsRe.length) {
            buf.append("\nuses only ").append(cpos).append(" consts out of ").append(this.constsRe.length);
        }
        if (fpos != this.funcs.length) {
            buf.append("\nuses only ").append(fpos).append(" funcs out of ").append(this.funcs.length);
        }
        return buf.toString();
    }
}
