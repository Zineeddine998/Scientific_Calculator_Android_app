package org.javia.arity;

class Compiler {
    private final OptCodeGen codeGen = new OptCodeGen(this.exception);
    private final Declaration decl = new Declaration();
    private final DeclarationParser declParser = new DeclarationParser(this.exception);
    private final SyntaxException exception = new SyntaxException();
    private final Lexer lexer = new Lexer(this.exception);
    private final RPN rpn = new RPN(this.exception);
    private final SimpleCodeGen simpleCodeGen = new SimpleCodeGen(this.exception);

    Compiler() {
    }

    Function compile(Symbols symbols, String source) throws SyntaxException {
        Function fun = null;
        this.decl.parse(source, this.lexer, this.declParser);
        if (this.decl.arity == -2) {
            try {
                fun = new Constant(compileSimple(symbols, this.decl.expression).evalComplex());
            } catch (SyntaxException e) {
                if (e != SimpleCodeGen.HAS_ARGUMENTS) {
                    throw e;
                }
            }
        }
        if (fun == null) {
            symbols.pushFrame();
            symbols.addArguments(this.decl.args);
            try {
                this.rpn.setConsumer(this.codeGen.setSymbols(symbols));
                this.lexer.scan(this.decl.expression, this.rpn);
                int arity = this.decl.arity;
                if (arity == -2) {
                    arity = this.codeGen.intrinsicArity;
                }
                fun = this.codeGen.getFun(arity);
            } finally {
                symbols.popFrame();
            }
        }
        fun.comment = source;
        return fun;
    }

    Function compileSimple(Symbols symbols, String expression) throws SyntaxException {
        this.rpn.setConsumer(this.simpleCodeGen.setSymbols(symbols));
        this.lexer.scan(expression, this.rpn);
        return this.simpleCodeGen.getFun();
    }

    FunctionAndName compileWithName(Symbols symbols, String source) throws SyntaxException {
        return new FunctionAndName(compile(symbols, source), this.decl.name);
    }
}
