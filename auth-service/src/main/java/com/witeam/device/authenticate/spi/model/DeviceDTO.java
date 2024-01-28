package com.witeam.device.authenticate.spi.model;

import com.witeam.service.common.biz.DevicePlatformTypeEnum;
import com.witeam.service.common.biz.DeviceTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author gow 2024/01/24
 */
@Data
@Accessors(chain = true)
public class DeviceDTO {
    private Integer deviceId;
    private Date createTime;
    private Date modifiedTime;
    private String groupKey;
    private String sn;
    private String deviceKey;
    private String deviceToken;
    private DeviceTypeEnum type;
    private DevicePlatformTypeEnum devicePlatformTypeEnum;
    private String name;
    private String description;
}
