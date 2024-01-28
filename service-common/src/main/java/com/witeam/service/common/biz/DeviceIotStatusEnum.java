package com.witeam.service.common.biz;

import java.util.HashMap;
import java.util.Map;

public enum DeviceIotStatusEnum {
    OFFLINE(0),
    ONLINE(1),
    NO_STATUS(2),
    UNKNOWN(-99);

    private final static Map<Integer, DeviceIotStatusEnum> INNER;

    static {
        INNER = new HashMap<>();
        for (DeviceIotStatusEnum deviceStatusChangeEnum : DeviceIotStatusEnum.values()) {
            INNER.put(deviceStatusChangeEnum.code, deviceStatusChangeEnum);
        }
    }

    private Integer code;


    DeviceIotStatusEnum(Integer code) {
        this.code = code;
    }

    public static DeviceIotStatusEnum parseFromCode(Integer code) {
        return INNER.getOrDefault(code, UNKNOWN);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

}
