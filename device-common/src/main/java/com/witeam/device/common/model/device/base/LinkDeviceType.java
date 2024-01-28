package com.witeam.device.common.model.device.base;


import java.util.HashMap;
import java.util.Map;
/**
 * @author gow 2024/01/23
 */
public enum LinkDeviceType {
    SUB_GROUP(-3),
    MIRROR(-2),
    APP(-1),
    COMMON(0),
    GATEWAY(1),
    UNKNOWN(99);

    private static final Map<Integer, LinkDeviceType> INNER = new HashMap<>();
    private Integer code;

    private LinkDeviceType(Integer code) {
        this.code = code;
    }

    public static LinkDeviceType parseFromCode(Integer code) {
        return code == null ? UNKNOWN : (LinkDeviceType)INNER.getOrDefault(code, UNKNOWN);
    }

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    static {
       LinkDeviceType[] var0 = values();
        int var1 = var0.length;

        for(int var2 = 0; var2 < var1; ++var2) {
            LinkDeviceType value = var0[var2];
            INNER.put(value.code, value);
        }

    }
}
