package com.witeam.service.common.schema.trigger.base;

import java.util.HashMap;
import java.util.Map;

public enum ConjunctionTypeEnum {
    AND("AND"),
    OR("OR"),
    UNKNOWN("NULL");

    private final static Map<String, ConjunctionTypeEnum> INNER;

    static {
        INNER = new HashMap<>();
        for (ConjunctionTypeEnum t : ConjunctionTypeEnum.values()) {
            INNER.put(t.code, t);
        }
    }

    private String code;


    ConjunctionTypeEnum(String code) {
        this.code = code;
    }

    public static ConjunctionTypeEnum parseFromCode(String code) {
        if (code == null) {
            return UNKNOWN;
        }
        return INNER.getOrDefault(code.toLowerCase(), UNKNOWN);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
