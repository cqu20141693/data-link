package com.witeam.service.common.call;

public class CommonResult<T> {

    /**
     * 数据
     */
    private T data;

    /**
     * code标记
     */
    private String code;

    /**
     * 返回信息
     */
    private String message;

    public boolean success() {
        return CommonCodeType.SUCCESS.getCode().equals(this.code);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}