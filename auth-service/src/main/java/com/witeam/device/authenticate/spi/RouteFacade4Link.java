package com.witeam.device.authenticate.spi;

import com.witeam.device.common.model.device.route.link.SelfLinkRouteInfo;
import com.witeam.service.common.call.CommonResult;

/**
 * @author gow 2024/01/23
 */
public interface RouteFacade4Link {
    CommonResult<Void> registerSelfLinkRouteInfo(SelfLinkRouteInfo var1, int var2);
}
