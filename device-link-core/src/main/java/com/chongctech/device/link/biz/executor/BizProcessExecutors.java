package com.chongctech.device.link.biz.executor;

import com.chongctech.device.link.biz.executor.config.ExecutorConfig;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author McGee_Z
 * @date 2019/11/5 15:04
 */
@Component
public class BizProcessExecutors {
    private static final Logger logger = LoggerFactory.getLogger(BizProcessExecutors.class);

    /**
     * 进行后台处理的线程池
     */
    private final ExecutorService processService;

    private final Executor[] connExecutor;

    private final Executor[] processExecutor;

    private final ExecutorConfig executorConfig;

    private final AtomicInteger processCounter = new AtomicInteger(0);

    public BizProcessExecutors(ExecutorConfig executorConfig) {
        this.executorConfig = executorConfig;

        ThreadFactory processThreadFactory =
                new ThreadFactoryBuilder().setNameFormat("process-%d").setDaemon(true).build();
        processService = new ThreadPoolExecutor(executorConfig.getThreadPoolSize(), executorConfig.getThreadPoolSize(),
                0, TimeUnit.SECONDS, new LinkedBlockingQueue<>(executorConfig.getThreadPoolQueueSize()),
                processThreadFactory, new ThreadPoolExecutor.AbortPolicy());

        int connExecutorCount = executorConfig.getConnExecutorCount();
        connExecutor = new Executor[connExecutorCount];
        for (int i = 0; i < connExecutorCount; i++) {
            connExecutor[i] = new SequenceExecutor(processService, executorConfig.getConnExecutorQueueSize());
        }

        int processExecutorCount = executorConfig.getProcessExecutorCount();
        processExecutor = new Executor[processExecutorCount];
        for (int i = 0; i < processExecutorCount; i++) {
            processExecutor[i] = new SequenceExecutor(processService, executorConfig.getProcessExecutorQueueSize());
        }

    }

    public boolean submitProcessTask(String key, Runnable task) {
        int index;
        if (key == null) {
            this.processCounter.compareAndSet(Integer.MAX_VALUE, 0);
            index = processCounter.getAndIncrement() % executorConfig.getProcessExecutorCount();
        } else {
            index = Math.abs(key.hashCode()) % executorConfig.getProcessExecutorCount();
        }
        try {
            processExecutor[index].execute(task);
            return true;
        } catch (Exception e) {
            logger.info("the process task execute fail , queue may full!");
            return false;
        }
    }

    public boolean submitConnTask(String key, Runnable task) {
        int index = Math.abs(key.hashCode()) % executorConfig.getConnExecutorCount();
        try {
            connExecutor[index].execute(task);
            return true;
        } catch (Exception e) {
            logger.info("the conn task execute fail , queue may full!");
            return false;
        }
    }

    @PreDestroy
    public void shutdown() {
        processService.shutdown();
    }
}
