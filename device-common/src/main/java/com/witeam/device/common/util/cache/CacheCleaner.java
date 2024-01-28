package com.witeam.device.common.util.cache;


import com.github.benmanes.caffeine.cache.Cache;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author gow 2024/01/23
 */
@Component
public class CacheCleaner {
    private static final Logger log = LoggerFactory.getLogger(CacheCleaner.class);
    private ScheduledExecutorService service;

    public CacheCleaner() {
    }

    @PostConstruct
    public void init() {
        ThreadFactory cleanThreadFactory = (new ThreadFactoryBuilder()).setNameFormat("cache-clean-%d").setDaemon(true).build();
        this.service = new ScheduledThreadPoolExecutor(2, cleanThreadFactory);
    }

    public void registerCycleCleanJob(Cache<?, ?> cache, Integer cycleSecond) {
        ScheduledExecutorService var10000 = this.service;
        Objects.requireNonNull(cache);
        var10000.scheduleWithFixedDelay(cache::cleanUp, 0L, (long)cycleSecond, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void destroy() {
        log.info("CacheCleaner destroy is called.");
        this.service.shutdown();
    }
}