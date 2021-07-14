package com.chongctech.device.link.biz.model.link;

import java.util.HashMap;
import java.util.Map;

public enum ChannelAuth {
    ONLY_PUSH("push", 1),

    ONLY_SUBSCRIBE("subscribe", 2),

    PUSH_AND_SUBSCRIBE("push_subscribe", 3),

    UNKNOWN("", -999);

    private final static Map<Integer, ChannelAuth> INNER;

    static {
        INNER = new HashMap<>();
        for (ChannelAuth channelAuth : ChannelAuth.values()) {
            INNER.put(channelAuth.code, channelAuth);
        }
    }

    private String tag;

    private int code;

    ChannelAuth(String tag, int code) {
        this.tag = tag;
        this.code = code;
    }

    public static ChannelAuth parseFromCode(int code) {
        return INNER.getOrDefault(code, UNKNOWN);
    }

    public String getTag() {
        return tag;
    }

    public int getCode() {
        return code;
    }
}
