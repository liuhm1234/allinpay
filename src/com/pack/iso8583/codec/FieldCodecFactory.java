package com.pack.iso8583.codec;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhaojx on 2019/3/1.
 */

public class FieldCodecFactory {
    @SuppressWarnings("unchecked")
	private static Map<String, IFieldCodec> sCodecFlyweight = new HashMap();

    private FieldCodecFactory() {
    }

    private static IFieldCodec lookupFieldCodec(FieldAttribute attr) {
        String id = makeAttributeID(attr);
        return !sCodecFlyweight.containsKey(id)?null:(IFieldCodec)sCodecFlyweight.get(id);
    }

    private static String makeAttributeID(FieldAttribute attr) {
        return "" + attr.type + attr.maxlen + attr.align + attr.compress;
    }

    private static IFieldCodec createCodec(FieldAttribute attr) {
        switch(attr.type) {
            case N_FIX:
                return new NFixFieldCodec(attr);
            case N_LLVAR:
                return new NL2VarFieldCodec(attr);
            case N_LLLVAR:
                return new NL3VarFieldCodec(attr);
            case ANS_FIX:
                return new ANSFixFieldCodec(attr);
            case ANS_LLVAR:
                return new ANSL2VarFieldCodec(attr);
            case ANS_LLLVAR:
                return new ANSL3VarFieldCodec(attr);
            case B_FIX:
                return new BFixFieldCodec(attr);
            case B_LLVAR:
                return new BL2VarFieldCodec(attr);
            case B_LLLVAR:
                return new BL3VarFieldCodec(attr);
            case Z_FIX:
                return new ZFixFieldCodec(attr);
            case Z_LLVAR:
                return new ZL2VarFieldCodec(attr);
            case Z_LLLVAR:
                return new ZL3VarFieldCodec(attr);
            default:
                return null;
        }
    }

    public static IFieldCodec createFieldCodec(FieldAttribute attr) {
    	IFieldCodec codec = lookupFieldCodec(attr);
        if(codec == null) {
            codec = createCodec(attr);
            sCodecFlyweight.put(makeAttributeID(attr), codec);
        }

        return codec;
    }
}
