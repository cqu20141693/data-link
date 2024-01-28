package com.witeam.service.common.device.protocol;

import com.alibaba.fastjson.JSON;

public class ShadowProtocol {
    private Long version;

    private JSON data;

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public JSON getData() {
        return data;
    }

    public void setData(JSON data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ShadowProtocol{" +
                "version=" + version +
                ", data=" + data +
                '}';
    }
}
