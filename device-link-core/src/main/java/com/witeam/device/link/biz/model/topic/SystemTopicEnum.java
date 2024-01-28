package com.witeam.device.link.biz.model.topic;

/**
 * @author witeam
 * @date 2019/10/29 10:40
 */
public enum SystemTopicEnum {

    ERROR("sys/error", "错误topic,下行");

    private final String name;
    private final String desc;

    SystemTopicEnum(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }
}
