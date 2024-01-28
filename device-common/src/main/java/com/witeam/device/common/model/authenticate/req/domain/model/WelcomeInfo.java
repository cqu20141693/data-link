package com.witeam.device.common.model.authenticate.req.domain.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author gow 2024/01/23
 */
@Data
@Accessors(chain = true)
public class WelcomeInfo {
    private String welcomeTopic;
    private byte[] welcomeMsg;
}
