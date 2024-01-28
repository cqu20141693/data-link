package com.witeam.device.route.link.spi;

import com.witeam.service.common.call.CommonResult;

/**
 * @author gow 2024/01/23
 */
public interface OperationServiceFacade {
    CommonResult<Void> offlineDevice(String linkTag, String session, long timestamp, String reason, String ip, Integer port);
}
