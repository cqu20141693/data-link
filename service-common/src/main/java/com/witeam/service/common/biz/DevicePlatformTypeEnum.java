package com.witeam.service.common.biz;

import java.util.HashMap;
import java.util.Map;

public enum DevicePlatformTypeEnum {
    SELF("self"),
    ALIYUN("aliyun"), //阿里云
    XIAOMA("xiaoma"),
    LECHEN("lechen"), //乐橙，接摄像头
    HY("hy"); //瀚 云

    private final static Map<String, DevicePlatformTypeEnum> INNER;

    static {
        INNER = new HashMap<>();
        for (DevicePlatformTypeEnum t : DevicePlatformTypeEnum.values()) {
            INNER.put(t.code, t);
        }
    }

    private String code;


    DevicePlatformTypeEnum(String code) {
        this.code = code;
    }

    public static DevicePlatformTypeEnum parseFromCode(String code) {
        return INNER.getOrDefault(code, SELF);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
