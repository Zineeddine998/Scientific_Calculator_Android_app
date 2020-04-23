package org.javia.arity;

class MoreMath {
    static final double[] FACT = new double[]{1.0d, 40320.0d, 2.0922789888E13d, 6.204484017332394E23d, 2.631308369336935E35d, 8.159152832478977E47d, 1.2413915592536073E61d, 7.109985878048635E74d, 1.2688693218588417E89d, 6.1234458376886085E103d, 7.156945704626381E118d, 1.8548264225739844E134d, 9.916779348709496E149d, 1.0299016745145628E166d, 1.974506857221074E182d, 6.689502913449127E198d, 3.856204823625804E215d, 3.659042881952549E232d, 5.5502938327393044E249d, 1.3113358856834524E267d, 4.7147236359920616E284d, 2.5260757449731984E302d};
    static final double[] GAMMA = new double[]{57.15623566586292d, -59.59796035547549d, 14.136097974741746d, -0.4919138160976202d, 3.399464998481189E-5d, 4.652362892704858E-5d, -9.837447530487956E-5d, 1.580887032249125E-4d, -2.1026444172410488E-4d, 2.1743961811521265E-4d, -1.643181065367639E-4d, 8.441822398385275E-5d, -2.6190838401581408E-5d, 3.6899182659531625E-6d};
    private static final double LOG2E = 1.4426950408889634d;

    MoreMath() {
    }

    public static final double acosh(double x) {
        return Math.log((x + x) - (1.0d / (Math.sqrt((x * x) - 1.0d) + x)));
    }

    public static final double asinh(double x) {
        return x < 0.0d ? -asinh(-x) : Math.log((x + x) + (1.0d / (Math.sqrt((x * x) + 1.0d) + x)));
    }

    public static final double atanh(double x) {
        return x < 0.0d ? -atanh(-x) : 0.5d * Math.log(((x + x) / (1.0d - x)) + 1.0d);
    }

    public static final double combinations(double n, double k) {
        if (n < 0.0d || k < 0.0d) {
            return Double.NaN;
        }
        if (n < k) {
            return 0.0d;
        }
        if (Math.floor(n) != n || Math.floor(k) != k) {
            return Math.exp((lgamma(n) - lgamma(k)) - lgamma(n - k));
        }
        k = Math.min(k, n - k);
        if (n <= 170.0d && 12.0d < k && k <= 170.0d) {
            return (factorial(n) / factorial(k)) / factorial(n - k);
        }
        double r = 1.0d;
        double diff = n - k;
        for (double i = k; i > 0.5d && r < Double.POSITIVE_INFINITY; i -= 1.0d) {
            r *= (diff + i) / i;
        }
        return r;
    }

    public static final double cos(double x) {
        return isPiMultiple(x - 1.5707963267948966d) ? 0.0d : Math.cos(x);
    }

    /* JADX WARNING: Missing block: B:11:0x002e, code:
            r8 = r8 - 1.0d;
            r0 = r0 * r8;
     */
    /* JADX WARNING: Missing block: B:12:0x0030, code:
            r8 = r8 - 1.0d;
            r0 = r0 * r8;
     */
    /* JADX WARNING: Missing block: B:13:0x0032, code:
            r8 = r8 - 1.0d;
            r0 = r0 * r8;
     */
    /* JADX WARNING: Missing block: B:14:0x0034, code:
            r8 = r8 - 1.0d;
            r0 = r0 * r8;
     */
    /* JADX WARNING: Missing block: B:15:0x0036, code:
            r0 = r0 * (r8 - 1.0d);
     */
    /* JADX WARNING: Missing block: B:20:?, code:
            return FACT[r2 >> 3] * r0;
     */
    public static final double factorial(double r8) {
        /*
        r6 = 4607182418800017408; // 0x3ff0000000000000 float:0.0 double:1.0;
        r4 = 0;
        r3 = (r8 > r4 ? 1 : (r8 == r4 ? 0 : -1));
        if (r3 >= 0) goto L_0x000b;
    L_0x0008:
        r4 = 9221120237041090560; // 0x7ff8000000000000 float:0.0 double:NaN;
    L_0x000a:
        return r4;
    L_0x000b:
        r4 = 4640185359819341824; // 0x4065400000000000 float:0.0 double:170.0;
        r3 = (r8 > r4 ? 1 : (r8 == r4 ? 0 : -1));
        if (r3 > 0) goto L_0x0023;
    L_0x0014:
        r4 = java.lang.Math.floor(r8);
        r3 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1));
        if (r3 != 0) goto L_0x0023;
    L_0x001c:
        r2 = (int) r8;
        r0 = r8;
        r3 = r2 & 7;
        switch(r3) {
            case 0: goto L_0x0040;
            case 1: goto L_0x0038;
            case 2: goto L_0x0036;
            case 3: goto L_0x0034;
            case 4: goto L_0x0032;
            case 5: goto L_0x0030;
            case 6: goto L_0x002e;
            case 7: goto L_0x002c;
            default: goto L_0x0023;
        };
    L_0x0023:
        r4 = lgamma(r8);
        r4 = java.lang.Math.exp(r4);
        goto L_0x000a;
    L_0x002c:
        r8 = r8 - r6;
        r0 = r0 * r8;
    L_0x002e:
        r8 = r8 - r6;
        r0 = r0 * r8;
    L_0x0030:
        r8 = r8 - r6;
        r0 = r0 * r8;
    L_0x0032:
        r8 = r8 - r6;
        r0 = r0 * r8;
    L_0x0034:
        r8 = r8 - r6;
        r0 = r0 * r8;
    L_0x0036:
        r8 = r8 - r6;
        r0 = r0 * r8;
    L_0x0038:
        r3 = FACT;
        r4 = r2 >> 3;
        r4 = r3[r4];
        r4 = r4 * r0;
        goto L_0x000a;
    L_0x0040:
        r3 = FACT;
        r4 = r2 >> 3;
        r4 = r3[r4];
        goto L_0x000a;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.javia.arity.MoreMath.factorial(double):double");
    }

    public static final double gcd(double x, double y) {
        if (Double.isNaN(x) || Double.isNaN(y) || Double.isInfinite(x) || Double.isInfinite(y)) {
            return Double.NaN;
        }
        x = Math.abs(x);
        y = Math.abs(y);
        while (x < 1.0E15d * y) {
            double save = y;
            y = x % y;
            x = save;
        }
        return x;
    }

    public static final double intExp10(int exp) {
        return Double.parseDouble("1E" + exp);
    }

    public static final int intLog10(double x) {
        return (int) Math.floor(Math.log10(x));
    }

    private static final boolean isPiMultiple(double x) {
        double d = x / 3.141592653589793d;
        return d == Math.floor(d);
    }

    public static final double lgamma(double x) {
        double tmp = x + 5.2421875d;
        double sum = 0.9999999999999971d;
        for (double d : GAMMA) {
            x += 1.0d;
            sum += d / x;
        }
        return ((0.9189385332046728d + Math.log(sum)) + ((tmp - 4.7421875d) * Math.log(tmp))) - tmp;
    }

    public static final double log2(double x) {
        return Math.log(x) * LOG2E;
    }

    public static final double permutations(double n, double k) {
        if (n < 0.0d || k < 0.0d) {
            return Double.NaN;
        }
        if (n < k) {
            return 0.0d;
        }
        if (Math.floor(n) != n || Math.floor(k) != k) {
            return Math.exp(lgamma(n) - lgamma(n - k));
        }
        if (n <= 170.0d && 10.0d < k && k <= 170.0d) {
            return factorial(n) / factorial(n - k);
        }
        double r = 1.0d;
        double limit = (n - k) + 0.5d;
        for (double i = n; i > limit && r < Double.POSITIVE_INFINITY; i -= 1.0d) {
            r *= i;
        }
        return r;
    }

    public static final double sin(double x) {
        return isPiMultiple(x) ? 0.0d : Math.sin(x);
    }

    public static final double tan(double x) {
        return isPiMultiple(x) ? 0.0d : Math.tan(x);
    }

    public static final double trunc(double x) {
        return x >= 0.0d ? Math.floor(x) : Math.ceil(x);
    }
}
