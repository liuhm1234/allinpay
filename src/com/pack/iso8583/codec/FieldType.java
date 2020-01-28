package com.pack.iso8583.codec;

/**
 * Created by zhaojx on 2019/3/1.
 */

/**
 *8583 变量类型定义
 */
public enum FieldType {
    N_FIX,
    N_LLVAR,
    N_LLLVAR,
    ANS_FIX,
    ANS_LLVAR,
    ANS_LLLVAR,
    B_FIX,
    B_LLVAR,
    B_LLLVAR,
    Z_FIX,
    Z_LLVAR,
    Z_LLLVAR,
    UNKNOWN;
}
