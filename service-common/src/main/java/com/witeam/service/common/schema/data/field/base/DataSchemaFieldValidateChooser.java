package com.witeam.service.common.schema.data.field.base;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.witeam.service.common.schema.data.exception.DataSchemaErrorCode;
import com.witeam.service.common.schema.data.exception.ErrorUtil;
import com.witeam.service.common.schema.data.field.*;
import com.witeam.service.common.schema.data.validate.DataValidateResult;

public class DataSchemaFieldValidateChooser {
    public static DataValidateResult dataSelectValidate(DataSchemaField<?> itemField, Object value) {
        try {
            DataSchemaFieldType schemaItemFieldType = itemField.getType();
            switch (schemaItemFieldType) {
                case INTEGER: {
                    DataSchemaIntegerField schemaIntegerField = (DataSchemaIntegerField) itemField;
                    return schemaIntegerField.dataValidate((Integer) value);
                }
                case LONG: {
                    DataSchemaLongField schemaLongField = (DataSchemaLongField) itemField;
                    return schemaLongField.dataValidate((Long) value);
                }
                case FLOAT: {
                    DataSchemaFloatField schemaFloatField = (DataSchemaFloatField) itemField;
                    return schemaFloatField.dataValidate((Float) value);
                }
                case DOUBLE: {
                    DataSchemaDoubleField schemaDoubleField = (DataSchemaDoubleField) itemField;
                    return schemaDoubleField.dataValidate((Double) value);
                }
                case BOOLEAN: {
                    DataSchemaBooleanField schemaBooleanField = (DataSchemaBooleanField) itemField;
                    return schemaBooleanField.dataValidate((Boolean) value);
                }
                case STRING: {
                    DataSchemaStringField schemaStringField = (DataSchemaStringField) itemField;
                    return schemaStringField.dataValidate((String) value);
                }
                case ARRAY: {
                    DataSchemaArrayField schemaArrayField = (DataSchemaArrayField) itemField;
                    return schemaArrayField.dataValidate((JSONArray) value);
                }
                case OBJECT: {
                    DataSchemaObjectField schemaObjectField = (DataSchemaObjectField) itemField;
                    return schemaObjectField.dataValidate((JSONObject) value);
                }
                default:
                    return DataValidateResult.error(DataSchemaErrorCode.SERVER_ERROR.getMsg(), DataSchemaErrorCode.SERVER_ERROR);
            }
        } catch (Exception e) {
            return DataValidateResult.error(ErrorUtil.errorString(itemField.getName(), DataSchemaErrorCode.DATA_PARSE_ERROR),
                    DataSchemaErrorCode.DATA_PARSE_ERROR);
        }
    }
}
