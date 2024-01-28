package com.witeam.device.link.config;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @author witeam
 * @date 2019/12/4 16:08
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "com.witeam.device.link.metrics")
@RefreshScope
public class MetricsConfig {
    /**
     * 统计消息数的计时周期(单位为秒)
     */
    private int metricsPeriod = 60;

    /**
     * 是否跟踪链路，线上debug用，尽量别打开
     */
    private boolean traceLink = false;

    /**
     * 指定跟踪链路
     */
    private List<String> traceLinkList;
}
