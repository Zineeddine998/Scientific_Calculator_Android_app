package com.fathzer.soft.javaluator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class AbstractEvaluator<T> {
    private final Map<String, Constant> constants = new HashMap();
    private final Map<String, BracketPair> expressionBrackets;
    private final String functionArgumentSeparator;
    private final Map<String, BracketPair> functionBrackets = new HashMap();
    private final Map<String, Function> functions = new HashMap();
    private final Map<String, List<Operator>> operators = new HashMap();
    private final Tokenizer tokenizer;

    protected AbstractEvaluator(Parameters parameters) {
        ArrayList<String> tokenDelimitersBuilder = new ArrayList();
        for (BracketPair pair : parameters.getFunctionBrackets()) {
            this.functionBrackets.put(pair.getOpen(), pair);
            this.functionBrackets.put(pair.getClose(), pair);
            tokenDelimitersBuilder.add(pair.getOpen());
            tokenDelimitersBuilder.add(pair.getClose());
        }
        this.expressionBrackets = new HashMap();
        for (BracketPair pair2 : parameters.getExpressionBrackets()) {
            this.expressionBrackets.put(pair2.getOpen(), pair2);
            this.expressionBrackets.put(pair2.getClose(), pair2);
            tokenDelimitersBuilder.add(pair2.getOpen());
            tokenDelimitersBuilder.add(pair2.getClose());
        }
        if (this.operators != null) {
            for (Operator ope : parameters.getOperators()) {
                tokenDelimitersBuilder.add(ope.getSymbol());
                List<Operator> known = (List) this.operators.get(ope.getSymbol());
                if (known == null) {
                    known = new ArrayList();
                    this.operators.put(ope.getSymbol(), known);
                }
                known.add(ope);
                if (known.size() > 1) {
                    validateHomonyms(known);
                }
            }
        }
        boolean needFunctionSeparator = false;
        if (parameters.getFunctions() != null) {
            for (Function function : parameters.getFunctions()) {
                this.functions.put(parameters.getTranslation(function.getName()), function);
                if (function.getMaximumArgumentCount() > 1) {
                    needFunctionSeparator = true;
                }
            }
        }
        if (parameters.getConstants() != null) {
            for (Constant constant : parameters.getConstants()) {
                this.constants.put(parameters.getTranslation(constant.getName()), constant);
            }
        }
        this.functionArgumentSeparator = parameters.getFunctionArgumentSeparator();
        if (needFunctionSeparator) {
            tokenDelimitersBuilder.add(this.functionArgumentSeparator);
        }
        this.tokenizer = new Tokenizer(tokenDelimitersBuilder);
    }

    private void doFunction(Deque<T> values, Function function, int argCount, Object evaluationContext) {
        if (function.getMinimumArgumentCount() > argCount || function.getMaximumArgumentCount() < argCount) {
            throw new IllegalArgumentException("Invalid argument count for " + function.getName());
        }
        values.push(evaluate(function, getArguments(values, argCount), evaluationContext));
    }

    private Iterator<T> getArguments(Deque<T> values, int nb) {
        if (values.size() < nb) {
            throw new IllegalArgumentException();
        }
        LinkedList<T> result = new LinkedList();
        for (int i = 0; i < nb; i++) {
            result.addFirst(values.pop());
        }
        return result.iterator();
    }

    private BracketPair getBracketPair(String token) {
        BracketPair result = (BracketPair) this.expressionBrackets.get(token);
        return result == null ? (BracketPair) this.functionBrackets.get(token) : result;
    }

    private void output(Deque<T> values, Token token, Object evaluationContext) {
        if (token.isLiteral()) {
            String literal = token.getLiteral();
            Constant ct = (Constant) this.constants.get(literal);
            T value = ct == null ? null : evaluate(ct, evaluationContext);
            if (value == null && evaluationContext != null && (evaluationContext instanceof AbstractVariableSet)) {
                value = ((AbstractVariableSet) evaluationContext).get(literal);
            }
            if (value == null) {
                value = toValue(literal, evaluationContext);
            }
            values.push(value);
        } else if (token.isOperator()) {
            Operator operator = token.getOperator();
            values.push(evaluate(operator, getArguments(values, operator.getOperandCount()), evaluationContext));
        } else {
            throw new IllegalArgumentException();
        }
    }

    private Token toToken(Token previous, String token) {
        if (token.equals(this.functionArgumentSeparator)) {
            return Token.FUNCTION_ARG_SEPARATOR;
        }
        if (this.functions.containsKey(token)) {
            return Token.buildFunction((Function) this.functions.get(token));
        }
        if (this.operators.containsKey(token)) {
            List<Operator> list = (List) this.operators.get(token);
            return list.size() == 1 ? Token.buildOperator((Operator) list.get(0)) : Token.buildOperator(guessOperator(previous, list));
        } else {
            BracketPair brackets = getBracketPair(token);
            if (brackets == null) {
                return Token.buildLiteral(token);
            }
            if (brackets.getOpen().equals(token)) {
                return Token.buildOpenToken(brackets);
            }
            return Token.buildCloseToken(brackets);
        }
    }

    protected T evaluate(Constant constant, Object evaluationContext) {
        throw new RuntimeException("evaluate(Constant) is not implemented for " + constant.getName());
    }

    protected T evaluate(Function function, Iterator<T> it, Object evaluationContext) {
        throw new RuntimeException("evaluate(Function, Iterator) is not implemented for " + function.getName());
    }

    protected T evaluate(Operator operator, Iterator<T> it, Object evaluationContext) {
        throw new RuntimeException("evaluate(Operator, Iterator) is not implemented for " + operator.getSymbol());
    }

    public T evaluate(String expression) {
        return evaluate(expression, null);
    }

    /* JADX WARNING: Removed duplicated region for block: B:44:0x0114  */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x00db A:{SYNTHETIC} */
    public T evaluate(java.lang.String r18, java.lang.Object r19) {
        /*
        r17 = this;
        r13 = new java.util.ArrayDeque;
        r13.<init>();
        r9 = new java.util.ArrayDeque;
        r9.<init>();
        r0 = r17;
        r14 = r0.functions;
        r14 = r14.isEmpty();
        if (r14 == 0) goto L_0x0068;
    L_0x0014:
        r7 = 0;
    L_0x0015:
        r12 = r17.tokenize(r18);
        r6 = 0;
    L_0x001a:
        r14 = r12.hasNext();
        if (r14 == 0) goto L_0x021a;
    L_0x0020:
        r10 = r12.next();
        r10 = (java.lang.String) r10;
        r0 = r17;
        r11 = r0.toToken(r6, r10);
        r14 = r11.isOpenBracket();
        if (r14 == 0) goto L_0x0099;
    L_0x0032:
        r9.push(r11);
        if (r6 == 0) goto L_0x006e;
    L_0x0037:
        r14 = r6.isFunction();
        if (r14 == 0) goto L_0x006e;
    L_0x003d:
        r0 = r17;
        r14 = r0.functionBrackets;
        r15 = r11.getBrackets();
        r15 = r15.getOpen();
        r14 = r14.containsKey(r15);
        if (r14 != 0) goto L_0x0147;
    L_0x004f:
        r14 = new java.lang.IllegalArgumentException;
        r15 = new java.lang.StringBuilder;
        r15.<init>();
        r16 = "Invalid bracket after function: ";
        r15 = r15.append(r16);
        r15 = r15.append(r10);
        r15 = r15.toString();
        r14.<init>(r15);
        throw r14;
    L_0x0068:
        r7 = new java.util.ArrayDeque;
        r7.<init>();
        goto L_0x0015;
    L_0x006e:
        r0 = r17;
        r14 = r0.expressionBrackets;
        r15 = r11.getBrackets();
        r15 = r15.getOpen();
        r14 = r14.containsKey(r15);
        if (r14 != 0) goto L_0x0147;
    L_0x0080:
        r14 = new java.lang.IllegalArgumentException;
        r15 = new java.lang.StringBuilder;
        r15.<init>();
        r16 = "Invalid bracket in expression: ";
        r15 = r15.append(r16);
        r15 = r15.append(r10);
        r15 = r15.toString();
        r14.<init>(r15);
        throw r14;
    L_0x0099:
        r14 = r11.isCloseBracket();
        if (r14 == 0) goto L_0x014a;
    L_0x009f:
        if (r6 != 0) goto L_0x00a9;
    L_0x00a1:
        r14 = new java.lang.IllegalArgumentException;
        r15 = "expression can't start with a close bracket";
        r14.<init>(r15);
        throw r14;
    L_0x00a9:
        r14 = r6.isFunctionArgumentSeparator();
        if (r14 == 0) goto L_0x00b7;
    L_0x00af:
        r14 = new java.lang.IllegalArgumentException;
        r15 = "argument is missing";
        r14.<init>(r15);
        throw r14;
    L_0x00b7:
        r3 = r11.getBrackets();
        r4 = 0;
    L_0x00bc:
        r14 = r9.isEmpty();
        if (r14 != 0) goto L_0x00d9;
    L_0x00c2:
        r8 = r9.pop();
        r8 = (com.fathzer.soft.javaluator.Token) r8;
        r14 = r8.isOpenBracket();
        if (r14 == 0) goto L_0x010c;
    L_0x00ce:
        r14 = r8.getBrackets();
        r14 = r14.equals(r3);
        if (r14 == 0) goto L_0x00e3;
    L_0x00d8:
        r4 = 1;
    L_0x00d9:
        if (r4 != 0) goto L_0x0114;
    L_0x00db:
        r14 = new java.lang.IllegalArgumentException;
        r15 = "Parentheses mismatched";
        r14.<init>(r15);
        throw r14;
    L_0x00e3:
        r14 = new java.lang.IllegalArgumentException;
        r15 = new java.lang.StringBuilder;
        r15.<init>();
        r16 = "Invalid parenthesis match ";
        r15 = r15.append(r16);
        r16 = r8.getBrackets();
        r16 = r16.getOpen();
        r15 = r15.append(r16);
        r16 = r3.getClose();
        r15 = r15.append(r16);
        r15 = r15.toString();
        r14.<init>(r15);
        throw r14;
    L_0x010c:
        r0 = r17;
        r1 = r19;
        r0.output(r13, r8, r1);
        goto L_0x00bc;
    L_0x0114:
        r14 = r9.isEmpty();
        if (r14 != 0) goto L_0x0147;
    L_0x011a:
        r14 = r9.peek();
        r14 = (com.fathzer.soft.javaluator.Token) r14;
        r14 = r14.isFunction();
        if (r14 == 0) goto L_0x0147;
    L_0x0126:
        r15 = r13.size();
        r14 = r7.pop();
        r14 = (java.lang.Integer) r14;
        r14 = r14.intValue();
        r2 = r15 - r14;
        r14 = r9.pop();
        r14 = (com.fathzer.soft.javaluator.Token) r14;
        r14 = r14.getFunction();
        r0 = r17;
        r1 = r19;
        r0.doFunction(r13, r14, r2, r1);
    L_0x0147:
        r6 = r11;
        goto L_0x001a;
    L_0x014a:
        r14 = r11.isFunctionArgumentSeparator();
        if (r14 == 0) goto L_0x019a;
    L_0x0150:
        if (r6 != 0) goto L_0x015a;
    L_0x0152:
        r14 = new java.lang.IllegalArgumentException;
        r15 = "expression can't start with a function argument separator";
        r14.<init>(r15);
        throw r14;
    L_0x015a:
        r14 = r6.isOpenBracket();
        if (r14 != 0) goto L_0x0166;
    L_0x0160:
        r14 = r6.isFunctionArgumentSeparator();
        if (r14 == 0) goto L_0x016e;
    L_0x0166:
        r14 = new java.lang.IllegalArgumentException;
        r15 = "argument is missing";
        r14.<init>(r15);
        throw r14;
    L_0x016e:
        r5 = 0;
    L_0x016f:
        r14 = r9.isEmpty();
        if (r14 != 0) goto L_0x0182;
    L_0x0175:
        r14 = r9.peek();
        r14 = (com.fathzer.soft.javaluator.Token) r14;
        r14 = r14.isOpenBracket();
        if (r14 == 0) goto L_0x018c;
    L_0x0181:
        r5 = 1;
    L_0x0182:
        if (r5 != 0) goto L_0x0147;
    L_0x0184:
        r14 = new java.lang.IllegalArgumentException;
        r15 = "Separator or parentheses mismatched";
        r14.<init>(r15);
        throw r14;
    L_0x018c:
        r14 = r9.pop();
        r14 = (com.fathzer.soft.javaluator.Token) r14;
        r0 = r17;
        r1 = r19;
        r0.output(r13, r14, r1);
        goto L_0x016f;
    L_0x019a:
        r14 = r11.isFunction();
        if (r14 == 0) goto L_0x01af;
    L_0x01a0:
        r9.push(r11);
        r14 = r13.size();
        r14 = java.lang.Integer.valueOf(r14);
        r7.push(r14);
        goto L_0x0147;
    L_0x01af:
        r14 = r11.isOperator();
        if (r14 == 0) goto L_0x01fa;
    L_0x01b5:
        r14 = r9.isEmpty();
        if (r14 != 0) goto L_0x01f5;
    L_0x01bb:
        r8 = r9.peek();
        r8 = (com.fathzer.soft.javaluator.Token) r8;
        r14 = r8.isOperator();
        if (r14 == 0) goto L_0x01f5;
    L_0x01c7:
        r14 = r11.getAssociativity();
        r15 = com.fathzer.soft.javaluator.Operator.Associativity.LEFT;
        r14 = r14.equals(r15);
        if (r14 == 0) goto L_0x01dd;
    L_0x01d3:
        r14 = r11.getPrecedence();
        r15 = r8.getPrecedence();
        if (r14 <= r15) goto L_0x01e7;
    L_0x01dd:
        r14 = r11.getPrecedence();
        r15 = r8.getPrecedence();
        if (r14 >= r15) goto L_0x01f5;
    L_0x01e7:
        r14 = r9.pop();
        r14 = (com.fathzer.soft.javaluator.Token) r14;
        r0 = r17;
        r1 = r19;
        r0.output(r13, r14, r1);
        goto L_0x01b5;
    L_0x01f5:
        r9.push(r11);
        goto L_0x0147;
    L_0x01fa:
        if (r6 == 0) goto L_0x020a;
    L_0x01fc:
        r14 = r6.isLiteral();
        if (r14 == 0) goto L_0x020a;
    L_0x0202:
        r14 = new java.lang.IllegalArgumentException;
        r15 = "A literal can't follow another literal";
        r14.<init>(r15);
        throw r14;
    L_0x020a:
        r0 = r17;
        r1 = r19;
        r0.output(r13, r11, r1);
        goto L_0x0147;
    L_0x0213:
        r0 = r17;
        r1 = r19;
        r0.output(r13, r8, r1);
    L_0x021a:
        r14 = r9.isEmpty();
        if (r14 != 0) goto L_0x023a;
    L_0x0220:
        r8 = r9.pop();
        r8 = (com.fathzer.soft.javaluator.Token) r8;
        r14 = r8.isOpenBracket();
        if (r14 != 0) goto L_0x0232;
    L_0x022c:
        r14 = r8.isCloseBracket();
        if (r14 == 0) goto L_0x0213;
    L_0x0232:
        r14 = new java.lang.IllegalArgumentException;
        r15 = "Parentheses mismatched";
        r14.<init>(r15);
        throw r14;
    L_0x023a:
        r14 = r13.size();
        r15 = 1;
        if (r14 == r15) goto L_0x0247;
    L_0x0241:
        r14 = new java.lang.IllegalArgumentException;
        r14.<init>();
        throw r14;
    L_0x0247:
        r14 = r13.pop();
        return r14;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fathzer.soft.javaluator.AbstractEvaluator.evaluate(java.lang.String, java.lang.Object):T");
    }

    public Collection<Constant> getConstants() {
        return this.constants.values();
    }

    public Collection<Function> getFunctions() {
        return this.functions.values();
    }

    public Collection<Operator> getOperators() {
        ArrayList<Operator> result = new ArrayList();
        for (List<Operator> list : this.operators.values()) {
            result.addAll(list);
        }
        return result;
    }

    protected Operator guessOperator(Token previous, List<Operator> candidates) {
        int argCount = (previous == null || !(previous.isCloseBracket() || previous.isLiteral())) ? 1 : 2;
        for (Operator operator : candidates) {
            if (operator.getOperandCount() == argCount) {
                return operator;
            }
        }
        return null;
    }

    protected abstract T toValue(String str, Object obj);

    protected Iterator<String> tokenize(String expression) {
        return this.tokenizer.tokenize(expression);
    }

    protected void validateHomonyms(List<Operator> operators) {
        if (operators.size() > 2) {
            throw new IllegalArgumentException();
        }
    }
}
