package com.chongctech.starter.config;

import static org.apache.pulsar.client.api.ProducerAccessMode.Shared;
import java.util.concurrent.TimeUnit;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.apache.pulsar.client.api.ProducerAccessMode;
import org.apache.pulsar.client.api.Schema;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author gow
 * @date 2021/7/13
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "com.chongctech.service.pulsar")
public class PulsarConfig {

    private @NotNull String serviceUrl;
    private String jwtToken;
    private ProducerProperties producer = new ProducerProperties();
    private @NotNull String linkChangeTopic;
    private @NotNull String publishTopic;
    private @NotNull String sendActionTopic;
    private Integer ioThread = 3;

    /**
     * @author gow
     * @date 2021/7/2
     */
    @Data
    public static class ProducerProperties {

        private final Schema<String> defaultSchema = Schema.STRING;
        private Boolean batchingEnabled = true;
        private Boolean blockIfQueueFull = false;
        private Integer maxPendingMessages = 1000;
        private Long batchingMaxPublishDelayMicros = TimeUnit.MILLISECONDS.toMicros(10);
        private Integer batchingMaxMessages = 1000;
        // 1M
        private Integer batchMaxBytes = 1024 * 1024;
        private ProducerAccessMode accessMode = Shared;
    }

}
