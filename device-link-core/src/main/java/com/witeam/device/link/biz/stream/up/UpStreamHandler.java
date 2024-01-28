package com.witeam.device.link.biz.stream.up;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttPubAckMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import io.netty.handler.codec.mqtt.MqttUnsubscribeMessage;

public interface UpStreamHandler {
    /**
     * 处理connect报文信息
     *
     * @param channel
     * @param msg
     */
    void handleConnect(Channel channel, MqttConnectMessage msg);

    /**
     * 处理publish报文
     *
     * @param channel
     * @param msg
     */
    void handlePublish(Channel channel, MqttPublishMessage msg);

    /**
     * 处理publish ack报文
     *
     * @param channel
     * @param msg
     */
    void handlePubAck(Channel channel, MqttPubAckMessage msg);

    /**
     * 处理unsubscribe报文
     *
     * @param channel
     * @param msg
     */
    void handleUnsubscribe(Channel channel, MqttUnsubscribeMessage msg);

    /**
     * 处理subscribe报文
     *
     * @param channel
     * @param msg
     */
    void handleSubscribe(Channel channel, MqttSubscribeMessage msg);

    /**
     * 处理publish release 报文，qos2
     *
     * @param channel
     * @param msg
     */
    void handlePubRel(Channel channel, MqttMessage msg);

    /**
     * 处理publish rec 报文，qos2
     *
     * @param channel
     * @param msg
     */
    void handlePubRec(Channel channel, MqttMessage msg);

    /**
     * 处理publish comp 报文，qos2
     *
     * @param channel
     * @param msg
     */
    void handlePubComp(Channel channel, MqttMessage msg);

    /**
     * 处理disconnect报文
     *
     * @param channel
     */
    void handleDisconnect(Channel channel);

    /**
     * 处理Ping报文
     *
     * @param channel
     * @param msg
     */
    void handlePingReq(Channel channel, MqttMessage msg);
}
