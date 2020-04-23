package org.javia.arity;

class DoubleStack {
    private double[] im = new double[8];
    private double[] re = new double[8];
    private int size = 0;

    DoubleStack() {
    }

    void clear() {
        this.size = 0;
    }

    double[] getIm() {
        boolean allZero = true;
        for (int i = 0; i < this.size; i++) {
            if (this.im[i] != 0.0d) {
                allZero = false;
                break;
            }
        }
        if (allZero) {
            return null;
        }
        double[] trimmed = new double[this.size];
        System.arraycopy(this.im, 0, trimmed, 0, this.size);
        return trimmed;
    }

    double[] getRe() {
        double[] trimmed = new double[this.size];
        System.arraycopy(this.re, 0, trimmed, 0, this.size);
        return trimmed;
    }

    void pop() {
        this.size--;
    }

    void pop(int cnt) {
        if (cnt > this.size) {
            throw new Error("pop " + cnt + " from " + this.size);
        }
        this.size -= cnt;
    }

    void push(double a, double b) {
        if (this.size >= this.re.length) {
            int newSize = this.re.length << 1;
            double[] newRe = new double[newSize];
            double[] newIm = new double[newSize];
            System.arraycopy(this.re, 0, newRe, 0, this.re.length);
            System.arraycopy(this.im, 0, newIm, 0, this.re.length);
            this.re = newRe;
            this.im = newIm;
        }
        this.re[this.size] = a;
        this.im[this.size] = b;
        this.size++;
    }
}
