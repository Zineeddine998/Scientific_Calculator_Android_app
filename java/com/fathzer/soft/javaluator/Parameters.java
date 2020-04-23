package com.fathzer.soft.javaluator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parameters {
    private final List<Constant> constants = new ArrayList();
    private final List<BracketPair> expressionBrackets = new ArrayList();
    private final List<BracketPair> functionBrackets = new ArrayList();
    private String functionSeparator;
    private final List<Function> functions = new ArrayList();
    private final List<Operator> operators = new ArrayList();
    private final Map<String, String> translations = new HashMap();

    public Parameters() {
        setFunctionArgumentSeparator(',');
    }

    private void setTranslation(String name, String translatedName) {
        this.translations.put(name, translatedName);
    }

    public void add(Constant constant) {
        this.constants.add(constant);
    }

    public void add(Function function) {
        this.functions.add(function);
    }

    public void add(Operator operator) {
        this.operators.add(operator);
    }

    public void addConstants(Collection<Constant> constants) {
        this.constants.addAll(constants);
    }

    public void addExpressionBracket(BracketPair pair) {
        this.expressionBrackets.add(pair);
    }

    public void addExpressionBrackets(Collection<BracketPair> brackets) {
        this.expressionBrackets.addAll(brackets);
    }

    public void addFunctionBracket(BracketPair pair) {
        this.functionBrackets.add(pair);
    }

    public void addFunctionBrackets(Collection<BracketPair> brackets) {
        this.functionBrackets.addAll(brackets);
    }

    public void addFunctions(Collection<Function> functions) {
        this.functions.addAll(functions);
    }

    public void addOperators(Collection<Operator> operators) {
        this.operators.addAll(operators);
    }

    public Collection<Constant> getConstants() {
        return this.constants;
    }

    public Collection<BracketPair> getExpressionBrackets() {
        return this.expressionBrackets;
    }

    public String getFunctionArgumentSeparator() {
        return this.functionSeparator;
    }

    public Collection<BracketPair> getFunctionBrackets() {
        return this.functionBrackets;
    }

    public Collection<Function> getFunctions() {
        return this.functions;
    }

    public Collection<Operator> getOperators() {
        return this.operators;
    }

    String getTranslation(String originalName) {
        String translation = (String) this.translations.get(originalName);
        return translation == null ? originalName : translation;
    }

    public void setFunctionArgumentSeparator(char separator) {
        this.functionSeparator = new String(new char[]{separator});
    }

    public void setTranslation(Constant constant, String translatedName) {
        setTranslation(constant.getName(), translatedName);
    }

    public void setTranslation(Function function, String translatedName) {
        setTranslation(function.getName(), translatedName);
    }
}
