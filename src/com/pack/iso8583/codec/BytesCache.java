package com.pack.iso8583.codec;

import java.util.Arrays;

public class BytesCache {
    private static final int BUFF_SIZE = 1024;
    private int capacity;
    private int offset;
    private byte[] buffer;

    public BytesCache(int size) {
        this.offset = 0;
        this.capacity = size > 0 ? size : 1024;
        this.buffer = new byte[this.capacity];
        Arrays.fill(this.buffer, (byte)0);
    }

    public BytesCache() {
        this(1024);
    }

    public BytesCache write(byte[] data) {
        if (data == null) {
            return this;
        }
        else {
            if (data.length + this.offset >= this.capacity) {
                this.capacity += data.length >= 1024 ? data.length + 512 : 1024;
                byte[] newbuff = new byte[this.capacity];
                Arrays.fill(newbuff, (byte)0);
                System.arraycopy(this.buffer, 0, newbuff, 0, this.offset);
                System.arraycopy(data, 0, newbuff, this.offset, data.length);
                this.offset += data.length;
                this.buffer = newbuff;
            }
            else {
                System.arraycopy(data, 0, this.buffer, this.offset, data.length);
                this.offset += data.length;
            }

            return this;
        }
    }

    public void clear() {
        this.offset = 0;
        this.buffer = new byte[1024];
        Arrays.fill(this.buffer, (byte)0);
    }

    public byte[] getBytes() {
        return Arrays.copyOf(this.buffer, this.offset);
    }
}