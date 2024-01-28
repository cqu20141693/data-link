package com.witeam.service.common.device.deliver.raw;

import java.util.HashMap;
import java.util.Map;

public enum ChangeType {
    LINK_CONNECTED(1, "connected"),
    LINK_DISCONNECTED(2, "disconnected"),
    LINK_HEART_BEAT(3, "linkHeartBeat"),
    UNKNOWN(0, "unknown");

    private final static Map<Integer, ChangeType> INNER;

    static {
        INNER = new HashMap<>();
        for (ChangeType sysLogEnum : ChangeType.values()) {
            INNER.put(sysLogEnum.code, sysLogEnum);
        }
    }

    private Integer code;

    private String msg;

    ChangeType(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static ChangeType parseFromCode(Integer code) {
        return INNER.getOrDefault(code, UNKNOWN);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
