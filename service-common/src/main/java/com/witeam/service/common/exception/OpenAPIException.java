package com.witeam.service.common.exception;


import com.witeam.service.common.call.IErrorCode;

public class OpenAPIException extends RuntimeException {
    private String errorCode;

    public OpenAPIException(String errorCode, String msg) {
        super(msg);
        this.errorCode = errorCode;
    }

    public OpenAPIException(IErrorCode errorCode) {
        super(errorCode.getDesc());
        this.errorCode = errorCode.getCode();
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
