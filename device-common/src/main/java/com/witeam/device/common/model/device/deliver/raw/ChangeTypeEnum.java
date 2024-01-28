package com.witeam.device.common.model.device.deliver.raw;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gow 2024/01/23
 */
public enum ChangeTypeEnum {
    LINK_CONNECTED(1, "connected"),
    LINK_DISCONNECTED(2, "disconnected"),
    LINK_HEART_BEAT(3, "linkHeartBeat"),
    LINK_ALIVE(4, "alive"),
    UNKNOWN(0, "unknown");

    private static final Map<Integer, ChangeTypeEnum> INNER = new HashMap();
    private Integer code;
    private String msg;

    private ChangeTypeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static ChangeTypeEnum parseFromCode(Integer code) {
        return (ChangeTypeEnum)INNER.getOrDefault(code, UNKNOWN);
    }

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    static {
        ChangeTypeEnum[] var0 = values();
        int var1 = var0.length;

        for(int var2 = 0; var2 < var1; ++var2) {
            ChangeTypeEnum sysLogEnum = var0[var2];
            INNER.put(sysLogEnum.code, sysLogEnum);
        }

    }
}
