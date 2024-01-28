package com.witeam.device.route.link.service;

import com.witeam.device.route.link.domain.DeviceRouteTableInfo;

/**
 * @author gow 2024/01/23
 */

public interface DeviceRouteTableService {
    DeviceRouteTableInfo addRouteKey(String var1, DeviceRouteTableInfo var2, int var3);

    int refreshRouteKey(String var1, DeviceRouteTableInfo var2, int var3);

    Boolean removeRouteKey(String var1, DeviceRouteTableInfo var2);

    DeviceRouteTableInfo getDeviceRouteInfo(String var1);
}
