package com.witeam.service.common.exception;

public class CommonAuthorityException extends Exception {
    public CommonAuthorityException() {
    }

    public CommonAuthorityException(String message) {
        super(message);
    }

    public CommonAuthorityException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommonAuthorityException(Throwable cause) {
        super(cause);
    }

    public CommonAuthorityException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
