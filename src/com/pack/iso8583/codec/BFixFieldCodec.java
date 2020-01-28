package com.pack.iso8583.codec;


/**
 * Created by zhaojx on 2019/3/1.
 */

public class BFixFieldCodec extends AbstractFieldCodec {
    public BFixFieldCodec(FieldAttribute attr) {
        super(attr);
        this.attr.maxlen >>= 3;
    }

    public byte[] encode(String data) {
        int length = this.attr.maxlen * 2;
        String value = null;
        if (data.length() <= length) {
            value = data;
        }
        else {
            value = data.substring(0, length);
        }

        return FieldUtils.hexString2Bytes(value);
    }

    public String decode(byte[] data) {
        this.offset = this.attr.maxlen;
        this.mData = FieldUtils.copyOf(data, this.attr.maxlen);
        return FieldUtils.bytes2HexString(this.mData);
    }
}