package com.fathzer.soft.javaluator;

public class BracketPair {
    public static final BracketPair ANGLES = new BracketPair('<', '>');
    public static final BracketPair BRACES = new BracketPair('{', '}');
    public static final BracketPair BRACKETS = new BracketPair('[', ']');
    public static final BracketPair PARENTHESES = new BracketPair('(', ')');
    private String close;
    private String open;

    public BracketPair(char open, char close) {
        this.open = new String(new char[]{open});
        this.close = new String(new char[]{close});
    }

    public String getClose() {
        return this.close;
    }

    public String getOpen() {
        return this.open;
    }

    public String toString() {
        return this.open + this.close;
    }
}
