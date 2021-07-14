package com.chongctech.device.link.biz.link;

import com.chongctech.device.common.model.device.base.DeviceTypeEnum;
import com.chongctech.device.common.model.device.deliver.raw.ChangeTypeEnum;
import com.chongctech.device.common.model.device.deliver.raw.LinkChangeModel;
import com.chongctech.device.link.biz.BrokerMetrics;
import com.chongctech.device.link.biz.executor.BizProcessExecutors;
import com.chongctech.device.link.biz.model.link.ChannelInfo;
import com.chongctech.device.link.biz.model.link.LinkInfo;
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
    private static final Logger logger = LoggerFactory.getLogger(LinkStatusHandlerImpl.class);

    @Autowired
    private LinkSession linkSession;

    @Autowired
    private BrokerMetrics brokerMetrics;

    @Autowired
    private DeliverRawService deliverRawService;

    @Autowired
    private DownStreamHandler downStreamHandler;

    @Autowired
    private BizProcessExecutors bizProcessExecutors;

    @Autowired
    private NodeUtil nodeUtil;

    @Override
    public boolean hasLink(String linkTag) {
        return linkSession.containLink(linkTag);
    }

    @Override
    public boolean disconnectFromService(String linkTag, String sessionKey, long time, String cause) {
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

        return doDisconnectLink(channel, linkTag, sessionKey, time, cause);
    }

    @Override
    public ChannelInfo disconnectFromLocal(Channel channel, String cause) {
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
        if (!doDisconnectLink(channel, linkTag, sessionKey, System.currentTimeMillis(), cause)) {
            return null;
        }

        return new ChannelInfo().setLinkTag(linkTag).setSessionKey(sessionKey);
    }

    private boolean doDisconnectLink(Channel channel, String linkTag, String sessionKey, Long time, String cause) {
        try {
            DeviceTypeEnum deviceTypeEnum = NettyUtils.getDeviceType(channel);
            logger.info("doDisconnectLink is called, linkTag={},sessionKey={},deviceTypeEnum={},cause={}.",
                    linkTag, sessionKey, deviceTypeEnum, cause);

            channel.flush();
            brokerMetrics.decLinkCount(NettyUtils.getChannelAuth(channel));

            //传递变更消息
            LinkChangeModel linkChangeModel = new LinkChangeModel();
            linkChangeModel.setLinkTag(linkTag);
            linkChangeModel.setTimeStamp(time);
            linkChangeModel.setChangeTypeEnum(ChangeTypeEnum.LINK_DISCONNECTED);
            linkChangeModel.setNodeTag(nodeUtil.getNodeTag());
            linkChangeModel.setSessionKey(sessionKey);
            linkChangeModel.setPort(nodeUtil.getPort());
            linkChangeModel.setSignatureTag(NettyUtils.getSignatureTag(channel));
            linkChangeModel.setDeviceType(deviceTypeEnum);
            deliverRawService.deliverLinkChangeMsg(linkChangeModel);
        } catch (Exception e) {
            logger.error("error occur when doDisconnectLink. {}, linkTag={}", e.getMessage(), linkTag);
            return false;
        } finally {
            NettyUtils.asyncCloseChannel(downStreamHandler.sendError(channel, cause));
        }
        logger.info("doDisconnectLink link linkTag:{} finished, deviceCount = {}, channel:{}",
                linkTag, brokerMetrics.getLinkAllCount(), channel);
        return true;
    }

    @Override
    public void disconnectAllLink() {
        logger.info("disconnectAllClient is called");
        linkSession.executeForEachLink((linkInfo) ->
                disconnectFromLocal(linkInfo.getChannel(), "server restart"));
    }

    @Override
    public boolean reportLinkAlive(Channel channel, Integer keepAlive) {
        // logger.debug("linkAlive method invoked,keepAlive={}", keepAlive);
        String linkTag = NettyUtils.getLinkTag(channel);
        if (!StringUtils.isBlank(linkTag)) {
            bizProcessExecutors.submitConnTask(linkTag, () -> {
                LinkChangeModel linkChangeModel = new LinkChangeModel()
                        .setLinkTag(linkTag)
                        .setNodeTag(nodeUtil.getNodeTag())
                        .setPort(nodeUtil.getPort())
                        .setDeviceType(NettyUtils.getDeviceType(channel))
                        .setSignatureTag(NettyUtils.getSignatureTag(channel))
                        .setKeepAliveSeconds(keepAlive)
                        .setTimeStamp(System.currentTimeMillis())
                        .setSessionKey(NettyUtils.getSessionKey(channel))
                        .setChangeTypeEnum(ChangeTypeEnum.LINK_ALIVE);
                deliverRawService.deliverLinkChangeMsg(linkChangeModel);
            });
        } else {
            NettyUtils
                    .asyncCloseChannel(downStreamHandler.sendError(channel, "linkTag is blank when trigger linkAlive"));
            return false;
        }
        return true;
    }


    @Override
    public boolean linkLocalRecord(String linkTag, String sessionKey, DeviceTypeEnum deviceType, LinkInfo linkInfo,
                                   String signatureTag) {
        LinkInfo preLinkInfo = linkSession.recordLink(linkTag, linkInfo);
        if (preLinkInfo != null) {
            //当前链路认证完成，发现本地仍存在链路信息，需下线老链路，当前链路登录失败
            logger.warn("do local record,but preLinkInfo is not null pre={},new={}", preLinkInfo, linkInfo);
            disconnectFromLocal(preLinkInfo.getChannel(), "a new login for the same client");
            return false;
        }

        //本地记录成功，传递登录消息
        LinkChangeModel linkChangeModel = new LinkChangeModel();
        linkChangeModel.setLinkTag(linkTag);
        linkChangeModel.setSessionKey(sessionKey);
        linkChangeModel.setDeviceType(deviceType);
        linkChangeModel.setTimeStamp(System.currentTimeMillis());
        linkChangeModel.setChangeTypeEnum(ChangeTypeEnum.LINK_CONNECTED);
        linkChangeModel.setNodeTag(nodeUtil.getNodeTag());
        linkChangeModel.setPort(nodeUtil.getPort());
        linkChangeModel.setSignatureTag(signatureTag);

        deliverRawService.deliverLinkChangeMsg(linkChangeModel);
        return true;
    }

    @Override
    public ChannelInfo loginLocalCheck(String linkTag) {
        LinkInfo linkInfo = linkSession.getLinkInfo(linkTag);
        if (linkInfo != null) {
            return disconnectFromLocal(linkInfo.getChannel(), "duplicate link found while device login.");
        }
        return null;
    }

    @Override
    public int getLinkCount() {
        return brokerMetrics.getLinkAllCount();
    }
}
