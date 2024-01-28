package com.witeam.starter.spi.impl.device.authenticate;

import com.witeam.device.authenticate.spi.RouteFacade4Link;
import com.witeam.device.common.model.device.route.link.SelfLinkRouteInfo;
import com.witeam.device.route.link.service.LinkRouteService;
import com.witeam.service.common.call.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author gow 2024/01/24
 */
@Component
public class RouteFacade4LinkImpl implements RouteFacade4Link {
    @Autowired
    private LinkRouteService linkRouteService;
    @Override
    public CommonResult<Void> registerSelfLinkRouteInfo(SelfLinkRouteInfo linkRouteInfo, int keepAliveSeconds) {
        return linkRouteService.registerSelfLinkRouteInfo(linkRouteInfo,keepAliveSeconds);
    }
}
