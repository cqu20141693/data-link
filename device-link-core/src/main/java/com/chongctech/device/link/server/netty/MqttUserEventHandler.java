/*
 * Copyright (c) 2012-2015 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */
package com.chongctech.device.link.server.netty;

import com.chongctech.device.link.biz.executor.BizProcessExecutors;
import com.chongctech.device.link.biz.link.LinkStatusHandler;
import com.chongctech.device.link.biz.stream.down.DownStreamHandler;
import com.chongctech.device.link.server.netty.domain.ChannelAliveEvent;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Sharable
class MqttUserEventHandler extends ChannelDuplexHandler {
    private static final Logger logger = LoggerFactory.getLogger(MqttUserEventHandler.class);

    private final DownStreamHandler downStreamHandler;
    private final LinkStatusHandler linkStatusHandler;
    private BizProcessExecutors bizProcessExecutors;

    public MqttUserEventHandler(DownStreamHandler downStreamHandler, LinkStatusHandler linkStatusHandler,
                                BizProcessExecutors bizProcessExecutors) {
        this.downStreamHandler = downStreamHandler;
        this.linkStatusHandler = linkStatusHandler;
        this.bizProcessExecutors = bizProcessExecutors;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {

        bizProcessExecutors.submitProcessTask(NettyUtils.getLinkTag(ctx.channel()), () -> {
            if (evt instanceof IdleStateEvent) {
                IdleState e = ((IdleStateEvent) evt).state();
                if (e == IdleState.ALL_IDLE) {
                    //fire a channelInactive to trigger publish of Will
                    String linkTag = NettyUtils.getLinkTag(ctx.channel());
                    if (linkTag != null) {
                        downStreamHandler.sendError(ctx.channel(), "heartbeat is lost!");
                    }
                    logger.debug(
                            "MqttIdleTimeoutHandler::userEventTriggered() is called, linkTag = {}, fireChannelInactive",
                            linkTag);
                    ctx.fireChannelInactive();
                    ctx.close();
                }
            } else if (evt instanceof ChannelAliveEvent) {
                this.linkStatusHandler
                        .reportLinkAlive(ctx.channel(), ((ChannelAliveEvent) evt).getChannelAliveCheckTime());
            } else {
                try {
                    super.userEventTriggered(ctx, evt);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
