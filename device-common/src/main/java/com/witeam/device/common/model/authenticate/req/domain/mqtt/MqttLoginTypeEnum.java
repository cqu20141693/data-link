package com.witeam.device.common.model.authenticate.req.domain.mqtt;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gow 2024/01/23
 */

public enum MqttLoginTypeEnum {
    SUB_GROUP_LOGIN("SG"),
    APP_LOGIN("A"),
    MIRROR_LOGIN("M"),
    GROUP_LOGIN("G"),
    DEVICE_LOGIN("D"),
    GROUP_LOGIN_HmacSHA256("G-HmacSHA256"),
    GROUP_LOGIN_HmacSM3("G-HmacSM3"),
    DEVICE_LOGIN_HmacSHA256("D-HmacSHA256"),
    DEVICE_LOGIN_HmacSM3("D-HmacSM3"),
    GROUP_CRYPTO_LOGIN_HmacSHA256("GC-HmacSHA256"),
    GROUP_CRYPTO_LOGIN_HmacSM3("GC-HmacSM3"),
    DEVICE_CRYPTO_LOGIN_HmacSHA256("DC-HmacSHA256"),
    DEVICE_CRYPTO_LOGIN_HmacSM3("DC-HmacSM3"),
    UNKNOWN("");

    private static final Map<String, MqttLoginTypeEnum> INNER = new HashMap();
    private String tag;

    private MqttLoginTypeEnum(String tag) {
        this.tag = tag;
    }

    public static MqttLoginTypeEnum parseFromTag(String tag) {
        return (MqttLoginTypeEnum)INNER.getOrDefault(tag, UNKNOWN);
    }

    public String getTag() {
        return this.tag;
    }

    static {
        MqttLoginTypeEnum[] var0 = values();
        int var1 = var0.length;

        for(int var2 = 0; var2 < var1; ++var2) {
            MqttLoginTypeEnum mqttLoginTypeEnum = var0[var2];
            INNER.put(mqttLoginTypeEnum.tag, mqttLoginTypeEnum);
        }

    }
}

