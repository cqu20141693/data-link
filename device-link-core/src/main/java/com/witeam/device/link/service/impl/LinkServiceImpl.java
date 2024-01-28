package com.witeam.device.link.service.impl;

import com.witeam.device.link.biz.link.LinkStatusHandler;
import com.witeam.device.link.biz.stream.down.DownStreamHandler;
import com.witeam.device.link.service.LinkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LinkServiceImpl implements LinkService {
    /**
     * The constant logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(LinkServiceImpl.class);

    @Autowired
    private DownStreamHandler downStreamHandler;

    @Autowired
    private LinkStatusHandler linkStatusHandler;

    /**
     * 判断链路是否存在
     *
     * @param linkTag 链路标识
     * @return the boolean
     */
    @Override
    public boolean isOnline(String linkTag) {
        try {
            return linkStatusHandler.hasLink(linkTag);
        } catch (Exception e) {
            logger.error("isOnline(...) error! linkTag={}, e={}", linkTag, e);
        }
        return false;
    }

    @Override
    public boolean sendQos1Msg(String linkTag, String bizId, String topic, byte[] content, long ackWaitTime) {
        try {
            return downStreamHandler.sendQos1Msg(linkTag, topic, content, bizId, ackWaitTime);
        } catch (Exception e) {
            logger.error("sendQos1Msg(...) error! linkTag={}, e={}", linkTag, e);
        }
        return false;
    }

    @Override
    public boolean sendQos0Msg(String linkTag, String topic, byte[] content) {
        try {
            return downStreamHandler.sendQos0Msg(linkTag, topic, content);
        } catch (Exception e) {
            logger.error("sendQos0Msg(...) error! linkTag={}, e={}", linkTag, e);
        }
        return false;
    }

    @Override
    public boolean disconnectLink(String linkTag, String sessionKey, long time, String reasonCode) {
        try {
            return linkStatusHandler.disconnectFromService(linkTag, sessionKey, time, reasonCode);
        } catch (Exception e) {
            logger.error("disconnectLink(...) error! linkTag={}, e={}", linkTag, e);
        }
        return false;
    }
}