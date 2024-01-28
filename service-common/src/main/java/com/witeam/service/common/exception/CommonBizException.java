package com.witeam.service.common.exception;

public class CommonBizException extends Exception {
    public CommonBizException() {
    }

    public CommonBizException(String message) {
        super(message);
    }

    public CommonBizException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommonBizException(Throwable cause) {
        super(cause);
    }

    public CommonBizException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
