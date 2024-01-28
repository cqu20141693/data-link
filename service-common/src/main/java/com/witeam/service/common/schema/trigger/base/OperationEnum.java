package com.witeam.service.common.schema.trigger.base;

import java.util.HashMap;
import java.util.Map;

public enum OperationEnum {
    EQ("EQ", "=="),
    NE("NE", "!="),
    LT("LT", "<"),
    LTE("LTE", "<="),
    GT("GT", ">"),
    GTE("GTE", ">="),
    UNKNOWN("UNKNOWN", "");

    private final static Map<String, OperationEnum> INNER;

    static {
        INNER = new HashMap<>();
        for (OperationEnum t : OperationEnum.values()) {
            INNER.put(t.type, t);
        }
    }

    private String type;

    private String code;


    OperationEnum(String type, String code) {
        this.type = type;
        this.code = code;
    }

    public static OperationEnum parseFromType(String type) {
        if (type == null) {
            return UNKNOWN;
        }
        return INNER.getOrDefault(type.toLowerCase(), UNKNOWN);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }
}
