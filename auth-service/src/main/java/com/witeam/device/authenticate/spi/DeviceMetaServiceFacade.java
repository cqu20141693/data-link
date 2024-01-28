package com.witeam.device.authenticate.spi;

import com.witeam.device.authenticate.spi.model.DeviceDTO;

/**
 * @author gow 2024/01/24
 */
public interface DeviceMetaServiceFacade {
    DeviceDTO getDevice(String var1, String var2);

    DeviceDTO getDeviceByLoginKey(String userName, String clientIdentifier);

    DeviceDTO getDevice(String var1);
}
