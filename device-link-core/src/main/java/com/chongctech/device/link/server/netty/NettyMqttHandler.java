/*
 * Copyright (c) 2012-2017 The original author or authors
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


import com.chongctech.device.link.biz.BrokerMetrics;
import com.chongctech.device.link.biz.executor.BizProcessExecutors;
import com.chongctech.device.link.biz.link.LinkStatusHandler;
import com.chongctech.device.link.biz.model.link.LinkSysCode;
import com.chongctech.device.link.biz.stream.down.DownStreamHandler;
import com.chongctech.device.link.biz.stream.up.UpStreamHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPubAckMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import io.netty.handler.codec.mqtt.MqttUnsubscribeMessage;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
@Sharable
@Component
public class NettyMqttHandler extends ChannelInboundHandlerAdapter {
    @Autowired
    private UpStreamHandler upStreamHandler;

    @Autowired
    private DownStreamHandler downStreamHandler;

    @Autowired
    private LinkStatusHandler linkStatusHandler;

    @Autowired
    private BrokerMetrics brokerMetrics;

    @Autowired
    private BizProcessExecutors bizProcessExecutors;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) {
        try {
            if (!(message instanceof MqttMessage)) {
                //非mqtt包
                channelSmartClose(ctx, LinkSysCode.PACKET_ERROR);
                return;
            }
            MqttMessage msg = (MqttMessage) message;
            DecoderResult decoderResult = msg.decoderResult();
            if (decoderResult.isFailure()) {
                //mqtt消息包解码错误
                log.info("invalid mqtt package from channel {} , cause:{}", ctx.channel(), decoderResult.cause());
                channelSmartClose(ctx, LinkSysCode.PACKET_DECODE_ERROR);
            }

            MqttMessageType messageType = msg.fixedHeader().messageType();
            log.debug("Processing MQTT message, type={}", messageType);

            if (messageType == MqttMessageType.CONNECT) {
                if (NettyUtils.tryInitStatus(ctx.channel())) {
                    channelSmartClose(ctx, LinkSysCode.DUPLICATE_CONN_PACKET);
                } else {
                    upStreamHandler.handleConnect(ctx.channel(), (MqttConnectMessage) msg);
                }
            } else {
                if (!NettyUtils.isValidStatus(ctx.channel())) {
                    log.warn("channelRead . none connect msg rec before init. channel: {}, it will be disconnected",
                            ctx.channel());
                    //无效状态链路直接关闭，若链路状态正初始化，则由初始化线程去清理信息
                    NettyUtils.asyncCloseChannel(
                            downStreamHandler.sendError(ctx.channel(), "none connect msg rec before init."));
                    return;
                }
                switch (messageType) {
                    case SUBSCRIBE:
                        upStreamHandler.handleSubscribe(ctx.channel(), (MqttSubscribeMessage) msg);
                        break;
                    case UNSUBSCRIBE:
                        upStreamHandler.handleUnsubscribe(ctx.channel(), (MqttUnsubscribeMessage) msg);
                        break;
                    case PUBLISH:
                        upStreamHandler.handlePublish(ctx.channel(), (MqttPublishMessage) msg);
                        break;
                    case PUBREC:
                        upStreamHandler.handlePubRec(ctx.channel(), msg);
                        break;
                    case PUBCOMP:
                        upStreamHandler.handlePubComp(ctx.channel(), msg);
                        break;
                    case PUBREL:
                        upStreamHandler.handlePubRel(ctx.channel(), msg);
                        break;
                    case DISCONNECT:
                        upStreamHandler.handleDisconnect(ctx.channel());
                        break;
                    case PUBACK:
                        upStreamHandler.handlePubAck(ctx.channel(), (MqttPubAckMessage) msg);
                        break;
                    case PINGREQ:
                        upStreamHandler.handlePingReq(ctx.channel(), msg);
                        break;
                    default:
                        log.info("close channel for receiving unknown mqtt packet type={}", messageType);
                        channelSmartClose(ctx, LinkSysCode.UNKNOWN_PACKET_TYPE);
                        break;
                }
            }
        } catch (Throwable ex) {
            log.error("Exception was caught while processing MQTT message, " + ex.getCause(), ex);
            channelSmartClose(ctx, LinkSysCode.SERVER_PROCESS_ERROR);
        } finally {
            ReferenceCountUtil.release(message);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        closeChannelIfValid(ctx, LinkSysCode.CONNECTION_LOST);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        channelSmartClose(ctx, LinkSysCode.SYS_EXCEPTION_ERROR);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        if (ctx.channel().isWritable()) {
            downStreamHandler.flushChannelWrite(ctx.channel());
        }
        super.channelWritabilityChanged(ctx);
    }

    private void channelSmartClose(ChannelHandlerContext ctx, LinkSysCode cause) {
        if (!closeChannelIfValid(ctx, cause)) {
            //连接未完成，直接关闭链路,多余信息由连接处理线程清理; 已关闭链路则只调用
            NettyUtils.asyncCloseChannel(downStreamHandler.sendError(ctx.channel(), cause.getCode()));

        } else {
            // consider slb probe
            brokerMetrics.incClosedStatelessChannelStats();
        }
    }

    private boolean closeChannelIfValid(ChannelHandlerContext ctx, LinkSysCode cause) {
        if (NettyUtils.isValidStatus(ctx.channel())) {
            //有效链路，存在链路信息;
            Channel channel = ctx.channel();
            if (linkStatusHandler.disconnectFromLocal(channel, cause) != null) {
                log.info("system close channel for {} , channel:{}.", cause, ctx.channel());
                return true;
            }
        }
        return false;
    }
}
