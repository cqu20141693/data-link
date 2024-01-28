package com.witeam.service.common.http;

import com.witeam.service.common.call.IErrorCode;

//100000 - 199999
public enum HttpCodeType implements IErrorCode {

    NOT_LOGIN("100403", "未登录");

    private String code;

    private String desc;

    HttpCodeType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return this.code;
    }


    public String getDesc() {
        return desc;

    }
}
