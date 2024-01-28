package com.witeam.device.common.model.authenticate.req;

import lombok.Data;

/**
 * @author gow 2024/01/23
 */
@Data
public class MqttLoginAuthReq {
    private String clientIdentifier;
    private String username;
    private String password;
    private String sessionKey;
    private String nodeTag;
    private Integer port;
    private Integer keepAliveSeconds;
}
