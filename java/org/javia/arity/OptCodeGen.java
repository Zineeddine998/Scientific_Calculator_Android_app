package org.javia.arity;

class OptCodeGen extends SimpleCodeGen {
    EvalContext context = new EvalContext();
    int intrinsicArity;
    private boolean isPercent;
    int sp;
    Complex[] stack = this.context.stackComplex;
    byte[] traceCode = new byte[1];
    double[] traceConstsIm = new double[1];
    double[] traceConstsRe = new double[1];
    Function[] traceFuncs = new Function[1];
    CompiledFunction tracer = new CompiledFunction(0, this.traceCode, this.traceConstsRe, this.traceConstsIm, this.traceFuncs);

    OptCodeGen(SyntaxException e) {
        super(e);
    }

    CompiledFunction getFun(int arity) {
        return new CompiledFunction(arity, this.code.toArray(), this.consts.getRe(), this.consts.getIm(), this.funcs.toArray());
    }

    void push(Token token) throws SyntaxException {
        byte op;
        Complex[] complexArr;
        int i;
        boolean prevWasPercent = this.isPercent;
        this.isPercent = false;
        switch (token.id) {
            case 9:
                op = (byte) 1;
                this.traceConstsRe[0] = token.value;
                this.traceConstsIm[0] = 0.0d;
                break;
            case 10:
            case 11:
                Symbol symbol = getSymbol(token);
                if (!token.isDerivative()) {
                    if (symbol.op <= (byte) 0) {
                        if (symbol.fun == null) {
                            op = (byte) 1;
                            this.traceConstsRe[0] = symbol.valueRe;
                            this.traceConstsIm[0] = symbol.valueIm;
                            break;
                        }
                        op = (byte) 2;
                        this.traceFuncs[0] = symbol.fun;
                        break;
                    }
                    op = symbol.op;
                    if (op >= (byte) 38 && op <= (byte) 42) {
                        int arg = op - 38;
                        if (arg + 1 > this.intrinsicArity) {
                            this.intrinsicArity = arg + 1;
                        }
                        complexArr = this.stack;
                        i = this.sp + 1;
                        this.sp = i;
                        complexArr[i].re = Double.NaN;
                        this.stack[this.sp].im = 0.0d;
                        this.code.push(op);
                        return;
                    }
                }
                op = (byte) 2;
                this.traceFuncs[0] = symbol.fun.getDerivative();
                break;
            default:
                op = token.vmop;
                if (op > (byte) 0) {
                    if (op == (byte) 12) {
                        this.isPercent = true;
                        break;
                    }
                }
                throw new Error("wrong vmop: " + op);
                break;
        }
        this.traceCode[0] = op;
        if (op != (byte) 8) {
            this.sp = this.tracer.execWithoutCheckComplex(this.context, this.sp, prevWasPercent ? -1 : -2);
        } else {
            complexArr = this.stack;
            i = this.sp + 1;
            this.sp = i;
            complexArr[i].re = Double.NaN;
            this.stack[this.sp].im = 0.0d;
        }
        if (!this.stack[this.sp].isNaN() || op == (byte) 1) {
            int nPopCode = op == (byte) 2 ? this.traceFuncs[0].arity() : VM.arity[op];
            while (nPopCode > 0) {
                byte pop = this.code.pop();
                if (pop == (byte) 1) {
                    this.consts.pop();
                } else if (pop == (byte) 2) {
                    nPopCode += this.funcs.pop().arity() - 1;
                } else {
                    nPopCode += VM.arity[pop];
                }
                nPopCode--;
            }
            this.consts.push(this.stack[this.sp].re, this.stack[this.sp].im);
            op = (byte) 1;
        } else if (op == (byte) 2) {
            this.funcs.push(this.traceFuncs[0]);
        }
        this.code.push(op);
    }

    void start() {
        super.start();
        this.sp = -1;
        this.intrinsicArity = 0;
        this.isPercent = false;
    }
}
