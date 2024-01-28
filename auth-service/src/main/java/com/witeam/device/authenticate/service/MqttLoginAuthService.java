package com.witeam.device.authenticate.service;

import com.witeam.device.common.model.authenticate.req.domain.mqtt.MqttLoginAuthResponse;
import com.witeam.service.common.call.CommonResult;

/**
 * @author gow 2024/01/23
 */
public interface MqttLoginAuthService {
    CommonResult<MqttLoginAuthResponse> auth(String var1, String var2, String var3, String var4, String var5, Integer var6, Integer var7);
}