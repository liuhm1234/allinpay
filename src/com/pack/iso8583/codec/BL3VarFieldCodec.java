package com.pack.iso8583.codec;


/**
 * Created by zhaojx on 2019/3/1.
 */

public class BL3VarFieldCodec extends AbstractFieldCodec {
    public BL3VarFieldCodec(FieldAttribute attr) {
        super(attr);
        this.attr.maxlen >>= 3;
    }

    public byte[] encode(String data) {
        data = FieldUtils.substr(data, this.attr.maxlen * 2);
        int dataLength = FieldUtils.bcdLength(data.length());
        byte[] field;
        byte[] len;
        byte[] val;
        if (this.attr.compress) {
            field = new byte[2 + dataLength];
            len = FieldUtils._N2Bcd(String.format("%03d", new Object[]{Integer.valueOf(dataLength)}));
            val = FieldUtils.hexString2Bytes(data);
            System.arraycopy(len, 0, field, 0, len.length);
            System.arraycopy(val, 0, field, len.length, val.length);
            return field;
        }
        else {
            field = new byte[3 + dataLength];
            len = FieldUtils.toBytes(String.format("%03d", new Object[]{Integer.valueOf(dataLength)}));
            val = FieldUtils.hexString2Bytes(data);
            System.arraycopy(len, 0, field, 0, len.length);
            System.arraycopy(val, 0, field, len.length, val.length);
            return field;
        }
    }

    public String decode(byte[] data) {
        int len;
        if (this.attr.compress) {
            len = FieldUtils.parseInt(FieldUtils.Bcd2N(FieldUtils.copyOf(data, 2)));
            this.offset = 2 + len;
            this.mData = FieldUtils.copyOfRange(data, 2, this.offset);
            return FieldUtils.isNullEmpty(this.mData) ? "" : FieldUtils.bytes2HexString(this.mData).substring(0, Math.min(len, this.attr.maxlen) * 2);
        }
        else {
            len = FieldUtils.parseInt(new String(FieldUtils.copyOf(data, 3)));
            this.offset = 3 + len;
            this.mData = FieldUtils.copyOfRange(data, 3, this.offset);
            return FieldUtils.isNullEmpty(this.mData) ? "" : FieldUtils.bytes2HexString(this.mData).substring(0, Math.min(len, this.attr.maxlen) * 2);
        }
    }
}
