package com.witeam.device.common.model.device.route.link;

import com.witeam.service.common.biz.DevicePlatformTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author gow 2024/01/23
 */
@Data
@Accessors(chain = true)
public class ThirdLinkRouteInfo {

    private String groupKey;
    private String sn;
    private DevicePlatformTypeEnum devicePlatformTypeEnum;

}
