package com.innovit.scientificcalculator;

import org.javia.arity.Symbols;
import org.javia.arity.SyntaxException;

public class ArityParser implements Parser {
    public String parse(String expression) {
        try {
            Double result = Double.valueOf(new Symbols().eval(expression));
            if (result.doubleValue() % 1.0d == 0.0d) {
                return "" + result.longValue();
            }
            return "" + result;
        } catch (SyntaxException ex) {
            return "ERROR: " + ex.getMessage();
        }
    }
}
