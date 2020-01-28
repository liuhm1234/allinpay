package com.pack.iso8583.codec;


/**
 * Created by zhaojx on 2019/3/1.
 */

public class ANSL3VarFieldCodec extends AbstractFieldCodec {
    public ANSL3VarFieldCodec(FieldAttribute attr) {
        super(attr);
    }

    public byte[] encode(String data) {
        data = FieldUtils.substr(data, this.attr.maxlen);
        if (this.attr.compress) {
            byte[] field1 = new byte[2 + data.length()];
            byte[] len = FieldUtils._N2Bcd(String.format("%03d", new Object[]{Integer.valueOf(data.length())}));
            byte[] val = FieldUtils.toBytes(data);
            System.arraycopy(len, 0, field1, 0, len.length);
            System.arraycopy(val, 0, field1, len.length, val.length);
            return field1;
        }
        else {
            String field = String.format("%03d", new Object[]{Integer.valueOf(data.length())}) + data;
            return FieldUtils.toBytes(field);
        }
    }

    public String decode(byte[] data) {
        int len;
        if (this.attr.compress) {
            len = FieldUtils.parseInt(FieldUtils.Bcd2N(FieldUtils.copyOf(data, 2)));
            this.offset = 2 + len;
            this.mData = FieldUtils.copyOfRange(data, 2, this.offset);
            return FieldUtils.isNullEmpty(this.mData) ? "" : FieldUtils.fromBytes(this.mData).substring(0, len);
        }
        else {
            len = FieldUtils.parseInt(new String(FieldUtils.copyOf(data, 3)));
            this.offset = 3 + len;
            this.mData = FieldUtils.copyOfRange(data, 3, this.offset);
            return FieldUtils.isNullEmpty(this.mData) ? "" : FieldUtils.fromBytes(this.mData).substring(0, len);
        }
    }
}
