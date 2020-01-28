package com.pack.iso8583.codec;

import java.util.Arrays;

/**
 * Created by zhaojx on 2019/3/1.
 */

public class NL2VarFieldCodec extends AbstractFieldCodec {
    public NL2VarFieldCodec(FieldAttribute attr) {
        super(attr);
    }

    public byte[] encode(String data) {
        data = FieldUtils.substr(data, this.attr.maxlen);
        if (this.attr.compress) {
            byte[] field1 = new byte[1 + FieldUtils.bcdLength(data.length())];
            Arrays.fill(field1, (byte)0);
            byte[] len = FieldUtils._N2Bcd(String.format("%02d", new Object[]{Integer.valueOf(data.length())}));
            byte[] val = this.attr.align == 76 ? FieldUtils.N2Bcd_(data) : FieldUtils._N2Bcd(data);
            System.arraycopy(len, 0, field1, 0, len.length);
            System.arraycopy(val, 0, field1, len.length, val.length);
            return field1;
        }
        else {
            String field = String.format("%02d", new Object[]{Integer.valueOf(data.length())}) + data;
            return FieldUtils.toBytes(field);
        }
    }

    public String decode(byte[] data) {
        int len;
        if (this.attr.compress) {
            len = FieldUtils.parseInt(FieldUtils.Bcd2N(FieldUtils.copyOf(data, 1)));
            int bcdLen = FieldUtils.bcdLength(len);
            this.offset = 1 + bcdLen;
            this.mData = FieldUtils.copyOfRange(data, 1, this.offset);
            if (FieldUtils.isNullEmpty(this.mData)) {
                return "";
            }
            else {
                String val = FieldUtils.Bcd2N(this.mData);
                return this.attr.align == 76 ? val.substring(0, len) : val.substring(bcdLen * 2 - len);
            }
        }
        else {
            len = FieldUtils.parseInt(new String(FieldUtils.copyOf(data, 2)));
            this.offset = 2 + len;
            this.mData = FieldUtils.copyOfRange(data, 2, this.offset);
            return FieldUtils.isNullEmpty(this.mData) ? "" : FieldUtils.fromBytes(this.mData).substring(0, len);
        }
    }
}

