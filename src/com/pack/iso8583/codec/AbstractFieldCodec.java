package com.pack.iso8583.codec;

/**
 * Created by zhaojx on 2019/3/1.
 */

public abstract class AbstractFieldCodec implements IFieldCodec {

    protected FieldAttribute attr;
    protected int offset;
    protected byte[] mData;

    public AbstractFieldCodec(FieldAttribute attr) {
        this.attr = attr;
    }

    public int offset() {
        return this.offset;
    }

    public byte[] getBytes() {
        return this.mData;
    }
}
