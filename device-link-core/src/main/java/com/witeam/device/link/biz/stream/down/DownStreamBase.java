package com.witeam.device.link.biz.stream.down;

import static io.netty.handler.codec.mqtt.MqttQoS.AT_MOST_ONCE;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.mqtt.MqttConnAckMessage;
import io.netty.handler.codec.mqtt.MqttConnAckVariableHeader;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPubAckMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttSubAckMessage;
import io.netty.handler.codec.mqtt.MqttSubAckPayload;
import io.netty.handler.codec.mqtt.MqttUnsubAckMessage;
import java.util.function.BooleanSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * 下行操作基础类
 */
public class DownStreamBase {
    private static final Logger logger = LoggerFactory.getLogger("com.witeam.log.link.downstream");

    protected ChannelFuture mqttPush(Channel channel, String topic, byte[] payload, int qos, int packetId,
                                     BooleanSupplier actionBeforePush) {

        if (channel != null) {
            boolean writable = channel.isWritable();
            logger.debug("mqtt push  channel={} topic={} payload={},writable={}", channel, topic, new String(payload),
                    writable);
            if (writable && StringUtils.hasText(topic)) {
                try {
                    MqttQoS mqttQoS = MqttQoS.valueOf(qos);
                    MqttFixedHeader fixedHeader =
                            new MqttFixedHeader(MqttMessageType.PUBLISH, false, mqttQoS, false, 0);
                    MqttPublishVariableHeader varHeader = new MqttPublishVariableHeader(topic, packetId);
                    ByteBuf byteBuf = Unpooled.wrappedBuffer(payload);
                    MqttPublishMessage publishMessage = new MqttPublishMessage(fixedHeader, varHeader, byteBuf);
                    if (actionBeforePush != null && !actionBeforePush.getAsBoolean()) {
                        //存在推送前的操作定义，且操作不成功
                        return null;
                    }
                    return channel.writeAndFlush(publishMessage);
                } catch (Exception e) {
                    logger.error("mqtt push failed. topic={}", topic);
                }
            }
        }
        return null;
    }

    ChannelFuture mqttPushAck(Channel channel, int messageId) {
        try {
            if (channel != null) {
                boolean writable = channel.isWritable();
                logger.debug("mqtt pub ack  channel={},writable={}", channel, writable);
                if (writable) {
                    MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBACK, false, AT_MOST_ONCE,
                            false, 0);
                    MqttMessageIdVariableHeader variableHeader = MqttMessageIdVariableHeader.from(messageId);
                    MqttPubAckMessage pubAckMessage = new MqttPubAckMessage(mqttFixedHeader, variableHeader);
                    return channel.writeAndFlush(pubAckMessage);
                } else {
                    logger.error("send pub ack failed. because the write buf is full. messageId = {}", messageId);
                }
            }
        } catch (Exception e) {
            logger.error("mqtt push ack failed. messageId={}", messageId);
        }
        return null;
    }

    ChannelFuture mqttConnAck(Channel channel, MqttConnectReturnCode returnCode) {
        try {
            boolean writable = channel.isWritable();
            logger.debug("mqtt conn ack  channel={} code={},writable={}", channel, returnCode, writable);
            if (channel != null && writable) {
                MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.CONNACK, false, AT_MOST_ONCE,
                        false, 0);
                MqttConnAckVariableHeader mqttConnAckVariableHeader = new MqttConnAckVariableHeader(returnCode, false);
                MqttConnAckMessage mqttConnAckMessage =
                        new MqttConnAckMessage(mqttFixedHeader, mqttConnAckVariableHeader);
                return channel.writeAndFlush(mqttConnAckMessage);
            } else {
                logger.error("send conn ack failed. returnCode = {}", returnCode);
            }
        } catch (Exception e) {
            logger.error("mqtt conn ack failed. returnCode={}", returnCode);
        }
        return null;
    }

    ChannelFuture mqttUnsubAck(Channel channel, int messageId) {
        try {
            if (channel != null && channel.isWritable()) {
                MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.UNSUBACK, false, AT_MOST_ONCE,
                        false, 0);
                MqttMessageIdVariableHeader variableHeader = MqttMessageIdVariableHeader.from(messageId);
                MqttUnsubAckMessage ackMessage = new MqttUnsubAckMessage(mqttFixedHeader, variableHeader);
                return channel.writeAndFlush(ackMessage);
            } else {
                logger.error("send unsub ack failed. messageId = {}", messageId);
            }
        } catch (Exception e) {
            logger.error("mqtt unsub ack failed. messageId={}", messageId);
        }
        return null;
    }

    ChannelFuture mqttSubAck(Channel channel, int messageId, Iterable<Integer> grantedQoSLevels) {
        try {
            if (channel != null && channel.isWritable()) {
                MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.SUBACK, false, AT_MOST_ONCE,
                        false, 0);
                MqttMessageIdVariableHeader variableHeader = MqttMessageIdVariableHeader.from(messageId);
                MqttSubAckPayload payload = new MqttSubAckPayload(grantedQoSLevels);
                MqttSubAckMessage ackMessage = new MqttSubAckMessage(mqttFixedHeader, variableHeader, payload);
                return channel.writeAndFlush(ackMessage);
            } else {
                logger.error("send sub ack failed. messageId = {}", messageId);
            }
        } catch (Exception e) {
            logger.error("mqtt sub ack failed. messageId={}", messageId);
        }
        return null;
    }

    ChannelFuture mqttPingResp(Channel channel) {
        try {
            if (channel != null) {
                boolean writable = channel.isWritable();
                logger.debug("mqtt ping resp  channel={},writable={}", channel, writable);
                if (writable) {
                    MqttFixedHeader pingHeader = new MqttFixedHeader(MqttMessageType.PINGRESP, false, AT_MOST_ONCE,
                            false, 0);
                    MqttMessage pingResp = new MqttMessage(pingHeader);
                    return channel.writeAndFlush(pingResp);
                } else {
                    logger.error("send ping resp failed.");
                }
            }
        } catch (Exception e) {
            logger.error("mqtt ping resp failed.");
        }
        return null;
    }
}
