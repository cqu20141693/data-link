package com.chongctech.starter.spi.impl.device.link.deliver;

import com.alibaba.fastjson.JSONObject;
import com.chongctech.device.common.model.device.deliver.raw.LinkChangeModel;
import com.chongctech.device.common.model.device.deliver.raw.PublishMessageModel;
import com.chongctech.device.common.model.device.deliver.raw.SendActionModel;
import com.chongctech.device.link.spi.deliver.DeliverRawService;
import com.chongctech.pulsar.core.domain.ProducerRecord;
import com.chongctech.pulsar.core.producer.StringProducerTemplate;
import com.chongctech.starter.config.PulsarOutTopicConfig;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.MessageId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author gow
 * @date 2021/7/13
 * // todo exception handle and log record
 */
@Component
@Slf4j
public class PulsarDeliver implements DeliverRawService {


    @Autowired
    private StringProducerTemplate stringProducerTemplate;

    @Autowired
    private PulsarOutTopicConfig outTopicConfig;

    @Override
    public void deliverLinkChangeMsg(LinkChangeModel linkChangeModel) {
        ProducerRecord<String> record =
                new ProducerRecord<>(outTopicConfig.getLinkChangeTopic(), linkChangeModel.getLinkTag(),
                        JSONObject.toJSONString(linkChangeModel));
        CompletableFuture<MessageId> future = stringProducerTemplate.sendAsync(record);
        handle("linkChangeModel", linkChangeModel, future);
    }

    @Override
    public boolean deliverPublishMsg(PublishMessageModel publishMessageModel) {
        ProducerRecord<String> record =
                new ProducerRecord<>(outTopicConfig.getPublishTopic(), publishMessageModel.getLinkTag(),
                        JSONObject.toJSONString(publishMessageModel));
        CompletableFuture<MessageId> future = stringProducerTemplate.sendAsync(record);
        handle("publishMessageModel", publishMessageModel, future);
        return true;
    }

    @Override
    public void deliverSendActionMsg(SendActionModel sendActionModel) {
        ProducerRecord<String> record =
                new ProducerRecord<>(outTopicConfig.getSendActionTopic(), sendActionModel.getLinkTag(),
                        JSONObject.toJSONString(sendActionModel));
        CompletableFuture<MessageId> future = stringProducerTemplate.sendAsync(record);
        handle("sendActionModel", sendActionModel, future);

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

}
