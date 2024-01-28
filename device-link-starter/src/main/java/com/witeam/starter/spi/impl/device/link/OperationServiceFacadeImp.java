package com.witeam.starter.spi.impl.device.link;

import com.witeam.device.route.link.spi.OperationServiceFacade;
import com.witeam.service.common.call.CommonResult;
import org.springframework.stereotype.Service;

/**
 * @author gow
 * @date 2024/1/28 0028
 */
@Service
public class OperationServiceFacadeImp implements OperationServiceFacade {
    @Override
    public CommonResult<Void> offlineDevice(String linkTag, String session, long timestamp, String reason, String ip,
                                            Integer port) {
        return null;
    }
}
