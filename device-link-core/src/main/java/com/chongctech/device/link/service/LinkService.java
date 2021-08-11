package com.chongctech.device.link.service;

/**
 * The interface Device service.
 */
public interface LinkService {
    /**
     * Is online boolean.
     *
     * @param linkTag
     * @return the boolean
     */
    boolean isOnline(String linkTag);

    /**
     * Send qos1 msg
     *
     * @return the boolean
     */
    boolean sendQos1Msg(String linkTag, String bizId, String topic, byte[] content, long ackWaitTime);

    boolean sendQos0Msg(String linkTag, String topic, byte[] content);

    /**
     * 断开链接，非本地则转发断链请求
     *
     * @param linkTag
     * @param sessionKey
     * @param time
     * @param reasonCode
     * @return
     */
    boolean disconnectLink(String linkTag, String sessionKey, long time, String reasonCode);
}
