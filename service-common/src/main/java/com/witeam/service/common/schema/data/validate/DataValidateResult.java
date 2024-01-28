package com.witeam.service.common.schema.data.validate;

import com.witeam.service.common.schema.data.exception.DataSchemaErrorCode;

public class DataValidateResult {
    private boolean success = false;

    private String msg;

    /**
     * 错误码.
     */
    private DataSchemaErrorCode dataSchemaErrorCode;

    public DataValidateResult(boolean success) {
        this.success = success;
    }

    public DataValidateResult(String msg, DataSchemaErrorCode dataSchemaErrorCode) {
        this.msg = msg;
        this.dataSchemaErrorCode = dataSchemaErrorCode;
    }

    public static DataValidateResult success() {
        return new DataValidateResult(true);
    }

    public static DataValidateResult error(String msg, DataSchemaErrorCode dataSchemaErrorCode) {
        return new DataValidateResult(msg, dataSchemaErrorCode);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataSchemaErrorCode getDataSchemaErrorCode() {
        return dataSchemaErrorCode;
    }

    public void setDataSchemaErrorCode(DataSchemaErrorCode dataSchemaErrorCode) {
        this.dataSchemaErrorCode = dataSchemaErrorCode;
    }

    @Override
    public String toString() {
        return "ValidateResult{" +
                "success=" + success +
                ", msg='" + msg + '\'' +
                ", schemaErrorCode=" + dataSchemaErrorCode +
                '}';
    }
}
