package com.chongctech.device.link.biz.link;

import com.chongctech.device.common.model.device.base.LinkDeviceType;
import com.chongctech.device.common.model.device.deliver.raw.ChangeTypeEnum;
import com.chongctech.device.common.model.device.deliver.raw.LinkChangeModel;
import com.chongctech.device.link.biz.BrokerMetrics;
import com.chongctech.device.link.biz.model.link.ChannelInfo;
import com.chongctech.device.link.biz.model.link.LinkInfo;
import com.chongctech.device.link.biz.model.link.LinkSysCode;
import com.chongctech.device.link.biz.session.LinkSession;
import com.chongctech.device.link.biz.stream.down.DownStreamHandler;
import com.chongctech.device.link.server.netty.NettyUtils;
import com.chongctech.device.link.spi.deliver.DeliverRawService;
import com.chongctech.device.link.util.NodeUtil;
import io.netty.channel.Channel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LinkStatusHandlerImpl implements LinkStatusHandler {
    private static final Logger logger = LoggerFactory.getLogger("com.cctech.log.link.status");

    @Autowired
    private LinkSession linkSession;

    @Autowired
    private BrokerMetrics brokerMetrics;

    @Autowired
    private DeliverRawService deliverRawService;

    @Autowired
    private DownStreamHandler downStreamHandler;

    @Autowired
    private NodeUtil nodeUtil;

    @Override
    public boolean hasLink(String linkTag) {
        return linkSession.containLink(linkTag);
    }

    @Override
    public boolean disconnectFromService(String linkTag, String sessionKey, long time, String logCode) {
        LinkInfo linkInfo = linkSession.getLinkInfo(linkTag);
        if (linkInfo == null) {
            logger.warn("disconnectFromService is called, but linkInfo not exist ,linkTag={}.", linkTag);
            return false;
        }

        Channel channel = linkInfo.getChannel();
        String sessionKeyFromChannel = NettyUtils.getSessionKey(channel);
        if (!StringUtils.equals(sessionKey, sessionKeyFromChannel)) {
            logger.warn("disconnectFromService is called, but have unmatched sessionKey ,linkTag={}.", linkTag);
            return false;
        }

        if (!NettyUtils.tryInvalidStatus(channel)) {
            //未设置成功，已是无效链路
            return false;
        }

        //try remove linkInfo
        linkSession.removeLink(linkTag);

        return doDisconnectLink(channel, linkTag, sessionKey, time, logCode);
    }

    @Override
    public ChannelInfo disconnectFromLocal(Channel channel, LinkSysCode cause) {
        if (!NettyUtils.tryInvalidStatus(channel)) {
            //未设置成功，已是无效链路
            return null;
        }

        String linkTag = NettyUtils.getLinkTag(channel);
        String sessionKey = NettyUtils.getSessionKey(channel);
        if (StringUtils.isEmpty(linkTag) || StringUtils.isEmpty(sessionKey)) {
            //此时的channel还未进行mqtt初始化
            logger.info("no related mqtt link and should be close , channel is not mqtt login. channel = {}.", channel);
            return null;
        }
        LinkInfo linkInfo = linkSession.removeLink(linkTag);
        if (linkInfo == null) {
            logger.warn("disconnectFromLocal is called, but linkInfo not exist ,linkTag={}.", linkTag);
            return null;
        }
        if (!doDisconnectLink(channel, linkTag, sessionKey, System.currentTimeMillis(), cause.getCode())) {
            return null;
        }

        return new ChannelInfo().setLinkTag(linkTag).setSessionKey(sessionKey);
    }

    private boolean doDisconnectLink(Channel channel, String linkTag, String sessionKey, Long time, String reasonCode) {
        try {
            LinkDeviceType deviceType = NettyUtils.getDeviceType(channel);
            logger.info("doDisconnectLink is called, linkTag={},sessionKey={},deviceType={},causeCode={}.",
                    linkTag, sessionKey, deviceType, reasonCode);

            channel.flush();
            brokerMetrics.decLinkCount(NettyUtils.getChannelAuth(channel));

            //传递变更消息
            deliverRawService.deliverLinkChangeMsg(new LinkChangeModel()
                    .setLinkTag(linkTag)
                    .setTimeStamp(time)
                    .setChangeTypeEnum(ChangeTypeEnum.LINK_DISCONNECTED)
                    .setNodeTag(nodeUtil.getNodeTag())
                    .setSessionKey(sessionKey)
                    .setPort(nodeUtil.getPort())
                    .setSignatureTag(NettyUtils.getSignatureTag(channel))
                    .setDeviceType(deviceType)
                    .setReasonCode(reasonCode));
        } catch (Exception e) {
            logger.error("error occur when doDisconnectLink. {}, linkTag={}", e.getMessage(), linkTag);
            return false;
        } finally {
            NettyUtils.asyncCloseChannel(downStreamHandler.sendError(channel, reasonCode));
        }
        logger.info("doDisconnectLink link linkTag:{} finished, deviceCount = {}, channel:{}",
                linkTag, brokerMetrics.getLinkAllCount(), channel);
        return true;
    }

    @Override
    public void disconnectAllLink() {
        logger.info("disconnectAllClient is called");
        linkSession.executeForEachLink((linkInfo) ->
                disconnectFromLocal(linkInfo.getChannel(), LinkSysCode.SERVER_RESTART));
    }

    @Override
    public boolean reportLinkAlive(Channel channel, Integer keepAlive) {
        String linkTag = NettyUtils.getLinkTag(channel);
        if (!StringUtils.isBlank(linkTag)) {
            deliverRawService.deliverLinkChangeMsg(new LinkChangeModel()
                    .setLinkTag(linkTag)
                    .setNodeTag(nodeUtil.getNodeTag())
                    .setPort(nodeUtil.getPort())
                    .setDeviceType(NettyUtils.getDeviceType(channel))
                    .setSignatureTag(NettyUtils.getSignatureTag(channel))
                    .setKeepAliveSeconds(keepAlive)
                    .setTimeStamp(System.currentTimeMillis())
                    .setSessionKey(NettyUtils.getSessionKey(channel))
                    .setChangeTypeEnum(ChangeTypeEnum.LINK_ALIVE));
        } else {
            NettyUtils
                    .asyncCloseChannel(downStreamHandler.sendError(channel, "linkTag is blank when trigger linkAlive"));
            return false;
        }
        return true;
    }


    @Override
    public boolean linkLocalRecord(String linkTag, String sessionKey, LinkDeviceType deviceType, LinkInfo linkInfo,
                                   String signatureTag) {
        LinkInfo preLinkInfo = linkSession.recordLink(linkTag, linkInfo);
        if (preLinkInfo != null) {
            //当前链路认证完成，发现本地仍存在链路信息，需下线老链路，当前链路登录失败
            logger.warn("do local record,but preLinkInfo is not null pre={},new={}", preLinkInfo, linkInfo);
            disconnectFromLocal(preLinkInfo.getChannel(), LinkSysCode.NEW_CLIENT_ONLINE);
            return false;
        }

        //本地记录成功，传递登录消息
        deliverRawService.deliverLinkChangeMsg(new LinkChangeModel()
                .setLinkTag(linkTag)
                .setSessionKey(sessionKey)
                .setDeviceType(deviceType)
                .setTimeStamp(System.currentTimeMillis())
                .setChangeTypeEnum(ChangeTypeEnum.LINK_CONNECTED)
                .setNodeTag(nodeUtil.getNodeTag())
                .setPort(nodeUtil.getPort())
                .setSignatureTag(signatureTag)
                .setReasonCode(LinkSysCode.ONLINE.getCode()));
        return true;
    }

    @Override
    public ChannelInfo loginLocalCheck(String linkTag) {
        LinkInfo linkInfo = linkSession.getLinkInfo(linkTag);
        if (linkInfo != null) {
            return disconnectFromLocal(linkInfo.getChannel(), LinkSysCode.NEW_CLIENT_ONLINE);
        }
        return null;
    }

    @Override
    public int getLinkCount() {
        return brokerMetrics.getLinkAllCount();
    }
}
