package com.witeam.device.common.model.device.deliver.raw;

import com.witeam.device.common.model.device.base.CmdStatus;
import lombok.Data;

/**
 * @author gow 2024/01/23
 */
@Data
public class SendActionModel {
    private String linkTag;
    private long timeStamp;
    private CmdStatus actionType;
    private String bizId;
}
