package com.witeam.device.link.biz.model.link;

import java.util.concurrent.TimeUnit;
import lombok.Data;

@Data
public class SendInfo {
    /**
     * 发送链路标记
     */
    private String linkTag;

    /**
     * 业务id
     */
    private String bizId;
    /**
     * 下行topic
     */
    private String topic;
    /**
     * 原始数据
     */
    private byte[] payload;
    /**
     * 传输质量等级
     */
    private int qos;
    /**
     * ack消息等待时间, 单位秒
     */
    private long ackWaitTime;
    /**
     * meg id
     */
    private volatile int messageId = -1;

    public SendInfo(String linkTag, String bizId,
                    String topic, byte[] payload, int qos, long ackWaitTime) {
        this.linkTag = linkTag;
        this.bizId = bizId;
        this.topic = topic;
        this.payload = payload;
        this.qos = qos;
        this.ackWaitTime = ackWaitTime;
    }
}
