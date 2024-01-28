package com.witeam.service.common.biz;

import java.util.HashMap;
import java.util.Map;

public enum DeviceTypeEnum {
    APP(-1),
    COMMON(0),
    GATEWAY(1), //网关设备
    STATEFUL_SUB_DEVICE(2),
    STATELESS_SUB_DEVICE(3),
    REPEATER(4),  //中继器
    VIRTUAL_DEVICE(16), //虚拟设备
    UNKNOWN(-99);

    private final static Map<Integer, DeviceTypeEnum> INNER;

    static {
        INNER = new HashMap<>();
        for (DeviceTypeEnum t : DeviceTypeEnum.values()) {
            INNER.put(t.code, t);
        }
    }

    private Integer code;


    DeviceTypeEnum(Integer code) {
        this.code = code;
    }

    public static DeviceTypeEnum parseFromCode(Integer code) {
        return INNER.getOrDefault(code, UNKNOWN);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
