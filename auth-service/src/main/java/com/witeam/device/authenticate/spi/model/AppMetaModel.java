package com.witeam.device.authenticate.spi.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author gow 2024/01/24
 */
@Data
@Accessors(chain = true)
public class AppMetaModel {
    private Integer userId;
    private String appKey;
    private String appToken;
    private boolean enableSubscribe;
    private boolean enableVirtualDevice;
}
