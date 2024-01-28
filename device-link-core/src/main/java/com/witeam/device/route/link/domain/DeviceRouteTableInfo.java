package com.witeam.device.route.link.domain;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author gow 2024/01/23
 */

@Data
@Accessors(chain = true)
public class DeviceRouteTableInfo {
    private String ip;
    private Integer port;
    private String session;
    private String type;
    private String signatureTag;
    public String toRedisValue() {
        return  this.ip + ":" + this.port + ":" + this.session + ":" + this.type + (this.signatureTag != null ? ":" + this.signatureTag : "");
    }

    public DeviceRouteTableInfo fromRedisValue(String value) {
        String[] data = value.split(":");
        this.ip = data[0];
        this.port = Integer.parseInt(data[1]);
        this.session = data[2];
        this.type = data[3];
        if (data.length >= 5) {
            this.signatureTag = data[4];
        }

        return this;
    }

}
