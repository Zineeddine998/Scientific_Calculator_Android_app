package org.javia.arity;

class ByteStack {
    private byte[] data = new byte[8];
    private int size = 0;

    ByteStack() {
    }

    void clear() {
        this.size = 0;
    }

    byte pop() {
        byte[] bArr = this.data;
        int i = this.size - 1;
        this.size = i;
        return bArr[i];
    }

    void push(byte b) {
        if (this.size >= this.data.length) {
            byte[] newData = new byte[(this.data.length << 1)];
            System.arraycopy(this.data, 0, newData, 0, this.data.length);
            this.data = newData;
        }
        byte[] bArr = this.data;
        int i = this.size;
        this.size = i + 1;
        bArr[i] = b;
    }

    byte[] toArray() {
        byte[] trimmed = new byte[this.size];
        System.arraycopy(this.data, 0, trimmed, 0, this.size);
        return trimmed;
    }
}
