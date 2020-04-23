package com.fathzer.soft.javaluator;

public class Operator {
    private Associativity associativity;
    private int operandCount;
    private int precedence;
    private String symbol;

    public enum Associativity {
        LEFT,
        RIGHT,
        NONE
    }

    public Operator(String symbol, int operandCount, Associativity associativity, int precedence) {
        if (symbol == null || associativity == null) {
            throw new NullPointerException();
        } else if (symbol.length() == 0) {
            throw new IllegalArgumentException("Operator symbol can't be null");
        } else if (operandCount < 1 || operandCount > 2) {
            throw new IllegalArgumentException("Only unary and binary operators are supported");
        } else if (Associativity.NONE.equals(associativity)) {
            throw new IllegalArgumentException("None associativity operators are not supported");
        } else {
            this.symbol = symbol;
            this.operandCount = operandCount;
            this.associativity = associativity;
            this.precedence = precedence;
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || (obj instanceof Operator)) {
            return false;
        }
        Operator other = (Operator) obj;
        if (this.operandCount != other.operandCount || this.associativity != other.associativity) {
            return false;
        }
        if (this.symbol == null) {
            if (other.symbol != null) {
                return false;
            }
        } else if (!this.symbol.equals(other.symbol)) {
            return false;
        }
        if (this.precedence != other.precedence) {
            return false;
        }
        return true;
    }

    public Associativity getAssociativity() {
        return this.associativity;
    }

    public int getOperandCount() {
        return this.operandCount;
    }

    public int getPrecedence() {
        return this.precedence;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public int hashCode() {
        int i = 0;
        int hashCode = (((this.operandCount + 31) * 31) + (this.associativity == null ? 0 : this.associativity.hashCode())) * 31;
        if (this.symbol != null) {
            i = this.symbol.hashCode();
        }
        return ((hashCode + i) * 31) + this.precedence;
    }
}
