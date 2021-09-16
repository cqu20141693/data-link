package com.chongctech.device.link.spi.deliver;

import com.chongctech.device.common.model.device.deliver.raw.LinkChangeModel;
import com.chongctech.device.common.model.device.deliver.raw.PublishMessageModel;
import com.chongctech.device.common.model.device.deliver.raw.SendActionModel;

public interface DeliverRawService {
    /**
     * 传递链路变更消息
     *
     * @param linkChangeModel 链路事件
     */
    void deliverLinkChangeMsg(LinkChangeModel linkChangeModel);

    /**
     * 传递Publish消息
     *
     * @param publishMessageModel 推送数据
     */
    void deliverPublishMsg(PublishMessageModel publishMessageModel);

    /**
     * 传递下行动作消息
     *
     * @param sendActionModel 下发命令
     */
    void deliverSendActionMsg(SendActionModel sendActionModel);
}
