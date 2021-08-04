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

import com.chongctech.device.common.model.device.base.DeviceTypeEnum;
import com.chongctech.device.link.biz.model.link.ChannelAuth;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.AttributeKey;

/**
 * Some Netty's channels utilities.
 *
 * @author andrea
 */
public class NettyUtils {

    private static final String ATTR_LINK_TAG = "linkTag";
    private static final String ATTR_SESSION_KEY = "sessionKey";
    private static final String ATTR_KEEP_ALIVE_SECONDS = "keepAliveSeconds";
    private static final String ATTR_MQTT_CHANNEL_AUTH = "channelAuth";
    private static final String ATTR_MQTT_DEVICE_TYPE = "deviceType";
    private static final String ATTR_MQTT_CHANNEL_STATUS = "channelStatus";
    private static final String ATTR_SIGNATURE_TAG = "signTag";

    private static final AttributeKey<String> ATTR_KEY_LINK_TAG = AttributeKey.valueOf(ATTR_LINK_TAG);
    private static final AttributeKey<String> ATTR_KEY_SESSION_KEY = AttributeKey.valueOf(ATTR_SESSION_KEY);
    private static final AttributeKey<Integer> ATTR_KEY_ALIVE_SECONDS = AttributeKey.valueOf(ATTR_KEEP_ALIVE_SECONDS);
    private static final AttributeKey<String> ATTR_KEY_SIGNATURE_TAG = AttributeKey.valueOf(ATTR_SIGNATURE_TAG);
    private static final AttributeKey<ChannelAuth> ATTR_KEY_CHANNEL_AUTH = AttributeKey.valueOf(ATTR_MQTT_CHANNEL_AUTH);
    private static final AttributeKey<DeviceTypeEnum> ATTR_KEY_DEVICE_TYPE = AttributeKey.valueOf(ATTR_MQTT_DEVICE_TYPE);

    private static final AttributeKey<Integer> ATTR_KEY_CHANNEL_STATUS = AttributeKey.valueOf(ATTR_MQTT_CHANNEL_STATUS);

    public static void recordChannelAuth(Channel channel, ChannelAuth channelAuth) {
        channel.attr(ATTR_KEY_CHANNEL_AUTH).set(channelAuth);
    }

    public static ChannelAuth getChannelAuth(Channel channel) {
        return channel.attr(ATTR_KEY_CHANNEL_AUTH).get();
    }

    public static void recordDeviceType(Channel channel, DeviceTypeEnum deviceTypeEnum) {
        channel.attr(ATTR_KEY_DEVICE_TYPE).set(deviceTypeEnum);
    }

    public static DeviceTypeEnum getDeviceType(Channel channel) {
        return channel.attr(ATTR_KEY_DEVICE_TYPE).get();
    }

    public static void recordLinkTag(Channel channel, String linkTag) {
        channel.attr(ATTR_KEY_LINK_TAG).set(linkTag);
    }

    public static String getLinkTag(Channel channel) {
        return channel.attr(ATTR_KEY_LINK_TAG).get();
    }

    public static void recordSessionKey(Channel channel, String sessionKey) {
        channel.attr(ATTR_KEY_SESSION_KEY).set(sessionKey);
    }

    public static String getSessionKey(Channel channel) {
        return channel.attr(ATTR_KEY_SESSION_KEY).get();
    }

    public static void recordKeepAliveSeconds(Channel channel, int keepAliveSeconds) {
        channel.attr(ATTR_KEY_ALIVE_SECONDS).set(keepAliveSeconds);
    }

    public static int getKeepAliveSeconds(Channel channel) {
        return channel.attr(ATTR_KEY_ALIVE_SECONDS).get();
    }

    public static void recordSignatureTag(Channel channel, String signatureTag) {
        channel.attr(ATTR_KEY_SIGNATURE_TAG).set(signatureTag);
    }

    public static String getSignatureTag(Channel channel) {
        return channel.attr(ATTR_KEY_SIGNATURE_TAG).get();
    }


    public static Boolean tryInitStatus(Channel channel) {
        return channel.attr(ATTR_KEY_CHANNEL_STATUS).setIfAbsent(ChannelStatusEnum.INIT.getCode()) != null;
    }

    public static void validStatus(Channel channel) {
        channel.attr(ATTR_KEY_CHANNEL_STATUS).set(ChannelStatusEnum.VALID.getCode());
    }

    public static boolean tryInvalidStatus(Channel channel) {
        return channel.attr(ATTR_KEY_CHANNEL_STATUS)
                .compareAndSet(ChannelStatusEnum.VALID.getCode(), ChannelStatusEnum.INVALID.getCode());
    }

    public static boolean isValidStatus(Channel channel) {
        return ChannelStatusEnum.VALID.getCode().equals(channel.attr(ATTR_KEY_CHANNEL_STATUS).get());
    }

    public static byte[] readBytesAndRewind(ByteBuf payload) {
        byte[] payloadContent = new byte[payload.readableBytes()];
        int mark = payload.readerIndex();
        payload.readBytes(payloadContent);
        payload.readerIndex(mark);
        return payloadContent;
    }

    public static void asyncCloseChannel(ChannelFuture channelFuture) {
        if (channelFuture != null) {
            channelFuture.addListener((ChannelFutureListener) future -> channelFuture.channel().close());
        }
    }
}
