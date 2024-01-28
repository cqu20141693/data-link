package com.witeam.service.common.device.deliver.raw;


import com.witeam.service.common.biz.DeviceTypeEnum;

public class LinkChange {

    /**
     * 链路标记
     */
    private String linkTag;

    /**
     * 变更时间戳
     */
    private long timeStamp;

    /**
     * 变更类型
     */
    private ChangeType changeType;

    /**
     * 变更节点标记
     */
    private String nodeTag;

    /**
     * 连接会话key
     */
    private String sessionKey;

    /**
     * 设备类型
     */
    private DeviceTypeEnum deviceType;

    /**
     * 链路alive时间
     */
    private int keepAliveSeconds;

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    public void setChangeType(ChangeType changeType) {
        this.changeType = changeType;
    }

    public String getNodeTag() {
        return nodeTag;
    }

    public void setNodeTag(String nodeTag) {
        this.nodeTag = nodeTag;
    }

    public String getLinkTag() {
        return linkTag;
    }

    public void setLinkTag(String linkTag) {
        this.linkTag = linkTag;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public int getKeepAliveSeconds() {
        return keepAliveSeconds;
    }

    public void setKeepAliveSeconds(int keepAliveSeconds) {
        this.keepAliveSeconds = keepAliveSeconds;
    }

    public DeviceTypeEnum getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceTypeEnum deviceType) {
        this.deviceType = deviceType;
    }
}
