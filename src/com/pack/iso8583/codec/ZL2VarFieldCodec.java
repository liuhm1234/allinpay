package com.pack.iso8583.codec;


/**
 * Created by zhaojx on 2019/3/1.
 */

public class ZL2VarFieldCodec extends AbstractFieldCodec {
    public ZL2VarFieldCodec(FieldAttribute attr) {
        super(attr);
    }

    public byte[] encode(String data) {
        data = FieldUtils.substr(data, this.attr.maxlen);
        if(this.attr.compress) {
            int field2 = FieldUtils.bcdLength(data.length());
            byte[] field1 = new byte[1 + field2];
            byte[] len = FieldUtils._N2Bcd(String.format("%02d", new Object[]{Integer.valueOf(data.length())}));
            byte[] val = FieldUtils.Ascii2Bcd(data);
            System.arraycopy(len, 0, field1, 0, len.length);
            System.arraycopy(val, 0, field1, len.length, val.length);
            return field1;
        } else {
            String field = String.format("%02d", new Object[]{Integer.valueOf(data.length())}) + data;
            return FieldUtils.Ascii2Bcd(field);
        }
    }

    public String decode(byte[] data) {
        int len;
        if(this.attr.compress) {
            len = FieldUtils.bcdLength(FieldUtils.parseInt(FieldUtils.Bcd2N(FieldUtils.copyOf(data, 1))));
            this.offset = 1 + len;
            this.mData = FieldUtils.copyOfRange(data, 1, this.offset);
            return FieldUtils.isNullEmpty(this.mData)?"":FieldUtils.Bcd2Ascii(this.mData);
        } else {
            len = FieldUtils.parseInt(new String(FieldUtils.copyOf(data, 2)));
            this.offset = 2 + len;
            this.mData = FieldUtils.copyOfRange(data, 2, this.offset);
            return FieldUtils.isNullEmpty(this.mData)?"":FieldUtils.Bcd2Ascii(this.mData);
        }
    }
}

