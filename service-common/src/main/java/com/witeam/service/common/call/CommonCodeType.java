package com.witeam.service.common.call;

public enum CommonCodeType implements IErrorCode {
    SUCCESS("0000", "success"),

    FAIL("0001", "fail"),

    PARAM_CHECK_ERROR("1001", "参数校验错误"),

    DB_OPERATION_ERROR("1010", "数据库操作异常"),

    AUTHORITY_ERROR("2001", "请检查相应操作权限"),

    BIZ_ERROR("2002", "业务异常，请稍后再试...."),

    UNKNOWN_ERROR("9999", "系统繁忙，请稍后再试....");

    private String code;

    private String desc;

    CommonCodeType(String code, String desc) {
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
