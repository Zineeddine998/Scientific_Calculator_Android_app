package org.javia.arity;

class Token {
    static final int LEFT = 2;
    static final int PREFIX = 1;
    static final int RIGHT = 3;
    static final int SUFIX = 4;
    int arity;
    final int assoc;
    final int id;
    String name = null;
    int position;
    final int priority;
    double value;
    final byte vmop;

    Token(int id, int priority, int assoc, int vmop) {
        this.id = id;
        this.priority = priority;
        this.assoc = assoc;
        this.vmop = (byte) vmop;
        this.arity = id == 11 ? 1 : -3;
    }

    public boolean isDerivative() {
        if (this.name != null) {
            int len = this.name.length();
            if (len > 0 && this.name.charAt(len - 1) == '\'') {
                return true;
            }
        }
        return false;
    }

    Token setAlpha(String alpha) {
        this.name = alpha;
        return this;
    }

    Token setPos(int pos) {
        this.position = pos;
        return this;
    }

    Token setValue(double value) {
        this.value = value;
        return this;
    }

    public String toString() {
        switch (this.id) {
            case 9:
                return "" + this.value;
            case 10:
                return this.name;
            case 11:
                return this.name + '(' + this.arity + ')';
            default:
                return "" + this.id;
        }
    }
}
