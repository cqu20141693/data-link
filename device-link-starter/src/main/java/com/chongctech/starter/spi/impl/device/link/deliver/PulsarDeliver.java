package com.chongctech.starter.spi.impl.device.link.deliver;

import com.alibaba.fastjson.JSONObject;
import com.chongctech.device.common.model.device.deliver.raw.LinkChangeModel;
import com.chongctech.device.common.model.device.deliver.raw.PublishMessageModel;
import com.chongctech.device.common.model.device.deliver.raw.SendActionModel;
import com.chongctech.device.link.spi.deliver.DeliverRawService;
import com.chongctech.starter.config.PulsarConfig;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.AuthenticationFactory;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.ProducerBuilder;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.springframework.stereotype.Component;

/**
 * @author gow
 * @date 2021/7/13
 * // todo exception handle and log record
 */
@Component
@Slf4j
public class PulsarDeliver implements DeliverRawService {

    private Producer<LinkChangeModel> linkChangProducer;
    private Producer<PublishMessageModel> publishProducer;
    private Producer<SendActionModel> sendActionProducer;
    private PulsarClient client;

    private PulsarConfig pulsarConfig;

    public PulsarDeliver(PulsarConfig pulsarConfig) throws PulsarClientException {
        this.pulsarConfig = pulsarConfig;
        buildProducer(pulsarConfig);
    }

    private void buildProducer(PulsarConfig pulsarConfig) throws PulsarClientException {
        if (client == null || client.isClosed()) {
            client = PulsarClient.builder()
                    .serviceUrl(pulsarConfig.getServiceUrl())
                    .authentication(AuthenticationFactory.token(pulsarConfig.getJwtToken()))
                    .ioThreads(pulsarConfig.getIoThread())
                    .build();
        }

        PulsarConfig.ProducerProperties producer = pulsarConfig.getProducer();
        if (linkChangProducer == null || !linkChangProducer.isConnected()) {
            linkChangProducer = config(producer,
                    client.newProducer(Schema.JSON(LinkChangeModel.class)).topic(pulsarConfig.getLinkChangeTopic()));
        }
        if (publishProducer == null || !publishProducer.isConnected()) {
            publishProducer = config(producer,
                    client.newProducer(Schema.JSON(PublishMessageModel.class)).topic(pulsarConfig.getPublishTopic()));
        }
        if (publishProducer == null || !publishProducer.isConnected()) {
            sendActionProducer = config(producer,
                    client.newProducer(Schema.JSON(SendActionModel.class)).topic(pulsarConfig.getSendActionTopic()));
        }
    }

    private <T> Producer<T> config(PulsarConfig.ProducerProperties producer,
                                   ProducerBuilder<T> builder) throws PulsarClientException {
        return builder
                .enableBatching(producer.getBatchingEnabled())
                .blockIfQueueFull(producer.getBlockIfQueueFull())
                .maxPendingMessages(producer.getMaxPendingMessages())
                .batchingMaxPublishDelay(producer.getBatchingMaxPublishDelayMicros(), TimeUnit.MICROSECONDS)
                .batchingMaxMessages(producer.getBatchingMaxMessages())
                .batchingMaxBytes(producer.getBatchMaxBytes())
                .accessMode(producer.getAccessMode())
                .create();
    }


    @Override
    public void deliverLinkChangeMsg(LinkChangeModel linkChangeModel) {
        CompletableFuture<MessageId> future = linkChangProducer.newMessage()
                .eventTime(System.currentTimeMillis())
                .properties(new HashMap<>())
                .key(linkChangeModel.getLinkTag())
                .value(linkChangeModel).sendAsync();
        handle("linkChangeModel", linkChangeModel, future);
    }

    private CompletableFuture<Boolean> handle(String key, Object model,
                                              CompletableFuture<MessageId> future) {
        return future.handle((msgId, ex) -> {
            if (ex != null) {
                log.error("message send async failed:key={},model={} ", key, JSONObject.toJSONString(model));
                return false;
            }
            return true;
        });
    }

    @Override
    public boolean deliverPublishMsg(PublishMessageModel publishMessageModel) {
        CompletableFuture<MessageId> future = publishProducer.newMessage()
                .eventTime(System.currentTimeMillis())
                .properties(new HashMap<>())
                .key(publishMessageModel.getLinkTag())
                .value(publishMessageModel).sendAsync();
        handle("publishMessageModel", publishMessageModel, future);
        return true;
    }

    @Override
    public void deliverSendActionMsg(SendActionModel sendActionModel) {
        CompletableFuture<MessageId> future = sendActionProducer.newMessage()
                .eventTime(System.currentTimeMillis())
                .properties(new HashMap<>())
                .key(sendActionModel.getLinkTag())
                .value(sendActionModel).sendAsync();
        handle("sendActionModel", sendActionModel, future);

    }
}
