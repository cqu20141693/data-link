package com.chongctech.device.link.biz.stream.down;

import com.chongctech.device.link.biz.model.link.LinkInfo;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;

public interface DownStreamHandler {
    /**
     * 向某个链路发送PublishAck消息。 主要用于收到QoS1的消息时的处理
     *
     * @param channel   链路
     * @param messageId PublishAck消息中的messageId
     * @return true标识成功
     */
    boolean replyPubAck(Channel channel, int messageId);

    /**
     * 向某个链路发送ConnAck消息。
     *
     * @param channel
     * @param returnCode
     */
    ChannelFuture replyConnAck(Channel channel, MqttConnectReturnCode returnCode);

    /**
     * 向某个链路发送UnsubAck消息。
     *
     * @param linkInfo
     * @param messageId
     */
    void replyUnsubAck(LinkInfo linkInfo, int messageId);

    /**
     * 向某个链路发送SubAck消息。
     *
     * @param linkInfo
     * @param messageId
     */
    void replySubAck(LinkInfo linkInfo, int messageId, Iterable<Integer> grantedQoSLevels);

    /**
     * 向某个链路发送PingResp消息。
     *
     * @param channel
     */
    void replyPingResp(Channel channel);

    /**
     * 发送错误消息
     *
     * @param channel
     * @param message
     * @return
     */
    ChannelFuture sendError(Channel channel, String message);

    /**
     * 发送错误消息
     *
     * @param linkInfo
     * @param message
     * @return
     */
    ChannelFuture sendError(LinkInfo linkInfo, String message);


    /**
     * 向某个链路下发一条publish格式的qos1消息
     * <p>
     *
     * @param linkTag 链路标识
     * @param topic   命令的topic
     * @param payload 命令的内容
     * @return true标识成功
     */
    boolean sendQos0Msg(String linkTag, String topic, byte[] payload);

    /**
     * 向某个链路下发一条publish格式的qos1消息
     * <p>
     *
     * @param linkTag     链路标识
     * @param topic       命令的topic
     * @param payload     命令的内容
     * @param bizId       业务id
     * @param ackWaitTime 消息确认等待时间
     * @return true标识成功
     */
    boolean sendQos1Msg(String linkTag, String topic, byte[] payload, String bizId, int ackWaitTime);

    /**
     * 刷写链路可写数据
     *
     * @param channel
     */
    void flushChannelWrite(Channel channel);
}
