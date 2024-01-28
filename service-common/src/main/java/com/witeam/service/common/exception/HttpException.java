package com.witeam.service.common.exception;

import com.witeam.service.common.call.IErrorCode;

public class HttpException extends RuntimeException {
    private String errorCode;

    public HttpException(String errorCode, String msg) {
        super(msg);
        this.errorCode = errorCode;
    }

    public HttpException(IErrorCode errorCode) {
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
