package com.witeam.service.common.schema.trigger.base;

import java.util.HashMap;
import java.util.Map;

public enum ConditionTypeEnum {
    NESTED("NESTED"),
    DATA("DATA"),
    WEATHER("WEATHER"),
    UNKNOWN("UNKNOWN");

    private final static Map<String, ConditionTypeEnum> INNER;

    static {
        INNER = new HashMap<>();
        for (ConditionTypeEnum t : ConditionTypeEnum.values()) {
            INNER.put(t.type, t);
        }
    }

    private String type;

    ConditionTypeEnum(String type) {
        this.type = type;
    }

    public static ConditionTypeEnum parseFromType(String type) {
        return INNER.getOrDefault(type, UNKNOWN);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
