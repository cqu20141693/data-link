package com.witeam.service.common.biz;

import java.util.HashMap;
import java.util.Map;

public enum GroupTypeEnum {
    /**
     * 普通组
     */
    COMMON(1),
    /**
     * 虚拟组
     */
    VIRTUAL(2),
    
    UNKNOWN(-99);

    private final static Map<Integer, GroupTypeEnum> INNER;

    static {
        INNER = new HashMap<>();
        for (GroupTypeEnum t : GroupTypeEnum.values()) {
            INNER.put(t.code, t);
        }
    }

    private Integer code;


    GroupTypeEnum(Integer code) {
        this.code = code;
    }

    public static GroupTypeEnum parseFromCode(Integer code) {
        return INNER.getOrDefault(code, UNKNOWN);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

}
