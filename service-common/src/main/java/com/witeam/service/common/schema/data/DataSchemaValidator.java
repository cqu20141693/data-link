package com.witeam.service.common.schema.data;

import com.alibaba.fastjson.JSONObject;
import com.witeam.service.common.schema.data.exception.DataSchemaErrorCode;
import com.witeam.service.common.schema.data.exception.DataSchemaException;
import com.witeam.service.common.schema.data.field.base.DataSchemaField;
import com.witeam.service.common.schema.data.field.base.DataSchemaFieldValidateChooser;
import com.witeam.service.common.schema.data.validate.DataValidateResult;

public class DataSchemaValidator {
    private DataSchemaField<?> dataSchemaField;

    private String rawSchemaStr;

    public void init(String defineString) throws DataSchemaException {
        this.dataSchemaField = DataSchemaParser.parse(defineString);
        rawSchemaStr = defineString;
    }

    public void init(JSONObject define) throws DataSchemaException {
        this.dataSchemaField = DataSchemaParser.parse(define);
        rawSchemaStr = define.toJSONString();
    }

    public DataValidateResult validate(Object data) {
        if (dataSchemaField == null) {
            return DataValidateResult.error("", DataSchemaErrorCode.DATA_PARSE_ERROR);
        }
        return directValidate(dataSchemaField, data);
    }

    public DataValidateResult directValidate(String defineString, Object data) {
        try {
            DataSchemaField<?> dataSchemaField = DataSchemaParser.parse(defineString);
            return directValidate(dataSchemaField, data);
        } catch (DataSchemaException e) {
            return DataValidateResult.error("", DataSchemaErrorCode.DATA_PARSE_ERROR);
        }
    }

    public DataValidateResult directValidate(JSONObject define, Object data) {
        try {
            DataSchemaField<?> dataSchemaField = DataSchemaParser.parse(define);
            return directValidate(dataSchemaField, data);
        } catch (DataSchemaException e) {
            return DataValidateResult.error("", DataSchemaErrorCode.DATA_PARSE_ERROR);
        }
    }

    public DataValidateResult directValidate(DataSchemaField<?> dataSchemaField, Object data) {
        if (dataSchemaField == null) {
            return DataValidateResult.error("", DataSchemaErrorCode.DATA_PARSE_ERROR);
        }
        return DataSchemaFieldValidateChooser.dataSelectValidate(dataSchemaField, data);
    }

    public String getRawSchemaStr() {
        return rawSchemaStr;
    }
}
