package org.javia.arity;

public class Util {
    public static final int FLOAT_PRECISION = -1;
    public static final int LEN_UNLIMITED = 100;

    public static String complexToString(Complex x, int maxLen, int rounding) {
        if (x.im == 0.0d) {
            return doubleToString(x.re, maxLen, rounding);
        }
        if (x.isNaN()) {
            return "NaN";
        }
        double xre = x.re;
        double xim = x.im;
        if (x.isInfinite()) {
            if (!Double.isInfinite(xre)) {
                xre = 0.0d;
            } else if (!Double.isInfinite(xim)) {
                xim = 0.0d;
            }
        }
        if (xim == 0.0d) {
            return doubleToString(xre, maxLen, rounding);
        }
        String str;
        boolean addPlus = xre != 0.0d && xim >= 0.0d;
        String sre = xre == 0.0d ? "" : doubleToString(xre, rounding);
        String sim = doubleToString(xim, rounding);
        String finalMultiply = Double.isInfinite(xim) ? "*" : "";
        if (sim.equals("1")) {
            sim = "";
        }
        if (sim.equals("-1")) {
            sim = "-";
        }
        if (maxLen != 100) {
            maxLen--;
            if (addPlus) {
                maxLen--;
            }
            maxLen -= finalMultiply.length();
            int sreLen = sre.length();
            int simLen = sim.length();
            int reduce = (sreLen + simLen) - maxLen;
            if (reduce > 0) {
                int diff = Math.abs(sreLen - simLen);
                int rShort = reduce > diff ? (reduce - diff) / 2 : 0;
                int rLong = rShort + Math.min(reduce, diff);
                int sreTarget = sreLen;
                int simTarget = simLen;
                if (sreLen > simLen) {
                    sreTarget -= rLong;
                    simTarget -= rShort;
                } else {
                    sreTarget -= rShort;
                    simTarget -= rLong;
                }
                if (sreTarget + simTarget > maxLen) {
                    simTarget--;
                }
                sre = sizeTruncate(sre, sreTarget);
                sim = sizeTruncate(sim, simTarget);
            }
        }
        StringBuilder append = new StringBuilder().append(sre);
        if (addPlus) {
            str = "+";
        } else {
            str = "";
        }
        return append.append(str).append(sim).append(finalMultiply).append('i').toString();
    }

    public static String doubleToString(double v, int roundingDigits) {
        double absv = Math.abs(v);
        String str = roundingDigits == -1 ? Float.toString((float) absv) : Double.toString(absv);
        StringBuffer buf = new StringBuffer(str);
        int roundingStart = (roundingDigits <= 0 || roundingDigits > 13) ? 17 : 16 - roundingDigits;
        int ePos = str.lastIndexOf(69);
        int exp = ePos != -1 ? Integer.parseInt(str.substring(ePos + 1)) : 0;
        if (ePos != -1) {
            buf.setLength(ePos);
        }
        int len = buf.length();
        int dotPos = 0;
        while (dotPos < len && buf.charAt(dotPos) != '.') {
            dotPos++;
        }
        exp += dotPos;
        if (dotPos < len) {
            buf.deleteCharAt(dotPos);
            len--;
        }
        int p = 0;
        while (p < len && buf.charAt(p) == '0') {
            roundingStart++;
            p++;
        }
        if (roundingStart < len) {
            if (buf.charAt(roundingStart) >= '5') {
                p = roundingStart - 1;
                while (p >= 0 && buf.charAt(p) == '9') {
                    buf.setCharAt(p, '0');
                    p--;
                }
                if (p >= 0) {
                    buf.setCharAt(p, (char) (buf.charAt(p) + 1));
                } else {
                    buf.insert(0, '1');
                    roundingStart++;
                    exp++;
                }
            }
            buf.setLength(roundingStart);
        }
        if (exp < -5 || exp > 10) {
            buf.insert(1, '.');
            exp--;
        } else {
            int i;
            for (i = len; i < exp; i++) {
                buf.append('0');
            }
            for (i = exp; i <= 0; i++) {
                buf.insert(0, '0');
            }
            if (exp <= 0) {
                exp = 1;
            }
            buf.insert(exp, '.');
            exp = 0;
        }
        int tail = buf.length() - 1;
        while (tail >= 0 && buf.charAt(tail) == '0') {
            buf.deleteCharAt(tail);
            tail--;
        }
        if (tail >= 0 && buf.charAt(tail) == '.') {
            buf.deleteCharAt(tail);
        }
        if (exp != 0) {
            buf.append('E').append(exp);
        }
        if (v < 0.0d) {
            buf.insert(0, '-');
        }
        return buf.toString();
    }

    public static String doubleToString(double x, int maxLen, int rounding) {
        return sizeTruncate(doubleToString(x, rounding), maxLen);
    }

    public static double shortApprox(double value, double maxError) {
        double v = Math.abs(value);
        double tail = MoreMath.intExp10(MoreMath.intLog10(Math.abs(maxError)));
        double ret = Math.floor((v / tail) + 0.5d) * tail;
        return value < 0.0d ? -ret : ret;
    }

    static String sizeTruncate(String str, int maxLen) {
        if (maxLen == 100) {
            return str;
        }
        int ePos = str.lastIndexOf(69);
        String tail = ePos != -1 ? str.substring(ePos) : "";
        int tailLen = tail.length();
        int headLen = str.length() - tailLen;
        int keepLen = Math.min(headLen, maxLen - tailLen);
        if (keepLen < 1) {
            return str;
        }
        if (keepLen < 2 && str.length() > 0 && str.charAt(0) == '-') {
            return str;
        }
        int dotPos = str.indexOf(46);
        if (dotPos == -1) {
            dotPos = headLen;
        }
        if (dotPos <= keepLen) {
            return str.substring(0, keepLen) + tail;
        }
        int exponent = ePos != -1 ? Integer.parseInt(str.substring(ePos + 1)) : 0;
        int start = str.charAt(0) == '-' ? 1 : 0;
        return sizeTruncate(str.substring(0, start + 1) + '.' + str.substring(start + 1, headLen) + 'E' + (exponent + ((dotPos - start) - 1)), maxLen);
    }
}
