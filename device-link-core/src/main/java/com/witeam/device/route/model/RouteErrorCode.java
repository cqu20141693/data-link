package com.witeam.device.route.model;

import com.witeam.service.common.call.IErrorCode;

/**
 * @author gow 2024/01/23
 */
public enum RouteErrorCode implements IErrorCode {
    BROKER_OFFLINE_ERROR("route:00001", "broker service offline error");

    private String code;
    private String desc;

    private RouteErrorCode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }
}