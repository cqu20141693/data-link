package com.witeam.service.common.device.deliver.raw;


import com.witeam.service.common.biz.DeviceTypeEnum;

public class PublishMsg {
    /**
     * 发送链路标记
     */
    private String linkTag;

    /**
     * 设备类型
     */
    private DeviceTypeEnum deviceType;

    /**
     * 变更时间戳
     */
    private long timeStamp;

    /**
     * 连接会话标识
     */
    private String sessionKey;

    /**
     * 主题topic
     */
    private String topic;

    /**
     * 负载数据
     */
    private byte[] payload;

    public String getLinkTag() {
        return linkTag;
    }

    public void setLinkTag(String linkTag) {
        this.linkTag = linkTag;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public DeviceTypeEnum getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceTypeEnum deviceType) {
        this.deviceType = deviceType;
    }
}
