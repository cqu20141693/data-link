package com.witeam.device.link.biz.model.link;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ChannelInfo {
    String linkTag;

    String sessionKey;
}
