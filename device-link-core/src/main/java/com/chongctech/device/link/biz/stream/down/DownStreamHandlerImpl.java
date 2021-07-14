package com.chongctech.device.link.biz.stream.down;

import com.chongctech.device.common.model.device.deliver.raw.SendActionModel;
import com.chongctech.device.link.biz.BrokerMetrics;
import com.chongctech.device.link.biz.executor.BizProcessExecutors;
import com.chongctech.device.link.biz.model.link.LinkInfo;
import com.chongctech.device.link.biz.model.link.SendInfo;
import com.chongctech.device.link.biz.model.topic.SystemTopicEnum;
import com.chongctech.device.link.biz.session.LinkSession;
import com.chongctech.device.link.biz.session.Qos1Session;
import com.chongctech.device.link.server.netty.NettyUtils;
import com.chongctech.device.link.spi.deliver.DeliverRawService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class DownStreamHandlerImpl extends DownStreamBase implements DownStreamHandler {
    private static final Logger logger = LoggerFactory.getLogger(DownStreamHandlerImpl.class);

    @Autowired
    private BrokerMetrics brokerMetrics;

    @Autowired
    private LinkSession linkSession;

    @Autowired
    private Qos1Session qos1Session;

    @Autowired
    private DeliverRawService deliverRawService;

    @Autowired
    private BizProcessExecutors bizProcessExecutors;

    @Override
    public boolean replyPubAck(Channel channel, int messageId) {
        String linkTag = NettyUtils.getLinkTag(channel);
        bizProcessExecutors.submitProcessTask(linkTag, () -> mqttPushAck(channel, messageId));
        return true;
    }

    @Override
    public ChannelFuture replyConnAck(Channel channel, MqttConnectReturnCode returnCode) {
        return mqttConnAck(channel, returnCode);
    }

    @Override
    public void replyUnsubAck(LinkInfo linkInfo, int messageId) {
        mqttUnsubAck(linkInfo.getChannel(), messageId);
    }

    @Override
    public void replySubAck(LinkInfo linkInfo, int messageId, Iterable<Integer> grantedQoSLevels) {
        mqttSubAck(linkInfo.getChannel(), messageId, grantedQoSLevels);
    }

    @Override
    public void replyPingResp(Channel channel) {
        mqttPingResp(channel);
    }

    @Override
    public ChannelFuture sendError(Channel channel, String message) {
        return mqttPush(channel, SystemTopicEnum.ERROR.getName(), message.getBytes(), 0, 0, null);
    }

    @Override
    public ChannelFuture sendError(LinkInfo linkInfo, String message) {
        return mqttPush(linkInfo.getChannel(), SystemTopicEnum.ERROR.getName(), message.getBytes(), 0, 0, null);

    }

    @Override
    public boolean sendQos0Msg(String linkTag, String topic, byte[] payload) {
        //拿通信链路
        LinkInfo linkInfo = linkSession.getLinkInfo(linkTag);
        if (linkInfo == null) {
            logger.warn("qos0 send fail, the linkTag:{} is offline", linkTag);
            return false;
        }

        if (!StringUtils.hasText(topic) || payload == null) {
            logger.error("qos0 send fail, the topic or the payload is null, linkTag:{}", linkTag);
            return false;
        }

        Channel channel = linkInfo.getChannel();
        if (!channel.isActive()) {
            channel.close();
            return false;
        }

        if (mqttPush(channel, topic, payload, 0, linkInfo.nextPacketId(), null) == null) {
            return false;
        }

        brokerMetrics.incQos0SendCount();
        return true;
    }

    @Override
    public boolean sendQos1Msg(String linkTag, String topic, byte[] payload, String bizId, int ackWaitTime) {
        //拿通信链路
        LinkInfo linkInfo = linkSession.getLinkInfo(linkTag);
        if (linkInfo == null) {
            logger.warn("qos1 send fail, the linkTag:{} is offline", linkTag);
            return false;
        }

        if (!StringUtils.hasText(topic) || payload == null) {
            logger.error("qos1 send fail, the topic or the payload is null, linkTag:{}", linkTag);
            return false;
        }

        SendInfo sendInfo = new SendInfo(linkTag, bizId, topic, payload, 1, ackWaitTime);
        linkInfo.getSendInfoList().add(sendInfo);

        return doQos1LinkSend(linkTag, linkInfo);
    }

    private boolean doQos1LinkSend(String linkTag, LinkInfo linkInfo) {
        return bizProcessExecutors.submitProcessTask(linkTag, () -> {
            Channel channel = linkInfo.getChannel();
            while (true) {
                if (!channel.isActive()) {
                    channel.close();
                    return;
                }
                SendInfo willSendInfo = linkInfo.getSendInfoList().poll();
                if (willSendInfo == null) {
                    break;
                }
                int messageId = willSendInfo.getMessageId() == -1 ? linkInfo.nextPacketId() : willSendInfo.getMessageId();

                ChannelFuture channelFuture = mqttPush(channel, willSendInfo.getTopic(), willSendInfo.getPayload(), 1, messageId,
                        () -> {
                            //qos1发送前操作，先加入发送session记录
                            qos1Session.add(willSendInfo.getLinkTag(), messageId, willSendInfo);
                            return true;
                        });

                //下行推送后操作
                if (channelFuture != null) {
                    brokerMetrics.incQos1SendCount();
                    channelFuture.addListener((ChannelFutureListener) future -> {
                        SendActionModel sendActionModel = new SendActionModel();
                        sendActionModel.setLinkTag(willSendInfo.getLinkTag());
                        sendActionModel.setBizId(willSendInfo.getBizId());
                        sendActionModel.setTimeStamp(System.currentTimeMillis());
                        sendActionModel.setActionType(SendActionModel.ActionType.SEND);
                        deliverRawService.deliverSendActionMsg(sendActionModel);
                    });
                } else {
                    SendActionModel sendActionModel = new SendActionModel();
                    sendActionModel.setLinkTag(willSendInfo.getLinkTag());
                    sendActionModel.setBizId(willSendInfo.getBizId());
                    sendActionModel.setTimeStamp(System.currentTimeMillis());
                    sendActionModel.setActionType(SendActionModel.ActionType.SEND_FAIL);
                    deliverRawService.deliverSendActionMsg(sendActionModel);
                }
            }
        });
    }

    @Override
    public void flushChannelWrite(Channel channel) {
        if (channel.isWritable()) {
            String linkTag = NettyUtils.getLinkTag(channel);
            if (linkTag == null) {
                return;
            }
            LinkInfo linkInfo = linkSession.getLinkInfo(linkTag);
            if (linkInfo == null) {
                return;
            }
            doQos1LinkSend(linkTag, linkInfo);
        }
    }
}
