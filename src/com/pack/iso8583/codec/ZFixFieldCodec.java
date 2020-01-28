package com.pack.iso8583.codec;


/**
 * Created by zhaojx on 2019/3/1.
 */

public class ZFixFieldCodec extends AbstractFieldCodec {
    public ZFixFieldCodec(FieldAttribute attr) {
        super(attr);
    }

    public byte[] encode(String data) {
        data = FieldUtils.makeN(data, this.attr.maxlen);
        return this.attr.compress?(this.attr.align == 76?FieldUtils.Ascii2Bcd(data):FieldUtils._Ascii2Bcd(data)):FieldUtils.toBytes(data);
    }

    public String decode(byte[] data) {
        if(this.attr.compress) {
            this.offset = FieldUtils.bcdLength(this.attr.maxlen);
            this.mData = FieldUtils.copyOf(data, this.offset);
            if(FieldUtils.isNullEmpty(this.mData)) {
                return "";
            } else {
                String val = FieldUtils.Bcd2Ascii(this.mData);
                return this.attr.align == 76?val.substring(0, this.attr.maxlen):val.substring(this.offset * 2 - this.attr.maxlen);
            }
        } else {
            this.offset = this.attr.maxlen;
            this.mData = FieldUtils.copyOf(data, this.offset);
            return FieldUtils.isNullEmpty(this.mData)?"":FieldUtils.fromBytes(this.mData);
        }
    }
}

