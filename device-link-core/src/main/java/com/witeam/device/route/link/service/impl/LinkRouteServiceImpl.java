package com.witeam.device.route.link.service.impl;

import com.witeam.device.common.model.device.route.link.SelfLinkRouteInfo;
import com.witeam.device.common.model.device.route.link.ThirdLinkRouteInfo;
import com.witeam.device.common.model.device.route.logic.DeviceRouteInfo;
import com.witeam.device.route.link.domain.DeviceRouteTableInfo;
import com.witeam.device.route.link.service.DeviceRouteTableService;
import com.witeam.device.route.link.service.LinkRouteService;
import com.witeam.device.route.link.spi.OperationServiceFacade;
import com.witeam.device.route.model.RouteErrorCode;
import com.witeam.service.common.call.CommonResult;
import com.witeam.service.common.call.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author gow 2024/01/23
 */

@Component
public class LinkRouteServiceImpl implements LinkRouteService {
    private static final Logger log = LoggerFactory.getLogger(LinkRouteServiceImpl.class);
    @Autowired
    private DeviceRouteTableService deviceRouteTableServiceImpl;
    @Autowired
    @Lazy
    private OperationServiceFacade operationServiceFacade;
    private final Integer delaySeconds = 10;

    public LinkRouteServiceImpl() {
    }

    public CommonResult<Void> registerSelfLinkRouteInfo(SelfLinkRouteInfo selfLinkRouteInfo, int keepAliveSeconds) {
        DeviceRouteTableInfo deviceRouteTableInfo = this.toRouteTBInfo(selfLinkRouteInfo);
        DeviceRouteTableInfo oldInfo = this.deviceRouteTableServiceImpl.addRouteKey(selfLinkRouteInfo.getLinkTag(), deviceRouteTableInfo, keepAliveSeconds + this.delaySeconds);
        if (oldInfo != null) {
            log.info("linkTag={} old={}", selfLinkRouteInfo.getLinkTag(), oldInfo.toRedisValue());
            CommonResult<Void> result = this.operationServiceFacade.offlineDevice(selfLinkRouteInfo.getLinkTag(), oldInfo.getSession(), System.currentTimeMillis(), "duplicate client login on the same link", oldInfo.getIp(), oldInfo.getPort());
            if (!result.success()) {
                return ResultUtil.returnError(RouteErrorCode.BROKER_OFFLINE_ERROR.getCode(), RouteErrorCode.BROKER_OFFLINE_ERROR.getDesc());
            }

            this.deviceRouteTableServiceImpl.removeRouteKey(selfLinkRouteInfo.getLinkTag(), oldInfo);
            this.deviceRouteTableServiceImpl.addRouteKey(selfLinkRouteInfo.getLinkTag(), deviceRouteTableInfo, keepAliveSeconds + this.delaySeconds);
        }

        return ResultUtil.returnSuccess();
    }

    private DeviceRouteTableInfo toRouteTBInfo(SelfLinkRouteInfo selfLinkRouteInfo) {
        return (new DeviceRouteTableInfo()).setIp(selfLinkRouteInfo.getNodeTag()).setPort(selfLinkRouteInfo.getPort()).setSession(selfLinkRouteInfo.getSessionKey()).setType(selfLinkRouteInfo.getType()).setSignatureTag(selfLinkRouteInfo.getSignatureTag());
    }

    public SelfLinkRouteInfo getSelfLinkRouteInfo(String linkTag) {
        DeviceRouteTableInfo deviceRouteInfo = this.deviceRouteTableServiceImpl.getDeviceRouteInfo(linkTag);
        return Optional.ofNullable(deviceRouteInfo).map((info) -> (new SelfLinkRouteInfo()).setLinkTag(linkTag)
                .setNodeTag(deviceRouteInfo.getIp()).setPort(deviceRouteInfo.getPort())
                .setSessionKey(deviceRouteInfo.getSession()).setType(deviceRouteInfo.getType())
                .setSignatureTag(deviceRouteInfo.getSignatureTag())).orElse(null);
    }

    public void refreshSelfLinkRoute(SelfLinkRouteInfo selfLinkRouteInfo, int keepAliveSeconds) {
        this.deviceRouteTableServiceImpl.refreshRouteKey(selfLinkRouteInfo.getLinkTag(), this.toRouteTBInfo(selfLinkRouteInfo), keepAliveSeconds);
    }

    public void invalidSelfLinkRoute(SelfLinkRouteInfo selfLinkRouteInfo) {
        this.deviceRouteTableServiceImpl.removeRouteKey(selfLinkRouteInfo.getLinkTag(), this.toRouteTBInfo(selfLinkRouteInfo));
    }

    public ThirdLinkRouteInfo getThirdLinkRouteInfo(DeviceRouteInfo deviceRouteInfo) {
        ThirdLinkRouteInfo thirdLinkRouteInfo = new ThirdLinkRouteInfo();
        thirdLinkRouteInfo.setGroupKey(deviceRouteInfo.getGroupKey());
        thirdLinkRouteInfo.setSn(deviceRouteInfo.getSn());
        thirdLinkRouteInfo.setDevicePlatformTypeEnum(deviceRouteInfo.getDevicePlatformTypeEnum());
        return thirdLinkRouteInfo;
    }
}

