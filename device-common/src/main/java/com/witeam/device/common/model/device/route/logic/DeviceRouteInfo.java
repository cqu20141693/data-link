package com.witeam.device.common.model.device.route.logic;

import com.witeam.service.common.biz.DevicePlatformTypeEnum;
import lombok.Data;

import java.util.Objects;

/**
 * @author gow 2024/01/23
 */

@Data
public class DeviceRouteInfo {
    private String groupKey;
    private String sn;
    private DevicePlatformTypeEnum devicePlatformTypeEnum;

    public DeviceRouteInfo(String groupKey, String sn) {
        this.groupKey = groupKey;
        this.sn = sn;
    }
    public String getGroupKey() {
        return this.groupKey;
    }

    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }

    public String getSn() {
        return this.sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public DevicePlatformTypeEnum getDevicePlatformTypeEnum() {
        return this.devicePlatformTypeEnum;
    }

    public void setDevicePlatformTypeEnum(DevicePlatformTypeEnum devicePlatformTypeEnum) {
        this.devicePlatformTypeEnum = devicePlatformTypeEnum;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            DeviceRouteInfo that = (DeviceRouteInfo)o;
            return Objects.equals(this.groupKey, that.groupKey) && Objects.equals(this.sn, that.sn);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.groupKey, this.sn});
    }
}
