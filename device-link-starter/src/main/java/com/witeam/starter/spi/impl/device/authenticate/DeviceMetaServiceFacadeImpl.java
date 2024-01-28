package com.witeam.starter.spi.impl.device.authenticate;

import com.witeam.device.authenticate.spi.DeviceMetaServiceFacade;
import com.witeam.device.authenticate.spi.model.DeviceDTO;
import org.springframework.stereotype.Component;

/**
 * @author gow 2024/01/24
 */
@Component
public class DeviceMetaServiceFacadeImpl implements DeviceMetaServiceFacade {
    @Override
    public DeviceDTO getDevice(String var1, String var2) {
        return null;
    }

    @Override
    public DeviceDTO getDeviceByLoginKey(String userName, String clientIdentifier) {
        return null;
    }

    @Override
    public DeviceDTO getDevice(String var1) {
        return null;
    }
}
