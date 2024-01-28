package com.witeam.device.link.biz.link;

import com.witeam.device.common.model.device.base.LinkDeviceType;
import com.witeam.device.link.biz.model.link.ChannelInfo;
import com.witeam.device.link.biz.model.link.LinkInfo;
import com.witeam.device.link.biz.model.link.LinkSysCode;
import io.netty.channel.Channel;

public interface LinkStatusHandler {
    /**
     * 判别某个链接是否存在
     *
     * @param linkTag linkTag
     * @return true表示存在，false表示不存在
     */
    boolean hasLink(String linkTag);

    /**
     * 由服务发起，需要检测当前链路情况
     *
     * @param linkTag
     * @param sessionKey
     * @param time
     * @param reasonCode
     * @return
     */
    boolean disconnectFromService(String linkTag, String sessionKey, long time, String reasonCode);

    /**
     * 由本地channel发起链路断开，channel强绑定，不用检测sessionKey等。
     *
     * @param channel
     * @param cause
     */
    ChannelInfo disconnectFromLocal(Channel channel, LinkSysCode cause);

    /**
     * 断开所有连接
     */
    void disconnectAllLink();

    /**
     * 链路存活事件
     *
     * @param channel
     * @param keepAlive
     * @return
     */
    boolean reportLinkAlive(Channel channel, Integer keepAlive);

    /**
     * 链接登录记录，
     *
     * @param linkTag
     * @param linkInfo
     * @param signatureTag
     * @return 是否成功
     */
    boolean linkLocalRecord(String linkTag, String sessionKey, LinkDeviceType deviceType, LinkInfo linkInfo,
                            String signatureTag);

    /**
     * 本地记录表检查，
     *
     * @param linkTag
     */
    ChannelInfo loginLocalCheck(String linkTag);

    /**
     * 返回当前broker上的客户端链路的数量
     */
    int getLinkCount();
}
