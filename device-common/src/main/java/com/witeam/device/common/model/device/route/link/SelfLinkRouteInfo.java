package com.witeam.device.common.model.device.route.link;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author gow 2024/01/23
 */
@Data
@Accessors(chain = true)
public class SelfLinkRouteInfo {

    private String linkTag;
    private String nodeTag;
    private Integer port;
    private String type;
    private String sessionKey;
    private String signatureTag;
}
