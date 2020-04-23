package org.javia.arity;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;

public class Symbols {
    private static final Symbol[] builtin;
    private static final String[] defines = new String[]{"log(x)=ln(x)*0.43429448190325182765", "log10(x)=log(x)", "lg(x)=log(x)", "log2(x)=ln(x)*1.4426950408889634074", "lb(x)=log2(x)", "log(base,x)=ln(x)/ln(base)", "gamma(x)=(x-1)!", "deg=0.017453292519943295", "indeg=57.29577951308232", "sind(x)=sin(x deg)", "cosd(x)=cos(x deg)", "tand(x)=tan(x deg)", "asind(x)=asin(x) indeg", "acosd(x)=acos(x) indeg", "atand(x)=atan(x) indeg", "tg(x)=tan(x)", "tgd(x)=tand(x)", "rnd(max)=rnd()*max", "re(x)=real(x)", "im(x)=imag(x)"};
    private static Symbol shell = new Symbol(null, 0.0d, false);
    private final Compiler compiler = new Compiler();
    private HashSet<Symbol> delta = null;
    private Stack<HashSet<Symbol>> frames = new Stack();
    private Hashtable<Symbol, Symbol> symbols = new Hashtable();

    static {
        byte i;
        Vector<Symbol> vect = new Vector();
        for (byte i2 : VM.builtins) {
            vect.addElement(Symbol.makeVmOp(VM.opcodeName[i2], i2));
        }
        String[] IMPLICIT_ARGS = new String[]{"x", "y", "z"};
        for (i2 = (byte) 0; i2 < IMPLICIT_ARGS.length; i2 = (byte) (i2 + 1)) {
            vect.addElement(Symbol.makeArg(IMPLICIT_ARGS[i2], i2));
        }
        vect.addElement(new Symbol("pi", 3.141592653589793d, true));
        vect.addElement(new Symbol("π", 3.141592653589793d, true));
        vect.addElement(new Symbol("e", 2.718281828459045d, true));
        vect.addElement(new Symbol("Infinity", Double.POSITIVE_INFINITY, true));
        vect.addElement(new Symbol("infinity", Double.POSITIVE_INFINITY, true));
        vect.addElement(new Symbol("Inf", Double.POSITIVE_INFINITY, true));
        vect.addElement(new Symbol("inf", Double.POSITIVE_INFINITY, true));
        vect.addElement(new Symbol("∞", Double.POSITIVE_INFINITY, true));
        vect.addElement(new Symbol("NaN", Double.NaN, true));
        vect.addElement(new Symbol("nan", Double.NaN, true));
        vect.addElement(new Symbol("i", 0.0d, 1.0d, true));
        vect.addElement(new Symbol("j", 0.0d, 1.0d, false));
        builtin = new Symbol[vect.size()];
        vect.copyInto(builtin);
    }

    public Symbols() {
        for (Symbol add : builtin) {
            add(add);
        }
        int i = 0;
        while (i < defines.length) {
            try {
                define(compileWithName(defines[i]));
                i++;
            } catch (SyntaxException e) {
                throw new Error("" + e);
            }
        }
    }

    public static boolean isDefinition(String source) {
        return source.indexOf(61) != -1;
    }

    void add(Symbol s) {
        Symbol previous = (Symbol) this.symbols.put(s, s);
        if (previous == null || !previous.isConst) {
            if (this.delta == null) {
                this.delta = new HashSet();
            }
            if (!this.delta.contains(s)) {
                HashSet hashSet = this.delta;
                if (previous == null) {
                    previous = Symbol.newEmpty(s);
                }
                hashSet.add(previous);
                return;
            }
            return;
        }
        this.symbols.put(previous, previous);
    }

    void addArguments(String[] args) {
        for (int i = 0; i < args.length; i++) {
            add(Symbol.makeArg(args[i], i));
        }
    }

    public synchronized Function compile(String source) throws SyntaxException {
        return this.compiler.compile(this, source);
    }

    public synchronized FunctionAndName compileWithName(String source) throws SyntaxException {
        return this.compiler.compileWithName(this, source);
    }

    public synchronized void define(String name, double value) {
        add(new Symbol(name, value, 0.0d, false));
    }

    public synchronized void define(String name, Complex value) {
        add(new Symbol(name, value.re, value.im, false));
    }

    public synchronized void define(String name, Function function) {
        if (function instanceof Constant) {
            define(name, function.eval());
        } else {
            add(new Symbol(name, function));
        }
    }

    public synchronized void define(FunctionAndName funAndName) {
        if (funAndName.name != null) {
            define(funAndName.name, funAndName.function);
        }
    }

    public synchronized double eval(String expression) throws SyntaxException {
        return this.compiler.compileSimple(this, expression).eval();
    }

    public synchronized Complex evalComplex(String expression) throws SyntaxException {
        return this.compiler.compileSimple(this, expression).evalComplex();
    }

    public Symbol[] getAllSymbols() {
        Symbol[] ret = new Symbol[this.symbols.size()];
        this.symbols.keySet().toArray(ret);
        return ret;
    }

    public String[] getDictionary() {
        Symbol[] syms = getAllSymbols();
        int size = syms.length;
        String[] strings = new String[size];
        for (int i = 0; i < size; i++) {
            strings[i] = syms[i].getName();
        }
        return strings;
    }

    public Symbol[] getTopFrame() {
        return this.delta == null ? new Symbol[0] : (Symbol[]) this.delta.toArray(new Symbol[0]);
    }

    synchronized Symbol lookup(String name, int arity) {
        return (Symbol) this.symbols.get(shell.setKey(name, arity));
    }

    Symbol lookupConst(String name) {
        return lookup(name, -3);
    }

    public synchronized void popFrame() {
        if (this.delta != null) {
            Iterator i$ = this.delta.iterator();
            while (i$.hasNext()) {
                Symbol previous = (Symbol) i$.next();
                if (previous.isEmpty()) {
                    this.symbols.remove(previous);
                } else {
                    this.symbols.put(previous, previous);
                }
            }
        }
        this.delta = (HashSet) this.frames.pop();
    }

    public synchronized void pushFrame() {
        this.frames.push(this.delta);
        this.delta = null;
    }

    public void remove(String name, Function function) {
        if (function instanceof Constant) {
            remove(new Symbol(name, function.eval(), 0.0d, false));
            return;
        }
        remove(new Symbol(name, function));
    }

    public void remove(FunctionAndName funAndName) {
        if (funAndName.name != null) {
            remove(funAndName.name, funAndName.function);
        }
    }

    void remove(Symbol s) {
        this.symbols.remove(s);
        if (this.delta != null) {
            this.delta.remove(s);
        }
    }

    public void reset() {
        this.delta = null;
        this.symbols.clear();
        this.frames.clear();
    }
}
