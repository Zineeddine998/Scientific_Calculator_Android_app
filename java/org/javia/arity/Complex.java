package org.javia.arity;

public class Complex {
    public double im;
    public double re;

    public Complex(double re, double im) {
        set(re, im);
    }

    public Complex(Complex o) {
        set(o);
    }

    private final Complex normalizeInfinity() {
        if (!Double.isInfinite(this.im)) {
            this.im = 0.0d;
        } else if (!Double.isInfinite(this.re)) {
            this.re = 0.0d;
        }
        return this;
    }

    private final Complex sqrt1z() {
        return set((1.0d - (this.re * this.re)) + (this.im * this.im), (-2.0d * this.re) * this.im).sqrt();
    }

    private final Complex swap() {
        return set(this.im, this.re);
    }

    public final double abs() {
        double abs = Math.abs(this.re);
        double b = Math.abs(this.im);
        if (abs == 0.0d || b == 0.0d) {
            return abs + b;
        }
        boolean aGreater = abs > b;
        double q = aGreater ? b / abs : abs / b;
        if (!aGreater) {
            abs = b;
        }
        return Math.sqrt(1.0d + (q * q)) * abs;
    }

    public final double abs2() {
        return (this.re * this.re) + (this.im * this.im);
    }

    public final Complex acos() {
        if (this.im == 0.0d && Math.abs(this.re) <= 1.0d) {
            return set(Math.acos(this.re), 0.0d);
        }
        return sqrt1z().set(this.re - this.im, this.re + this.im).log().set(this.im, -this.re);
    }

    public final Complex acosh() {
        if (this.im == 0.0d && this.re >= 1.0d) {
            return set(MoreMath.acosh(this.re), 0.0d);
        }
        return set(((this.re * this.re) - (this.im * this.im)) - 1.0d, (2.0d * this.re) * this.im).sqrt().set(this.re + this.re, this.im + this.im).log();
    }

    public final Complex add(Complex o) {
        double ulp = Math.ulp(this.re);
        this.re += o.re;
        this.im += o.im;
        if (Math.abs(this.re) < 1024.0d * ulp) {
            this.re = 0.0d;
        }
        return this;
    }

    public final double arg() {
        return Math.atan2(this.im, this.re);
    }

    public double asReal() {
        return this.im == 0.0d ? this.re : Double.NaN;
    }

    public final Complex asin() {
        if (this.im == 0.0d && Math.abs(this.re) <= 1.0d) {
            return set(Math.asin(this.re), 0.0d);
        }
        double saveA = this.re;
        return sqrt1z().set(this.re - this.im, this.im + saveA).log().set(this.im, -this.re);
    }

    public final Complex asinh() {
        if (this.im == 0.0d) {
            return set(MoreMath.asinh(this.re), 0.0d);
        }
        return set(((this.re * this.re) - (this.im * this.im)) + 1.0d, (2.0d * this.re) * this.im).sqrt().set(this.re + this.re, this.im + this.im).log();
    }

    public final Complex atan() {
        if (this.im == 0.0d) {
            return set(Math.atan(this.re), 0.0d);
        }
        double a2 = this.re * this.re;
        double b2 = this.im * this.im;
        double down = (((a2 + b2) - this.im) - this.im) + 1.0d;
        return set((-((a2 + b2) - 1.0d)) / down, (-(this.re + this.re)) / down).log().set((-this.im) / 2.0d, this.re / 2.0d);
    }

    public final Complex atanh() {
        if (this.im == 0.0d) {
            return set(MoreMath.atanh(this.re), 0.0d);
        }
        double a2 = this.re * this.re;
        double down = ((a2 + 1.0d) - this.re) - this.re;
        return set(((1.0d - a2) - (this.im * this.im)) / down, (this.im + this.im) / down).log().set(this.re / 2.0d, this.im / 2.0d);
    }

    public final Complex combinations(Complex o) {
        if (this.im == 0.0d && o.im == 0.0d) {
            return set(MoreMath.combinations(this.re, o.re), 0.0d);
        }
        double a = this.re;
        double b = this.im;
        lgamma();
        double c = this.re;
        double d = this.im;
        set(o).lgamma();
        double e = this.re;
        double f = this.im;
        set(a - o.re, b - o.im).lgamma();
        return set((c - e) - this.re, (d - f) - this.im).exp();
    }

    public final Complex conjugate() {
        return set(this.re, -this.im);
    }

    public final Complex cos() {
        return this.im == 0.0d ? set(MoreMath.cos(this.re), 0.0d) : set(MoreMath.cos(this.re) * Math.cosh(this.im), (-MoreMath.sin(this.re)) * Math.sinh(this.im));
    }

    public final Complex cosh() {
        return this.im == 0.0d ? set(Math.cosh(this.re), 0.0d) : swap().cos().conjugate();
    }

    public final Complex div(Complex o) {
        double c = o.re;
        double d = o.im;
        if (this.im == 0.0d && d == 0.0d) {
            return set(this.re / c, 0.0d);
        }
        if (o.isInfinite() && isFinite()) {
            return set(0.0d, 0.0d);
        }
        if (d == 0.0d) {
            if (this.re == 0.0d) {
                return set(0.0d, this.im / c);
            }
            return set(this.re / c, this.im / c);
        } else if (c == 0.0d) {
            return set(this.im / d, (-this.re) / d);
        } else {
            double q;
            double down;
            if (Math.abs(c) > Math.abs(d)) {
                q = d / c;
                down = c + (d * q);
                return set((this.re + (this.im * q)) / down, (this.im - (this.re * q)) / down);
            }
            q = c / d;
            down = (c * q) + d;
            return set(((this.re * q) + this.im) / down, ((this.im * q) - this.re) / down);
        }
    }

    public final boolean equals(Complex o) {
        return (this.re == o.re || !(this.re == this.re || o.re == o.re)) && (this.im == o.im || !(this.im == this.im || o.im == o.im));
    }

    public final Complex exp() {
        double expRe = Math.exp(this.re);
        if (this.im == 0.0d) {
            return set(expRe, 0.0d);
        }
        return set(MoreMath.cos(this.im) * expRe, MoreMath.sin(this.im) * expRe);
    }

    public final Complex factorial() {
        return this.im == 0.0d ? set(MoreMath.factorial(this.re), 0.0d) : lgamma().exp();
    }

    public final Complex gcd(Complex o) {
        if (this.im == 0.0d && o.im == 0.0d) {
            return set(MoreMath.gcd(this.re, o.re), 0.0d);
        }
        Complex y = new Complex(o);
        double xabs2 = abs2();
        for (double yabs2 = y.abs2(); xabs2 < 1.0E30d * yabs2; yabs2 = y.abs2()) {
            double yRe = y.re;
            double yIm = y.im;
            y.set(mod(y));
            set(yRe, yIm);
            xabs2 = yabs2;
        }
        if (Math.abs(this.re) < Math.abs(this.im)) {
            set(-this.im, this.re);
        }
        if (this.re >= 0.0d) {
            return this;
        }
        negate();
        return this;
    }

    public final boolean isFinite() {
        return (isInfinite() || isNaN()) ? false : true;
    }

    public final boolean isInfinite() {
        return Double.isInfinite(this.re) || (Double.isInfinite(this.im) && !isNaN());
    }

    public final boolean isNaN() {
        return Double.isNaN(this.re) || Double.isNaN(this.im);
    }

    public final Complex lgamma() {
        double sumRe = 0.9999999999999971d;
        double sumIm = 0.0d;
        double down = (this.re * this.re) + (this.im * this.im);
        double xplusk = this.re;
        double[] GAMMA = MoreMath.GAMMA;
        for (double cc : GAMMA) {
            xplusk += 1.0d;
            down += (xplusk + xplusk) - 1.0d;
            sumRe += (cc * xplusk) / down;
            sumIm -= (this.im * cc) / down;
        }
        double a = this.re + 0.5d;
        double tmpRe = this.re + 5.2421875d;
        double saveIm = this.im;
        this.re = tmpRe;
        log();
        double termRe = (((this.re * a) - (this.im * saveIm)) + 0.9189385332046728d) - tmpRe;
        double termIm = ((this.im * a) + (this.re * saveIm)) - saveIm;
        set(sumRe, sumIm).log();
        this.re += termRe;
        this.im += termIm;
        return this;
    }

    public final Complex log() {
        if (this.im == 0.0d && this.re >= 0.0d) {
            return set(Math.log(this.re), 0.0d);
        }
        return set(Math.log(abs()), Math.atan2(this.im, this.re));
    }

    public final Complex mod(Complex o) {
        double a = this.re;
        double b = this.im;
        if (b == 0.0d && o.im == 0.0d) {
            return set(a % o.re, 0.0d);
        }
        return div(o).set(Math.rint(this.re), Math.rint(this.im)).mul(o).set(a - this.re, b - this.im);
    }

    Complex mul(double o) {
        this.re *= o;
        this.im *= o;
        return this;
    }

    public final Complex mul(Complex o) {
        double a = this.re;
        double b = this.im;
        double c = o.re;
        double d = o.im;
        if (b == 0.0d && d == 0.0d) {
            return set(a * c, 0.0d);
        }
        double mre = (a * c) - (b * d);
        double mim = (a * d) + (b * c);
        if (!set(mre, mim).isNaN()) {
            return this;
        }
        if (set(a, b).isInfinite()) {
            normalizeInfinity();
            a = this.re;
            b = this.im;
        }
        if (o.isInfinite()) {
            set(c, d).normalizeInfinity();
            c = this.re;
            d = this.im;
        }
        if (b == 0.0d) {
            if (d == 0.0d) {
                return set(a * c, 0.0d);
            }
            if (c == 0.0d) {
                return set(0.0d, a * d);
            }
            return set(a * c, a * d);
        } else if (a == 0.0d) {
            if (c == 0.0d) {
                return set((-b) * d, 0.0d);
            }
            if (d == 0.0d) {
                return set(0.0d, b * c);
            }
            return set((-b) * d, b * c);
        } else if (d == 0.0d) {
            return set(a * c, b * c);
        } else {
            if (c == 0.0d) {
                return set((-b) * d, a * d);
            }
            return set(mre, mim);
        }
    }

    public final Complex negate() {
        return set(-this.re, -this.im);
    }

    public final Complex permutations(Complex o) {
        if (this.im == 0.0d && o.im == 0.0d) {
            return set(MoreMath.permutations(this.re, o.re), 0.0d);
        }
        double a = this.re;
        double b = this.im;
        lgamma();
        double c = this.re;
        double d = this.im;
        set(a - o.re, b - o.im).lgamma();
        return set(c - this.re, d - this.im).exp();
    }

    public final Complex pow(Complex y) {
        double a;
        if (y.im == 0.0d) {
            if (y.re == 0.0d) {
                return set(1.0d, 0.0d);
            }
            if (this.im == 0.0d) {
                double res = Math.pow(this.re, y.re);
                if (res == res) {
                    return set(res, 0.0d);
                }
            }
            if (y.re == 2.0d) {
                return square();
            }
            if (y.re == 0.5d) {
                return sqrt();
            }
            double p = Math.pow(abs2(), y.re / 2.0d);
            a = arg() * y.re;
            return set(MoreMath.cos(a) * p, MoreMath.sin(a) * p);
        } else if (this.im != 0.0d || this.re <= 0.0d) {
            return log().set((y.re * this.re) - (y.im * this.im), (y.re * this.im) + (y.im * this.re)).exp();
        } else {
            a = Math.pow(this.re, y.re);
            return set(0.0d, y.im * Math.log(this.re)).exp().set(this.re * a, this.im * a);
        }
    }

    public Complex set(double re, double im) {
        this.re = re;
        this.im = im;
        return this;
    }

    public Complex set(Complex o) {
        this.re = o.re;
        this.im = o.im;
        return this;
    }

    public final Complex sin() {
        return this.im == 0.0d ? set(MoreMath.sin(this.re), 0.0d) : set(MoreMath.sin(this.re) * Math.cosh(this.im), MoreMath.cos(this.re) * Math.sinh(this.im));
    }

    public final Complex sinh() {
        return this.im == 0.0d ? set(Math.sinh(this.re), 0.0d) : swap().sin().swap();
    }

    public final Complex sqrt() {
        if (this.im != 0.0d) {
            double t = Math.sqrt((Math.abs(this.re) + abs()) / 2.0d);
            if (this.re >= 0.0d) {
                set(t, this.im / (t + t));
            } else {
                double abs = Math.abs(this.im) / (t + t);
                if (this.im < 0.0d) {
                    t = -t;
                }
                set(abs, t);
            }
        } else if (this.re >= 0.0d) {
            set(Math.sqrt(this.re), 0.0d);
        } else {
            set(0.0d, Math.sqrt(-this.re));
        }
        return this;
    }

    public final Complex square() {
        return set((this.re * this.re) - (this.im * this.im), (2.0d * this.re) * this.im);
    }

    public final Complex sub(Complex o) {
        double ulp = Math.ulp(this.re);
        this.re -= o.re;
        this.im -= o.im;
        if (Math.abs(this.re) < 1024.0d * ulp) {
            this.re = 0.0d;
        }
        return this;
    }

    public final Complex tan() {
        if (this.im == 0.0d) {
            return set(MoreMath.tan(this.re), 0.0d);
        }
        double aa = this.re + this.re;
        double bb = this.im + this.im;
        double down = MoreMath.cos(aa) + Math.cosh(bb);
        return set(MoreMath.sin(aa) / down, Math.sinh(bb) / down);
    }

    public final Complex tanh() {
        return this.im == 0.0d ? set(Math.tanh(this.re), 0.0d) : swap().tan().swap();
    }

    public String toString() {
        return this.im == 0.0d ? "" + this.re : "(" + this.re + ", " + this.im + ')';
    }
}
