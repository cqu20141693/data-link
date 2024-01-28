package com.witeam.device.link.biz;

import com.witeam.device.link.biz.model.link.ChannelAuth;
import com.witeam.device.link.biz.session.LinkSession;
import com.witeam.device.link.config.MetricsConfig;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BrokerMetrics {
    /**
     * 实时的链路数
     */
    private final LongAdder linkAllCount = new LongAdder();

    /**
     * 实时的链路数
     */
    private final LongAdder linkPushCount = new LongAdder();

    /**
     * 实时的链路数
     */
    private final LongAdder linkSubscribeCount = new LongAdder();

    /**
     * 本计时周期内实时的数据条数
     */
    private final AtomicInteger dataCount = new AtomicInteger();

    /**
     * 本计时周期内实时的qos0 send数目
     */
    private final AtomicInteger qos0SendCount = new AtomicInteger();

    /**
     * 本计时周期内实时的qos1 send数目
     */
    private final AtomicInteger qos1SendCount = new AtomicInteger();


    /**
     * 本计时周期内实时的connect req
     */
    private final AtomicInteger connEventCount = new AtomicInteger();

    /**
     * 本计时周期内实时的被关闭的无状态的channel数目
     */
    private final AtomicInteger closedStatelessChannelStats = new AtomicInteger();
    @Autowired
    private MetricsConfig metricsConfig;

    @Autowired
    private LinkSession linkSession;

    @PostConstruct
    public void init() {
        ThreadFactory factory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat("metrics-%d").build();
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1, factory);
        Runnable task = () -> {
            long dataCountLastPeriod = dataCount.getAndSet(0);
            long qos0SendCountLastPeriod = qos0SendCount.getAndSet(0);
            long qos1SendCountLastPeriod = qos1SendCount.getAndSet(0);
            long connCountLastPeriod = connEventCount.getAndSet(0);
            int closedStatelessChannelCount = closedStatelessChannelStats.getAndSet(0);
            log.info("link all count: {}, link push count: {}, link subscribe count: {}," +
                            " last period data count:{},qos0 send count:{},qos1 send count:{},connect event count:{},"
                            + "closed stateless channel count={}", linkAllCount.intValue(), linkPushCount.intValue(),
                    linkSubscribeCount.intValue(), dataCountLastPeriod, qos0SendCountLastPeriod,
                    qos1SendCountLastPeriod, connCountLastPeriod, closedStatelessChannelCount);
            if (metricsConfig.isTraceLink()) {
                linkSession.printTraceLink(metricsConfig.getTraceLinkList());
            }
        };
        scheduledExecutorService.scheduleAtFixedRate(task, 1, metricsConfig.getMetricsPeriod(), TimeUnit.SECONDS);
    }

    public void incLinkCount(ChannelAuth channelAuth) {
        linkAllCount.increment();
        switch (channelAuth) {
            case ONLY_PUSH:
                linkPushCount.increment();
                break;
            case ONLY_SUBSCRIBE:
                linkSubscribeCount.increment();
                break;
            default:
                break;
        }
    }

    public void decLinkCount(ChannelAuth channelAuth) {
        linkAllCount.decrement();
        switch (channelAuth) {
            case ONLY_PUSH:
                linkPushCount.decrement();
                break;
            case ONLY_SUBSCRIBE:
                linkSubscribeCount.decrement();
                break;
            default:
                break;
        }
    }

    public int getLinkAllCount() {
        return linkAllCount.intValue();
    }

    public void incDataCount() {
        dataCount.incrementAndGet();
    }

    public void incQos1SendCount() {
        qos1SendCount.incrementAndGet();
    }

    public void incConnEventCount() {
        connEventCount.incrementAndGet();
    }

    public void incQos0SendCount() {
        qos0SendCount.incrementAndGet();
    }

    public void incClosedStatelessChannelStats() {
        closedStatelessChannelStats.incrementAndGet();
    }
}

