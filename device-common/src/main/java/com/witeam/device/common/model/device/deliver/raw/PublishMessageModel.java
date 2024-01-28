package com.witeam.device.common.model.device.deliver.raw;

import com.witeam.device.common.model.device.base.LinkDeviceType;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author gow 2024/01/23
 */
@Data
@Accessors(chain = true)
public class PublishMessageModel {
    private String sessionKey;
    private String linkTag;
    private String signatureTag;
    private LinkDeviceType deviceType;
    private long timeStamp;
    private String topic;
    private byte[] payload;

}
