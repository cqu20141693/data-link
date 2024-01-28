package com.witeam.starter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author gow
 * @date 2021/7/22
 */
@Configuration
@ConfigurationProperties(prefix = "pulsar.topic.out")
@Data
public class PulsarOutTopicConfig {
    private String linkChangeTopic;
    private String publishTopic;
    private String sendActionTopic;
}
