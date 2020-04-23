package org.javia.arity;

class FunctionStack {
    private Function[] data = new Function[8];
    private int size = 0;

    FunctionStack() {
    }

    void clear() {
        this.size = 0;
    }

    Function pop() {
        Function[] functionArr = this.data;
        int i = this.size - 1;
        this.size = i;
        return functionArr[i];
    }

    void push(Function b) {
        if (this.size >= this.data.length) {
            Function[] newData = new Function[(this.data.length << 1)];
            System.arraycopy(this.data, 0, newData, 0, this.data.length);
            this.data = newData;
        }
        Function[] functionArr = this.data;
        int i = this.size;
        this.size = i + 1;
        functionArr[i] = b;
    }

    Function[] toArray() {
        Function[] trimmed = new Function[this.size];
        System.arraycopy(this.data, 0, trimmed, 0, this.size);
        return trimmed;
    }
}
