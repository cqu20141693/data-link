package com.witeam.device.route.link.service;

import com.witeam.device.common.model.device.route.link.SelfLinkRouteInfo;
import com.witeam.device.common.model.device.route.link.ThirdLinkRouteInfo;
import com.witeam.device.common.model.device.route.logic.DeviceRouteInfo;
import com.witeam.service.common.call.CommonResult;

/**
 * @author gow 2024/01/23
 */
public interface LinkRouteService {
    CommonResult<Void> registerSelfLinkRouteInfo(SelfLinkRouteInfo selfLinkRouteInfo, int keepAliveSeconds);

    SelfLinkRouteInfo getSelfLinkRouteInfo(String var1);

    void refreshSelfLinkRoute(SelfLinkRouteInfo var1, int var2);

    void invalidSelfLinkRoute(SelfLinkRouteInfo var1);

    ThirdLinkRouteInfo getThirdLinkRouteInfo(DeviceRouteInfo var1);
}
