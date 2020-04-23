package org.javia.arity;

class VM {
    static final byte ABS = (byte) 29;
    static final byte ACOS = (byte) 21;
    static final byte ACOSH = (byte) 27;
    static final byte ADD = (byte) 3;
    static final byte ASIN = (byte) 20;
    static final byte ASINH = (byte) 26;
    static final byte ATAN = (byte) 22;
    static final byte ATANH = (byte) 28;
    static final byte CALL = (byte) 2;
    static final byte CBRT = (byte) 14;
    static final byte CEIL = (byte) 31;
    static final byte COMB = (byte) 36;
    static final byte CONST = (byte) 1;
    static final byte COS = (byte) 18;
    static final byte COSH = (byte) 24;
    static final byte DIV = (byte) 6;
    static final byte EXP = (byte) 15;
    static final byte FACT = (byte) 11;
    static final byte FLOOR = (byte) 30;
    static final byte GCD = (byte) 35;
    static final byte IMAG = (byte) 44;
    static final byte LN = (byte) 16;
    static final byte LOAD0 = (byte) 38;
    static final byte LOAD1 = (byte) 39;
    static final byte LOAD2 = (byte) 40;
    static final byte LOAD3 = (byte) 41;
    static final byte LOAD4 = (byte) 42;
    static final byte MAX = (byte) 34;
    static final byte MIN = (byte) 33;
    static final byte MOD = (byte) 7;
    static final byte MUL = (byte) 5;
    static final byte PERCENT = (byte) 12;
    static final byte PERM = (byte) 37;
    static final byte POWER = (byte) 10;
    static final byte REAL = (byte) 43;
    static final byte RESERVED = (byte) 0;
    static final byte RND = (byte) 8;
    static final byte SIGN = (byte) 32;
    static final byte SIN = (byte) 17;
    static final byte SINH = (byte) 23;
    static final byte SQRT = (byte) 13;
    static final byte SUB = (byte) 4;
    static final byte TAN = (byte) 19;
    static final byte TANH = (byte) 25;
    static final byte UMIN = (byte) 9;
    static final byte[] arity = new byte[]{RESERVED, RESERVED, (byte) -1, CALL, CALL, CALL, CALL, CALL, RESERVED, CONST, CALL, CONST, CONST, CONST, CONST, CONST, CONST, CONST, CONST, CONST, CONST, CONST, CONST, CONST, CONST, CONST, CONST, CONST, CONST, CONST, CONST, CONST, CONST, CALL, CALL, CALL, CALL, CALL, RESERVED, RESERVED, RESERVED, RESERVED, RESERVED, CONST, CONST};
    static final byte[] builtins = new byte[]{RND, SQRT, CBRT, SIN, COS, TAN, ASIN, ACOS, ATAN, SINH, COSH, TANH, ASINH, ACOSH, ATANH, EXP, LN, ABS, FLOOR, CEIL, SIGN, MIN, MAX, GCD, COMB, PERM, MOD, REAL, IMAG};
    static final String[] opcodeName = new String[]{"reserved", "const", "call", "add", "sub", "mul", "div", "mod", "rnd", "umin", "power", "fact", "percent", "sqrt", "cbrt", "exp", "ln", "sin", "cos", "tan", "asin", "acos", "atan", "sinh", "cosh", "tanh", "asinh", "acosh", "atanh", "abs", "floor", "ceil", "sign", "min", "max", "gcd", "comb", "perm", "load0", "load1", "load2", "load3", "load4", "real", "imag"};

    VM() {
    }
}
