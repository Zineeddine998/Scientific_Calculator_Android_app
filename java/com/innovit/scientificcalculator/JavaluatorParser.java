package com.innovit.scientificcalculator;

import com.fathzer.soft.javaluator.DoubleEvaluator;

public class JavaluatorParser implements Parser {
    public String parse(String expression) {
        try {
            Double result = (Double) new DoubleEvaluator().evaluate(expression);
            if (result.doubleValue() % 1.0d == 0.0d) {
                return "" + result.longValue();
            }
            return "" + result;
        } catch (Exception e) {
            return "ERROR";
        }
    }
}
