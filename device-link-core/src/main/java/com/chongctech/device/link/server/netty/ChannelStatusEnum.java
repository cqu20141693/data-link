package com.chongctech.device.link.server.netty;

public enum ChannelStatusEnum {
    INVALID(-1),
    INIT(0),
    VALID(1);

    private Integer code;

    ChannelStatusEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
