package org.javia.arity;

class SimpleCodeGen extends TokenConsumer {
    static final SyntaxException HAS_ARGUMENTS = new SyntaxException();
    ByteStack code = new ByteStack();
    DoubleStack consts = new DoubleStack();
    SyntaxException exception;
    FunctionStack funcs = new FunctionStack();
    Symbols symbols;

    SimpleCodeGen(SyntaxException exception) {
        this.exception = exception;
    }

    CompiledFunction getFun() {
        return new CompiledFunction(0, this.code.toArray(), this.consts.getRe(), this.consts.getIm(), this.funcs.toArray());
    }

    Symbol getSymbol(Token token) throws SyntaxException {
        String name = token.name;
        boolean isDerivative = token.isDerivative();
        if (isDerivative) {
            if (token.arity == 1) {
                name = name.substring(0, name.length() - 1);
            } else {
                throw this.exception.set("Derivative expects arity 1 but found " + token.arity, token.position);
            }
        }
        Symbol symbol = this.symbols.lookup(name, token.arity);
        if (symbol == null) {
            throw this.exception.set("undefined '" + name + "' with arity " + token.arity, token.position);
        }
        if (isDerivative && symbol.op > (byte) 0 && symbol.fun == null) {
            symbol.fun = CompiledFunction.makeOpFunction(symbol.op);
        }
        if (!isDerivative || symbol.fun != null) {
            return symbol;
        }
        throw this.exception.set("Invalid derivative " + name, token.position);
    }

    void push(Token token) throws SyntaxException {
        byte op;
        switch (token.id) {
            case 9:
                op = (byte) 1;
                this.consts.push(token.value, 0.0d);
                break;
            case 10:
            case 11:
                Symbol symbol = getSymbol(token);
                if (!token.isDerivative()) {
                    if (symbol.op <= (byte) 0) {
                        if (symbol.fun == null) {
                            op = (byte) 1;
                            this.consts.push(symbol.valueRe, symbol.valueIm);
                            break;
                        }
                        op = (byte) 2;
                        this.funcs.push(symbol.fun);
                        break;
                    }
                    op = symbol.op;
                    if (op >= (byte) 38 && op <= (byte) 42) {
                        throw HAS_ARGUMENTS.set("eval() on implicit function", this.exception.position);
                    }
                }
                op = (byte) 2;
                this.funcs.push(symbol.fun.getDerivative());
                break;
            default:
                op = token.vmop;
                if (op <= (byte) 0) {
                    throw new Error("wrong vmop: " + op + ", id " + token.id + " in \"" + this.exception.expression + '\"');
                }
                break;
        }
        this.code.push(op);
    }

    SimpleCodeGen setSymbols(Symbols symbols) {
        this.symbols = symbols;
        return this;
    }

    void start() {
        this.code.clear();
        this.consts.clear();
        this.funcs.clear();
    }
}
