package org.javia.arity;

class Lexer {
    static final int ADD = 1;
    static final int CALL = 11;
    static final int COMMA = 12;
    static final int CONST = 10;
    static final int DIV = 4;
    static final int END = 15;
    private static final char END_MARKER = '$';
    static final int FACT = 8;
    static final int LPAREN = 13;
    static final int MOD = 5;
    static final int MUL = 3;
    static final int NUMBER = 9;
    static final int PERCENT = 17;
    static final int POWER = 7;
    static final int RPAREN = 14;
    static final int SQRT = 16;
    static final int SUB = 2;
    static final Token TOK_ADD = new Token(1, 4, 2, 3);
    static final Token TOK_COMMA = new Token(12, 2, 0, 0);
    static final Token TOK_CONST = new Token(10, 20, 0, 0);
    static final Token TOK_DIV = new Token(4, 5, 2, 6);
    static final Token TOK_END = new Token(15, 0, 0, 0);
    static final Token TOK_FACT = new Token(8, 8, 4, 11);
    static final Token TOK_LPAREN = new Token(13, 1, 1, 0);
    static final Token TOK_MOD = new Token(5, 5, 2, 7);
    static final Token TOK_MUL = new Token(3, 5, 2, 5);
    static final Token TOK_NUMBER = new Token(9, 20, 0, 0);
    static final Token TOK_PERCENT = new Token(17, 9, 4, 12);
    static final Token TOK_POWER = new Token(7, 7, 3, 10);
    static final Token TOK_RPAREN = new Token(14, 3, 0, 0);
    static final Token TOK_SQRT = new Token(16, 10, 1, 13);
    static final Token TOK_SUB = new Token(2, 4, 2, 4);
    static final Token TOK_UMIN = new Token(6, 6, 1, 9);
    static final int UMIN = 6;
    private static final char UNICODE_DIV = '÷';
    private static final char UNICODE_MINUS = '−';
    private static final char UNICODE_MUL = '×';
    private static final char UNICODE_SQRT = '√';
    private static final String WHITESPACE = " \n\r\t";
    private SyntaxException exception;
    private char[] input = new char[32];
    private int pos;

    Lexer(SyntaxException exception) {
        this.exception = exception;
    }

    private void init(String str) {
        int len = str.length();
        if (this.input.length < len + 1) {
            this.input = new char[(len + 1)];
        }
        str.getChars(0, len, this.input, 0);
        this.input[len] = END_MARKER;
        this.pos = 0;
    }

    Token nextToken() throws SyntaxException {
        while (WHITESPACE.indexOf(this.input[this.pos]) != -1) {
            this.pos++;
        }
        char c = this.input[this.pos];
        int begin = this.pos;
        this.pos = begin + 1;
        switch (c) {
            case '!':
                return TOK_FACT;
            case '#':
                return TOK_MOD;
            case '$':
                return TOK_END;
            case '%':
                return TOK_PERCENT;
            case '(':
                return TOK_LPAREN;
            case ')':
                return TOK_RPAREN;
            case '*':
                return TOK_MUL;
            case '+':
                return TOK_ADD;
            case ',':
                return TOK_COMMA;
            case '-':
                return TOK_SUB;
            case '/':
                return TOK_DIV;
            default:
                int p = this.pos;
                int p2;
                if (('0' <= c && c <= '9') || c == '.') {
                    if (c == '0') {
                        char cc = Character.toLowerCase(this.input[p]);
                        int base = cc == 'x' ? 16 : cc == 'b' ? 2 : cc == 'o' ? 8 : 0;
                        if (base > 0) {
                            String coded;
                            p++;
                            while (true) {
                                p2 = p + 1;
                                c = this.input[p];
                                if (('a' > c || c > 'z') && (('A' > c || c > 'Z') && ('0' > c || c > '9'))) {
                                    coded = String.valueOf(this.input, begin + 2, (p2 - 3) - begin);
                                    this.pos = p2 - 1;
                                } else {
                                    p = p2;
                                }
                            }
                            coded = String.valueOf(this.input, begin + 2, (p2 - 3) - begin);
                            this.pos = p2 - 1;
                            try {
                                return TOK_NUMBER.setValue((double) Integer.parseInt(coded, base));
                            } catch (NumberFormatException e) {
                                throw this.exception.set("invalid number '" + String.valueOf(this.input, begin, (p2 - 1) - begin) + "'", begin);
                            }
                        }
                    }
                    while (true) {
                        if (('0' <= c && c <= '9') || c == '.' || c == 'E' || c == 'e') {
                            if ((c == 'E' || c == 'e') && (this.input[p] == '-' || this.input[p] == UNICODE_MINUS)) {
                                this.input[p] = '-';
                                p++;
                            }
                            p2 = p + 1;
                            c = this.input[p];
                            p = p2;
                        } else {
                            this.pos = p - 1;
                            String nbStr = String.valueOf(this.input, begin, (p - 1) - begin);
                            try {
                                if (nbStr.equals(".")) {
                                    return TOK_NUMBER.setValue(0.0d);
                                }
                                return TOK_NUMBER.setValue(Double.parseDouble(nbStr));
                            } catch (NumberFormatException e2) {
                                throw this.exception.set("invalid number '" + nbStr + "'", begin);
                            }
                        }
                    }
                } else if (('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z')) {
                    while (true) {
                        p2 = p + 1;
                        c = this.input[p];
                        if (('a' <= c && c <= 'z') || (('A' <= c && c <= 'Z') || ('0' <= c && c <= '9'))) {
                            p = p2;
                        }
                    }
                    if (c == '\'') {
                        p = p2 + 1;
                        c = this.input[p2];
                    } else {
                        p = p2;
                    }
                    String nameValue = String.valueOf(this.input, begin, (p - 1) - begin);
                    while (WHITESPACE.indexOf(c) != -1) {
                        p2 = p + 1;
                        c = this.input[p];
                        p = p2;
                    }
                    if (c == '(') {
                        this.pos = p;
                        return new Token(11, 0, 1, 0).setAlpha(nameValue);
                    }
                    this.pos = p - 1;
                    return TOK_CONST.setAlpha(nameValue);
                } else if ((c >= 913 && c <= 937) || ((c >= 945 && c <= 969) || c == 8734)) {
                    return TOK_CONST.setAlpha("" + c);
                } else {
                    switch (c) {
                        case '^':
                            return TOK_POWER;
                        case 215:
                            return TOK_MUL;
                        case 247:
                            return TOK_DIV;
                        case 8722:
                            return TOK_SUB;
                        case 8730:
                            return TOK_SQRT;
                        default:
                            throw this.exception.set("invalid character '" + c + "'", begin);
                    }
                }
                break;
        }
    }

    void scan(String str, TokenConsumer consumer) throws SyntaxException {
        this.exception.expression = str;
        if (str.indexOf(36) != -1) {
            throw this.exception.set("Invalid character '$'", str.indexOf(36));
        }
        init(str);
        consumer.start();
        Token token;
        do {
            int savePos = this.pos;
            token = nextToken();
            token.position = savePos;
            consumer.push(token);
        } while (token != TOK_END);
    }
}
