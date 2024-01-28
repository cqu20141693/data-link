package com.witeam.service.common.biz;

import java.util.HashMap;
import java.util.Map;

public enum DataStatusEnum {
    INVALID(-1),
    INIT(0),
    USE(1),
    MODIFYING(2),
    UNKNOWN(-99);

    private final static Map<Integer, DataStatusEnum> INNER;

    static {
        INNER = new HashMap<>();
        for (DataStatusEnum t : DataStatusEnum.values()) {
            INNER.put(t.code, t);
        }
    }

    private Integer code;


    DataStatusEnum(Integer code) {
        this.code = code;
    }

    public static DataStatusEnum parseFromCode(Integer code) {
        return INNER.getOrDefault(code, UNKNOWN);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
