package com.witeam.device.common.model.device.deliver.raw;

import com.witeam.device.common.model.device.base.LinkDeviceType;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author gow 2024/01/23
 */
@Data
@Accessors(chain = true)
public class LinkChangeModel {
    private String linkTag;
    private String nodeTag;
    private String sessionKey;
    private LinkDeviceType deviceType;
    private Integer port;
    private String signatureTag;
    private int keepAliveSeconds;
    private long timeStamp;
    private ChangeTypeEnum changeTypeEnum;
    private String reasonCode;

}
