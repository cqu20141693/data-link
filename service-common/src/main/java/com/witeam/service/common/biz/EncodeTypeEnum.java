package com.witeam.service.common.biz;



import com.witeam.service.common.util.codec.base.*;

import java.util.HashMap;
import java.util.Map;

public enum EncodeTypeEnum {
    TYPE_INT("int", new TypeIntConvert()),

    TYPE_LONG("long", new TypeLongConvert()),

    TYPE_FLOAT("float", new TypeFloatConvert()),

    TYPE_DOUBLE("double", new TypeDoubleConvert()),

    TYPE_STRING("string", new TypeStringConvert()),

    TYPE_JSON("json", new TypeJsonConvert()),

    TYPE_BIN("bin", new TypeBinConvert()),

    UNKNOWN("unknown", null);

    private final static Map<String, EncodeTypeEnum> INNER;

    static {
        INNER = new HashMap<>();
        for (EncodeTypeEnum encodeTypeEnum : EncodeTypeEnum.values()) {
            INNER.put(encodeTypeEnum.type, encodeTypeEnum);
        }
    }

    private String type;

    private AbstractTypeConvert<?> abstractTypeConvert;

    EncodeTypeEnum(String type, AbstractTypeConvert<?> abstractTypeConvert) {
        this.type = type;
        this.abstractTypeConvert = abstractTypeConvert;
    }

    public static EncodeTypeEnum parseFromType(String type) {
        return INNER.getOrDefault(type, UNKNOWN);
    }

    public String getType() {
        return type;
    }

    public AbstractTypeConvert<?> getTypeConvert() {
        return abstractTypeConvert;
    }
}
