package com.chongctech.device.link.biz.session;

import com.chongctech.device.common.model.device.base.CmdStatus;
import com.chongctech.device.common.model.device.deliver.raw.SendActionModel;
import com.chongctech.device.common.util.cache.CacheCleaner;
import com.chongctech.device.link.biz.model.link.SendInfo;
import com.chongctech.device.link.spi.deliver.DeliverRawService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.github.benmanes.caffeine.cache.RemovalCause;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 下行消息等待session
 */
@Slf4j
@Component
public class Qos1Session {
    @Autowired
    private CacheCleaner cacheCleaner;

    @Autowired
    private DeliverRawService deliverRawService;

    /**
     * CommandKey ---> CommandToken
     * 用于存储已经下发给设备的命令信息 qos1
     * 默认命令信息缓存60秒，默认有效期为60秒
     */
    private Cache<Qos1Key, SendInfo> cacheQos1SendCache;

    private final long DEFAULT_EXPIRE_TIME= TimeUnit.SECONDS.toNanos(10);
    private final long MAX_EXPIRE_TIME= TimeUnit.SECONDS.toNanos(30);
    @PostConstruct
    public void init() {
        //初始化命令缓存
        cacheQos1SendCache = Caffeine.newBuilder()
                .expireAfter(new Expiry<Qos1Key, SendInfo>() {
                    @Override
                    public long expireAfterCreate(@NonNull Qos1Key key, @NonNull SendInfo value, long currentTime) {
                        return (value.getAckWaitTime() <= 0 || value.getAckWaitTime() > MAX_EXPIRE_TIME) ? DEFAULT_EXPIRE_TIME :
                                value.getAckWaitTime();
                    }

                    @Override
                    public long expireAfterUpdate(@NonNull Qos1Key key, @NonNull SendInfo value, long currentTime,
                                                  @NonNegative long currentDuration) {
                        return currentDuration;
                    }

                    @Override
                    public long expireAfterRead(@NonNull Qos1Key key, @NonNull SendInfo value, long currentTime,
                                                @NonNegative long currentDuration) {
                        return currentDuration;
                    }
                })
                .removalListener((qos1Key, sendInfo, removalCause) -> {
                    if (removalCause == RemovalCause.EXPIRED && sendInfo != null) {
                        if (deliverRawService != null) {
                            SendActionModel sendActionModel = new SendActionModel();
                            sendActionModel.setLinkTag(sendInfo.getLinkTag());
                            sendActionModel.setBizId(sendInfo.getBizId());
                            sendActionModel.setTimeStamp(System.currentTimeMillis());
                            sendActionModel.setActionType(CmdStatus.ACK_EXPIRE);
                            deliverRawService.deliverSendActionMsg(sendActionModel);
                        }

                        log.info("receive qos1 ack expired. linkTag = {},  bizId={}",
                                sendInfo.getLinkTag(), sendInfo.getBizId());
                    }
                })
                .build();

        // 每分钟在后台做一次缓存清理
        cacheCleaner.registerCycleCleanJob(cacheQos1SendCache, 10);
    }

    public void add(String linkTag, Integer msgId, SendInfo sendInfo) {
        Qos1Key key = new Qos1Key();
        key.setMsgId(msgId);
        key.setLinkTag(linkTag);
        cacheQos1SendCache.put(key, sendInfo);
    }

    public void remove(String linkTag, Integer msgId) {
        Qos1Key key = new Qos1Key();
        key.setMsgId(msgId);
        key.setLinkTag(linkTag);
        cacheQos1SendCache.invalidate(key);
    }

    public SendInfo get(String linkTag, Integer msgId) {
        Qos1Key key = new Qos1Key();
        key.setMsgId(msgId);
        key.setLinkTag(linkTag);
        return cacheQos1SendCache.getIfPresent(key);
    }

    private static class Qos1Key {
        private String linkTag;

        private Integer msgId;

        public String getLinkTag() {
            return linkTag;
        }

        public void setLinkTag(String linkTag) {
            this.linkTag = linkTag;
        }

        public Integer getMsgId() {
            return msgId;
        }

        public void setMsgId(Integer msgId) {
            this.msgId = msgId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Qos1Key qos1Key = (Qos1Key) o;
            return Objects.equals(linkTag, qos1Key.linkTag) &&
                    Objects.equals(msgId, qos1Key.msgId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(linkTag, msgId);
        }
    }
}
