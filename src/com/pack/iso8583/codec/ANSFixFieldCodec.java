package com.pack.iso8583.codec;

/**
 * Created by zhaojx on 2019/3/1.
 */

public class ANSFixFieldCodec extends AbstractFieldCodec {
    public ANSFixFieldCodec(FieldAttribute attr) {
        super(attr);
    }

    public byte[] encode(String data) {
        data = FieldUtils.makeANS(data, this.attr.maxlen);
        return FieldUtils.toBytes(data);
    }

    public String decode(byte[] data) {
        this.offset = this.attr.maxlen;
        this.mData = FieldUtils.copyOf(data, this.attr.maxlen);
        return FieldUtils.fromBytes(this.mData);
    }
}
