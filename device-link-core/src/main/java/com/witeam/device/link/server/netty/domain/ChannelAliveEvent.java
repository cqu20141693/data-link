package com.witeam.device.link.server.netty.domain;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author gow
 * @date 2021/6/18
 */
@Data
@Accessors(chain = true)
public class ChannelAliveEvent {
    /**
     * channel Alive check seconds
     */
    private Integer channelAliveCheckTime;

}
