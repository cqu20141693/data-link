package com.witeam.service.common.schema.data.exception;

import java.util.HashMap;
import java.util.Map;

public class DataSchemaException extends Exception {
    /** 错误码 */
    private String code;

    /** 扩展字段，当value为空时，用/表示 */
    private Map<String,String> field;

    public DataSchemaException() {
        super();
    }

    public DataSchemaException(String message) {
        super(message);
    }

    public DataSchemaException(String code, String message) {
        super(message);
        this.code = code;
    }

    public DataSchemaException(String code, String message, Map<String,String> field) {
        super(message);
        this.code = code;
        this.field = field;
    }

    public DataSchemaException(String code, String message, String key, String value) {
        super(message);
        this.code = code;
        this.field = new HashMap<>();
        this.field.put(key, value);
    }

    public DataSchemaException(String code, String message, String key) {
        super(message);
        this.code = code;
        this.field = new HashMap<>();
        this.field.put(key, "/");
    }

    public DataSchemaException(DataSchemaErrorCode dataSchemaErrorCode) {
        super(dataSchemaErrorCode.getMsg());
        this.code = dataSchemaErrorCode.getErrorCode();
    }

    public DataSchemaException(DataSchemaErrorCode dataSchemaErrorCode, String key, String value) {
        super(dataSchemaErrorCode.getMsg());
        this.code = dataSchemaErrorCode.getErrorCode();
        this.field = new HashMap<>();
        this.field.put(key, value);
    }

    public DataSchemaException(DataSchemaErrorCode dataSchemaErrorCode, String key) {
        super(dataSchemaErrorCode.getMsg());
        this.code = dataSchemaErrorCode.getErrorCode();
        this.field = new HashMap<>();
        this.field.put(key, "/");
    }

    public String getCode() {
        return code;
    }

    public Map<String, String> getField()
    {
        return field;
    }
}
