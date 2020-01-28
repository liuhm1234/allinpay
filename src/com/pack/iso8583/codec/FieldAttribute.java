package com.pack.iso8583.codec;

/**
 * Created by zhaojx on 2019/3/1.
 */

/**
 * 8583域属性定义
 */
public class FieldAttribute {
    public FieldType type;
    public boolean compress;
    public int maxlen;
    public char align;

    public FieldAttribute() {
    }
}