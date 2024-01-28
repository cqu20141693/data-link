package com.witeam.device.common.model.authenticate.req.domain.mqtt;

import com.witeam.device.common.model.device.base.LinkDeviceType;
import com.witeam.device.common.model.authenticate.req.domain.model.WelcomeInfo;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author gow 2024/01/23
 */
@Data
@Accessors(chain = true)
public class MqttLoginAuthResponse {
    private MqttChannelType loginType;
    private String groupKey;
    private String sn;
    private String token;
    private LinkDeviceType deviceType;
    private String signatureTag;
    private String linkAddress;
    private WelcomeInfo welcomeInfo;
}
