package com.chongctech.device.link.biz.stream.up;

import static io.netty.handler.codec.mqtt.MqttConnectReturnCode.CONNECTION_ACCEPTED;
import static io.netty.handler.codec.mqtt.MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED;
import static io.netty.handler.codec.mqtt.MqttConnectReturnCode.CONNECTION_REFUSED_NOT_AUTHORIZED;
import static io.netty.handler.codec.mqtt.MqttConnectReturnCode.CONNECTION_REFUSED_SERVER_UNAVAILABLE;
import static io.netty.handler.codec.mqtt.MqttConnectReturnCode.CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION;
import com.chongctech.device.common.model.device.base.CmdStatus;
import com.chongctech.device.common.model.device.base.DeviceTypeEnum;
import com.chongctech.device.common.model.device.deliver.raw.ChangeTypeEnum;
import com.chongctech.device.common.model.device.deliver.raw.LinkChangeModel;
import com.chongctech.device.link.biz.model.link.LinkSysCode;
import com.chongctech.device.common.model.device.deliver.raw.PublishMessageModel;
import com.chongctech.device.common.model.device.deliver.raw.SendActionModel;
import com.chongctech.device.common.util.device.LinkTagUtil;
import com.chongctech.device.link.biz.BrokerMetrics;
import com.chongctech.device.link.biz.executor.BizProcessExecutors;
import com.chongctech.device.link.biz.link.LinkStatusHandler;
import com.chongctech.device.link.biz.model.authenticate.AuthenticateResponse;
import com.chongctech.device.link.biz.model.link.ChannelAuth;
import com.chongctech.device.link.biz.model.link.LinkInfo;
import com.chongctech.device.link.biz.model.link.SendInfo;
import com.chongctech.device.link.biz.session.LinkSession;
import com.chongctech.device.link.biz.session.Qos1Session;
import com.chongctech.device.link.biz.stream.down.DownStreamHandler;
import com.chongctech.device.link.config.MqttProtocolConfiguration;
import com.chongctech.device.link.server.netty.MqttIdleStateHandler;
import com.chongctech.device.link.server.netty.NettyUtils;
import com.chongctech.device.link.spi.authenticate.AuthenticateServiceFacade;
import com.chongctech.device.link.spi.deliver.DeliverRawService;
import com.chongctech.device.link.util.NodeUtil;
import com.chongctech.service.common.call.result.CommonResult;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttPubAckMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import io.netty.handler.codec.mqtt.MqttUnsubscribeMessage;
import io.netty.handler.codec.mqtt.MqttVersion;
import io.netty.util.CharsetUtil;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class UpStreamHandlerImpl implements UpStreamHandler {
    private static final Logger logger = LoggerFactory.getLogger(UpStreamHandlerImpl.class);

    private final static long MAX_CONNECT_WAIT_MS = 20000L;

    private final static String IDLE_STAT_HANDLER = "idleStateHandler";

    @Autowired
    private BizProcessExecutors bizProcessExecutors;

    @Autowired
    private MqttProtocolConfiguration mqttConfig;

    @Autowired
    private DeliverRawService deliverRawService;

    @Autowired
    private AuthenticateServiceFacade authenticateService;

    @Autowired
    private BrokerMetrics brokerMetrics;

    @Autowired
    private DownStreamHandler downStreamHandler;

    @Autowired
    private LinkStatusHandler linkStatusHandler;

    @Autowired
    private LinkSession linkSession;

    @Autowired
    private Qos1Session qos1Session;

    @Autowired
    private NodeUtil nodeUtil;

    private void connectFailClose(Channel channel, String failMsg, MqttConnectReturnCode returnCode) {
        ChannelFuture notifyFuture = downStreamHandler.sendError(channel, failMsg);
        notifyFuture.addListener((ChannelFutureListener) future ->
                NettyUtils.asyncCloseChannel(downStreamHandler.replyConnAck(channel, returnCode)));
    }

    @Override
    public void handleConnect(Channel channel, MqttConnectMessage msg) {
        String clientIdentifier = msg.payload().clientIdentifier();
        String userName = msg.payload().userName();
        byte[] passwordInBytes = msg.payload().passwordInBytes();
        String passWord = passwordInBytes == null ? "" : new String(passwordInBytes, CharsetUtil.UTF_8);
        if (clientIdentifier == null) {
            logger.warn("processConnect. the clientIdentifier is null. channel: {}, it will be disconnected",
                    channel.toString());
            connectFailClose(channel, "clientIdentifier should not be null", CONNECTION_REFUSED_IDENTIFIER_REJECTED);
            return;
        }

        int keepAlive = msg.variableHeader().keepAliveTimeSeconds();
        if (keepAlive < mqttConfig.getMinHeartBeatSecond()) {
            logger.warn(
                    "processConnect. the keepAlive second is less then {}, the clientIdentifier = {} is forbidden to "
                            + "connect to this server"
                    , mqttConfig.getMinHeartBeatSecond(), clientIdentifier);
            connectFailClose(channel, "keepAlive must >= " + mqttConfig.getMinHeartBeatSecond(),
                    CONNECTION_REFUSED_IDENTIFIER_REJECTED);
            return;
        }

        logger.info("processConnect. clientIdentifier={}, username={}", clientIdentifier, userName);
        if (brokerMetrics.getLinkAllCount() >= mqttConfig.getMaxLink()) {
            logger.warn("processConnect. the connection count reach the max count!");
            connectFailClose(channel, "server connection count reach the maxsize",
                    CONNECTION_REFUSED_SERVER_UNAVAILABLE);
            return;
        }

        if (msg.variableHeader().version() != MqttVersion.MQTT_3_1.protocolLevel()
                && msg.variableHeader().version() != MqttVersion.MQTT_3_1_1.protocolLevel()) {
            logger.error("processConnect. MQTT protocol version is not valid. clientIdentifier={}", clientIdentifier);
            NettyUtils.asyncCloseChannel(
                    downStreamHandler.replyConnAck(channel, CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION));
            return;
        }

        final long connectTime = System.currentTimeMillis();
        boolean result = bizProcessExecutors.submitConnTask(clientIdentifier, () -> {
            long now = System.currentTimeMillis();
            /*
              如果Connect消息处理时间和进入ConnectTask的时间相差大于20秒，或者客户端已经断开了本网络连接，就直接断开。
             */
            if (Math.abs(now - connectTime) >= MAX_CONNECT_WAIT_MS || !channel.isActive()) {
                logger.warn("processConnect. the connect message is timeout, refuse it! clientIdentifier={}",
                        clientIdentifier);
                connectFailClose(channel, "the connect message is timeout",
                        CONNECTION_REFUSED_IDENTIFIER_REJECTED);
                return;
            } else {
                logger.info("processConnect. begin auth clientIdentifier={}", clientIdentifier);
            }
            String sessionKey = RandomStringUtils.randomAlphanumeric(16);
            // login authentic
            CommonResult<AuthenticateResponse> authResult =
                    authenticateService.authenticate(clientIdentifier, userName, passWord,
                            sessionKey, nodeUtil.getNodeTag(), nodeUtil.getPort(), keepAlive);
            if (!authResult.success()) {
                logger.warn("processConnect. clientIdentifier: {} login failed.", clientIdentifier);
                connectFailClose(channel, authResult.getMessage(), CONNECTION_REFUSED_NOT_AUTHORIZED);
                return;
            }

            AuthenticateResponse response = authResult.getData();

            // 新链路上线,本地记录发消息
            LinkInfo newLinkInfo = new LinkInfo(channel);
            //本地登录信息记录，看是否存在登录
            boolean recorded = linkStatusHandler.linkLocalRecord(response.getLinkTag(), sessionKey,
                    response.getDeviceTypeEnum(), newLinkInfo, response.getSignatureTag());
            if (!recorded) {
                connectFailClose(channel, "server unavailable", CONNECTION_REFUSED_SERVER_UNAVAILABLE);
                return;
            }
            //链路参数绑定到channel
            NettyUtils.recordChannelAuth(channel, response.getChannelAuth());
            NettyUtils.recordDeviceType(channel, response.getDeviceTypeEnum());
            NettyUtils.recordLinkTag(channel, response.getLinkTag());
            NettyUtils.recordSessionKey(channel, sessionKey);
            NettyUtils.recordKeepAliveSeconds(channel, keepAlive);
            NettyUtils.recordSignatureTag(channel, response.getSignatureTag());

            // reset the timeout second
            setIdleTime(channel.pipeline(), (keepAlive + 10));
            //标记channel连接状态有效
            NettyUtils.validStatus(channel);
            brokerMetrics.incLinkCount(response.getChannelAuth());

            if (channel.isActive()) {
                //返回
                ChannelFuture channelFuture = downStreamHandler.replyConnAck(channel, CONNECTION_ACCEPTED);
                if (channelFuture != null) {
                    channelFuture.addListener((ChannelFutureListener) future -> {
                        String linkTag = NettyUtils.getLinkTag(channel);
                        if (future.isSuccess()) {
                            LinkTagUtil.LinkTagElements elements = LinkTagUtil.parseFromLinkTag(linkTag);
                            if (elements == null) {
                                logger.warn("unexpected situation happen,linkTag parse fail. linkTag={}", linkTag);
                                // linkTag error
                                linkStatusHandler.disconnectFromLocal(channel, LinkSysCode.SERVER_ERROR);
                                return;
                            }
                            Optional.ofNullable(response.getWelcomeInfoModel())
                                    .ifPresent(info -> downStreamHandler.sendQos0Msg(linkTag, info.getWelcomeTopic(),
                                            info.getWelcomeMsg()));
                        } else {
                            logger.error("replyConnAck failed,channel={},linkTag={},time={}", channel, linkTag, now);
                            //未完成connAck，设备链路已断开，主动清理
                            linkStatusHandler.disconnectFromLocal(channel, LinkSysCode.CONN_ACK_SEND_FAIL);
                        }

                    });
                    logger.info("processConnect finished, clientIdentifier={} ,userName ={}, channel={}",
                            clientIdentifier, userName, channel);
                }
            } else {
                //未完成登录，设备链路已断开，主动清理
                linkStatusHandler.disconnectFromLocal(channel, LinkSysCode.INACTIVE_WHILE_CONNECTED);
            }
        });
        if (!result) {
            logger.warn(
                    "processConnect. the connect task maybe full, this client connection will be rejected. "
                            + "clientIdentifier={}",
                    clientIdentifier);
            channel.close();
        }
    }

    @Override
    public void handlePublish(Channel channel, MqttPublishMessage msg) {
        String linkTag = NettyUtils.getLinkTag(channel);
        String sessionKey = NettyUtils.getSessionKey(channel);
        if (!StringUtils.hasText(linkTag) || !StringUtils.hasText(sessionKey)) {
            logger.error("channel {} linkTag or sessionKey is Empty!", channel);
            NettyUtils.asyncCloseChannel(downStreamHandler.sendError(channel, "link channel has not been init."));
            return;
        }
        ChannelAuth channelAuth = NettyUtils.getChannelAuth(channel);
        if (channelAuth == ChannelAuth.ONLY_SUBSCRIBE) {
            logger.info("system will close channel {} ,for no mqtt publish authority!", channel);
            linkStatusHandler.disconnectFromLocal(channel, LinkSysCode.PUB_NOT_AUTHORITY);
            return;
        }

        logger.debug("processPublish(...) linkTag={}", linkTag);
        if (Objects.isNull(msg)) {
            logger.error("handlePublish: linkTag={}, channel {} ,mqtt publish message should not be null", linkTag,
                    channel);
            linkStatusHandler.disconnectFromLocal(channel, LinkSysCode.PUB_NULL_ERROR);
            return;
        }

        if (Objects.isNull(msg.variableHeader())) {
            logger.error(
                    "handlePublish: linkTag={}, channel {} ,mqtt publish message header should not be null, msg={}",
                    linkTag, channel, msg);
            linkStatusHandler.disconnectFromLocal(channel, LinkSysCode.PUB_HEADER_NULL_ERROR);
            return;
        }

        if (!StringUtils.hasText(msg.variableHeader().topicName())) {
            logger.error(
                    "handlePublish: linkTag={}, channel {} ,mqtt publish message topic should not be empty, msg={}",
                    linkTag, channel, msg);
            linkStatusHandler.disconnectFromLocal(channel, LinkSysCode.PUB_TOPIC_NULL_ERROR);
            return;
        }

        final int messageId = msg.variableHeader().packetId();
        LinkInfo linkInfo = linkSession.getLinkInfo(linkTag);
        if (linkInfo == null) {
            logger.warn("handlePublish: linkTag={}, channel {} ,invalid link.", linkTag, channel);
            linkStatusHandler.disconnectFromLocal(channel, LinkSysCode.INVALID_LINK);
            return;
        }

        final MqttQoS qos = msg.fixedHeader().qosLevel();
        if (qos == MqttQoS.EXACTLY_ONCE) {
            logger.info("link:{} will be disconnected, the mqtt server dons't support QoS 2, channel:{}", linkTag,
                    channel);
            linkStatusHandler.disconnectFromLocal(channel, LinkSysCode.QOS_2_NOT_SUPPORT);
            return;
        }

        final byte[] payload = NettyUtils.readBytesAndRewind(msg.payload());
        final String topic = msg.variableHeader().topicName();
        DeviceTypeEnum deviceTypeEnum = NettyUtils.getDeviceType(channel);

        bizProcessExecutors.submitProcessTask(linkTag, () ->
                deliverRawService.deliverPublishMsg(new PublishMessageModel()
                        .setLinkTag(linkTag)
                        .setPayload(payload)
                        .setTopic(topic)
                        .setSessionKey(sessionKey)
                        .setDeviceType(deviceTypeEnum)
                        .setSignatureTag(NettyUtils.getSignatureTag(channel))
                        .setTimeStamp(System.currentTimeMillis())));

        //qos1 默认返回
        if (qos == MqttQoS.AT_LEAST_ONCE) {
            //qos 1 ,返回ack
            downStreamHandler.replyPubAck(channel, messageId);
        }

        brokerMetrics.incDataCount();
    }

    @Override
    public void handlePubAck(Channel channel, MqttPubAckMessage msg) {
        String linkTag = NettyUtils.getLinkTag(channel);
        String sessionKey = NettyUtils.getSessionKey(channel);
        if (StringUtils.isEmpty(linkTag) || StringUtils.isEmpty(sessionKey)) {
            logger.error("channel {} linkTag or sessionKey is Empty!", channel);
            NettyUtils.asyncCloseChannel(downStreamHandler.sendError(channel, "linkTag or sessionKey is Empty"));
            return;
        }
        ChannelAuth channelAuth = NettyUtils.getChannelAuth(channel);
        if (channelAuth == ChannelAuth.ONLY_SUBSCRIBE) {
            logger.info("system will close channel {} ,for no mqtt publish ack authority!", channel);
            linkStatusHandler.disconnectFromLocal(channel, LinkSysCode.PUB_NOT_AUTHORITY);
            return;
        }

        logger.debug("handlePubAck(...) linkTag={}", linkTag);

        LinkInfo linkInfo = linkSession.getLinkInfo(linkTag);
        if (linkInfo == null) {
            logger.warn("handlePubAck: linkTag={}, channel {} ,invalid link.", linkTag, channel);
            linkStatusHandler.disconnectFromLocal(channel, LinkSysCode.INVALID_LINK);
            return;
        }
        int messageId = msg.variableHeader().messageId();

        SendInfo sendInfo = qos1Session.get(linkTag, messageId);
        if (sendInfo != null) {
            qos1Session.remove(linkTag, messageId);
            SendActionModel sendActionModel = new SendActionModel();
            sendActionModel.setLinkTag(sendInfo.getLinkTag());
            sendActionModel.setBizId(sendInfo.getBizId());
            sendActionModel.setTimeStamp(System.currentTimeMillis());
            sendActionModel.setActionType(CmdStatus.ACK);
            deliverRawService.deliverSendActionMsg(sendActionModel);
        }
    }

    @Override
    public void handleUnsubscribe(Channel channel, MqttUnsubscribeMessage msg) {
        logger.info("handleSubscribe , dosn't suppurt unsubscribe,channel {}", channel);
        linkStatusHandler.disconnectFromLocal(channel, LinkSysCode.SUB_NOT_SUPPORT);
    }

    @Override
    public void handleSubscribe(Channel channel, MqttSubscribeMessage msg) {
        logger.info("handleSubscribe , dosn't suppurt subscribe,channel {}", channel);
        linkStatusHandler.disconnectFromLocal(channel, LinkSysCode.SUB_NOT_SUPPORT);
    }

    @Override
    public void handlePubRel(Channel channel, MqttMessage msg) {
        logger.info("handlePubRel , this broker dosn't suppurt QoS2,channel {}", channel);
        linkStatusHandler.disconnectFromLocal(channel, LinkSysCode.QOS_2_NOT_SUPPORT);
    }

    @Override
    public void handlePubRec(Channel channel, MqttMessage msg) {
        logger.info("handlePubRel this broker dosn't suppurt QoS2,channel {}", channel);
        linkStatusHandler.disconnectFromLocal(channel, LinkSysCode.QOS_2_NOT_SUPPORT);
    }

    @Override
    public void handlePubComp(Channel channel, MqttMessage msg) {
        logger.info("handlePubRel this broker dosn't suppurt QoS2,channel {}", channel);
        linkStatusHandler.disconnectFromLocal(channel, LinkSysCode.QOS_2_NOT_SUPPORT);
    }

    @Override
    public void handleDisconnect(Channel channel) {
        logger.info("handleDisconnect.channel {}", channel);
        linkStatusHandler.disconnectFromLocal(channel, LinkSysCode.CLIENT_SEND_DISCONNECT);
    }

    @Override
    public void handlePingReq(Channel channel, MqttMessage msg) {
        String linkTag = NettyUtils.getLinkTag(channel);
        String sessionKey = NettyUtils.getSessionKey(channel);
        if (StringUtils.hasText(linkTag)) {
            bizProcessExecutors.submitProcessTask(linkTag, () -> {
                deliverRawService.deliverLinkChangeMsg(new LinkChangeModel()
                        .setLinkTag(linkTag)
                        .setTimeStamp(System.currentTimeMillis())
                        .setChangeTypeEnum(ChangeTypeEnum.LINK_HEART_BEAT)
                        .setNodeTag(nodeUtil.getNodeTag())
                        .setSessionKey(sessionKey)
                        .setKeepAliveSeconds(NettyUtils.getKeepAliveSeconds(channel))
                        .setDeviceType(NettyUtils.getDeviceType(channel))
                        .setPort(nodeUtil.getPort())
                        .setReasonCode(LinkSysCode.PING.getCode())
                        .setSignatureTag(NettyUtils.getSignatureTag(channel)));

                downStreamHandler.replyPingResp(channel);
            });
        } else {
            NettyUtils.asyncCloseChannel(downStreamHandler.sendError(channel, "mqtt not login."));
        }
    }

    private void setIdleTime(ChannelPipeline pipeline, int idleTime) {
        if (pipeline.names().contains(IDLE_STAT_HANDLER)) {
            ChannelHandler remove = pipeline.remove(IDLE_STAT_HANDLER);
        }
        pipeline.addAfter("encoder", IDLE_STAT_HANDLER, new MqttIdleStateHandler(0, 0, idleTime));
    }
}