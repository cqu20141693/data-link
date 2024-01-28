package com.witeam.service.common.device.protocol;


import com.witeam.service.common.biz.DeviceTag;

import java.util.List;

public class StatusReportProtocol {
    private List<DeviceTag> online;

    private List<DeviceTag> offline;

    public List<DeviceTag> getOnline() {
        return online;
    }

    public void setOnline(List<DeviceTag> online) {
        this.online = online;
    }

    public List<DeviceTag> getOffline() {
        return offline;
    }

    public void setOffline(List<DeviceTag> offline) {
        this.offline = offline;
    }
}
