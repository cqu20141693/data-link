package com.witeam.starter.spi.impl.device.link.deliver;

import com.alibaba.fastjson.JSONObject;
import com.witeam.device.common.model.device.deliver.raw.LinkChangeModel;
import com.witeam.device.common.model.device.deliver.raw.PublishMessageModel;
import com.witeam.device.common.model.device.deliver.raw.SendActionModel;
import com.witeam.device.link.spi.deliver.DeliverRawService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author gow 2024/01/22
 */
@Component
@Slf4j
public class LocalFileDeliver implements DeliverRawService {
    @Override
    public void deliverLinkChangeMsg(LinkChangeModel linkChangeModel) {
        log.info("deliverLinkChangeMsg:{}", JSONObject.toJSON(linkChangeModel));
    }

    @Override
    public void deliverPublishMsg(PublishMessageModel publishMessageModel) {
        log.info("deliverPublishMsg:{}", JSONObject.toJSON(publishMessageModel));
    }

    @Override
    public void deliverSendActionMsg(SendActionModel sendActionModel) {
        log.info("deliverSendActionMsg:{}", JSONObject.toJSON(sendActionModel));
    }
}
