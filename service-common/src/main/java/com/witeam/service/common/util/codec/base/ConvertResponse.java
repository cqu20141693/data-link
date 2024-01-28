package com.witeam.service.common.util.codec.base;

public class ConvertResponse<T> {
    private boolean success = true;

    private String failMsg;

    private T convertResult;

    public boolean isSuccess() {
        return success;
    }

    public ConvertResponse<T> setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public String getFailMsg() {
        return failMsg;
    }

    public ConvertResponse<T> setFailMsg(String failMsg) {
        this.failMsg = failMsg;
        return this;
    }

    public T getConvertResult() {
        return convertResult;
    }

    public ConvertResponse<T> setConvertResult(T convertResult) {
        this.convertResult = convertResult;
        return this;
    }
}
