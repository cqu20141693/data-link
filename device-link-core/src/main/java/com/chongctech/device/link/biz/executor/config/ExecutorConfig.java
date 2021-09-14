package com.chongctech.device.link.biz.executor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "com.chongctech.device.link.executor")
public class ExecutorConfig {
    private int connExecutorCount = 8;

    private int connExecutorQueueSize = 25600;

    private int processExecutorCount = 32;

    private int processExecutorQueueSize = 51200;

    private int threadPoolSize = 40;

    private int threadPoolQueueSize = 1024;
}
