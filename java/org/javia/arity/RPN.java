package org.javia.arity;

import java.util.Stack;

class RPN extends TokenConsumer {
    TokenConsumer consumer;
    SyntaxException exception;
    int prevTokenId = 0;
    Stack<Token> stack = new Stack();

    RPN(SyntaxException exception) {
        this.exception = exception;
    }

    static final boolean isOperand(int id) {
        return id == 8 || id == 14 || id == 9 || id == 10 || id == 17;
    }

    private void popHigher(int priority) throws SyntaxException {
        Token t = top();
        while (t != null && t.priority >= priority) {
            this.consumer.push(t);
            this.stack.pop();
            t = top();
        }
    }

    private Token top() {
        return this.stack.empty() ? null : (Token) this.stack.peek();
    }

    void push(Token token) throws SyntaxException {
        int i = 1;
        int priority = token.priority;
        int id = token.id;
        Token t;
        switch (id) {
            case 9:
            case 10:
                if (isOperand(this.prevTokenId)) {
                    push(Lexer.TOK_MUL);
                }
                this.consumer.push(token);
                break;
            case 12:
                if (isOperand(this.prevTokenId)) {
                    popHigher(priority);
                    t = top();
                    if (t != null && t.id == 11) {
                        t.arity++;
                        break;
                    }
                    throw this.exception.set("COMMA not inside CALL", token.position);
                }
                throw this.exception.set("misplaced COMMA", token.position);
                break;
            case 14:
                if (this.prevTokenId == 11) {
                    Token top = top();
                    top.arity--;
                } else if (!isOperand(this.prevTokenId)) {
                    throw this.exception.set("unexpected ) or END", token.position);
                }
                popHigher(priority);
                t = top();
                if (t != null) {
                    if (t.id == 11) {
                        this.consumer.push(t);
                    } else if (t != Lexer.TOK_LPAREN) {
                        throw this.exception.set("expected LPAREN or CALL", token.position);
                    }
                    this.stack.pop();
                    break;
                }
                break;
            case 15:
                t = Lexer.TOK_RPAREN;
                t.position = token.position;
                do {
                    push(t);
                } while (top() != null);
                break;
            default:
                if (token.assoc != 1) {
                    if (isOperand(this.prevTokenId)) {
                        if (token.assoc != 3) {
                            i = 0;
                        }
                        popHigher(i + priority);
                        this.stack.push(token);
                        break;
                    } else if (id == 2) {
                        token = Lexer.TOK_UMIN;
                        this.stack.push(token);
                        break;
                    } else if (id != 1) {
                        throw this.exception.set("operator without operand", token.position);
                    } else {
                        return;
                    }
                }
                if (isOperand(this.prevTokenId)) {
                    push(Lexer.TOK_MUL);
                }
                this.stack.push(token);
                break;
        }
        this.prevTokenId = token.id;
    }

    void setConsumer(TokenConsumer consumer) {
        this.consumer = consumer;
    }

    void start() {
        this.stack.removeAllElements();
        this.prevTokenId = 0;
        this.consumer.start();
    }
}
