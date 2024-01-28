package com.witeam.service.common.biz;

import java.util.HashMap;
import java.util.Map;

public enum DeviceProxyEnum {
    DATA(1),
    LOGICAL(2),
    UNKNOWN(-99);

    private final static Map<Integer, DeviceProxyEnum> INNER;

    static {
        INNER = new HashMap<>();
        for (DeviceProxyEnum t : DeviceProxyEnum.values()) {
            INNER.put(t.code, t);
        }
    }

    private Integer code;


    DeviceProxyEnum(Integer code) {
        this.code = code;
    }

    public static DeviceProxyEnum parseFromCode(Integer code) {
        return INNER.getOrDefault(code, UNKNOWN);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
