package com.chongctech.device.link.biz.model.link;

import io.netty.channel.Channel;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class LinkInfo {
    // packetId
    private final AtomicInteger nextMessageId;
    private final Integer MAX_ID;

    private final Channel channel;

    /**
     * 下线qos=1消息List
     */
    private ConcurrentLinkedQueue<SendInfo> sendInfoList = new ConcurrentLinkedQueue<>();

    public LinkInfo(Channel channel) {
        this(channel, 0xffff);
    }

    public LinkInfo(Channel channel, int maxId) {
        assert 0xffff >= maxId : "maxId must be less than 0xffff";
        this.channel = channel;
        MAX_ID = maxId;
        nextMessageId = new AtomicInteger(new Random(maxId).nextInt(maxId));
    }


    public Channel getChannel() {
        return channel;
    }

    public ConcurrentLinkedQueue<SendInfo> getSendInfoList() {
        return sendInfoList;
    }

    public int nextPacketId() {
        this.nextMessageId.compareAndSet(MAX_ID, 1);
        return this.nextMessageId.getAndIncrement();
    }
}
