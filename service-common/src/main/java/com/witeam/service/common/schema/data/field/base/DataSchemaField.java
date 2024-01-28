package com.witeam.service.common.schema.data.field.base;


import com.alibaba.fastjson.JSONObject;
import com.witeam.service.common.schema.data.exception.DataSchemaErrorCode;
import com.witeam.service.common.schema.data.exception.DataSchemaException;
import com.witeam.service.common.schema.data.validate.DataValidateResult;
import org.apache.commons.lang3.StringUtils;

public abstract class DataSchemaField<T> {
    private String name;

    private String desc;

    private DataSchemaFieldType type;

    public DataSchemaField() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public DataSchemaFieldType getType() {
        return type;
    }

    public void setType(DataSchemaFieldType type) {
        this.type = type;
    }

    public abstract DataSchemaField<T> parse(JSONObject define) throws DataSchemaException;

    protected void parseBasic(JSONObject define) throws DataSchemaException {
        String name = define.getString(DataSchemaFieldConstants.NAME);
        if (StringUtils.isNotEmpty(name)) {
            this.setName(name);
        } else {
            throw new DataSchemaException(DataSchemaErrorCode.BAD_PARAMETERS);
        }

        String strType = define.getString(DataSchemaFieldConstants.TYPE);
        DataSchemaFieldType dataSchemaFieldType = DataSchemaFieldType.parseFromName(strType);
        if (dataSchemaFieldType == DataSchemaFieldType.UNKNOWN) {
            throw new DataSchemaException(DataSchemaErrorCode.BAD_PARAMETERS);
        } else {
            this.setType(dataSchemaFieldType);
        }

        this.setDesc(define.getString(DataSchemaFieldConstants.DESC));
    }

    public abstract DataValidateResult dataValidate(T data);

}
