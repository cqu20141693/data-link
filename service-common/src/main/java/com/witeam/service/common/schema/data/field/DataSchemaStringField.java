package com.witeam.service.common.schema.data.field;

import com.alibaba.fastjson.JSONObject;
import com.witeam.service.common.schema.data.exception.DataSchemaErrorCode;
import com.witeam.service.common.schema.data.exception.DataSchemaException;
import com.witeam.service.common.schema.data.exception.ErrorUtil;
import com.witeam.service.common.schema.data.field.base.DataSchemaField;
import com.witeam.service.common.schema.data.field.base.DataSchemaFieldConstants;
import com.witeam.service.common.schema.data.validate.DataValidateResult;

public class DataSchemaStringField extends DataSchemaField<String> {
    private Integer minLength;
    private Integer maxLength;

    public Integer getMinLength() {
        return minLength;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public DataSchemaField<String> parse(JSONObject define) throws DataSchemaException {
        parseBasic(define);
        try {
            Integer maxLength = define.getInteger(DataSchemaFieldConstants.MAX_LENGTH);
            if (maxLength != null) {
                this.setMaxLength(maxLength);
            } else {
                this.setMaxLength(4096);
            }

            Integer minLength = define.getInteger(DataSchemaFieldConstants.MIN_LENGTH);
            if (minLength != null) {
                this.setMinLength(minLength);
            } else {
                this.setMinLength(0);
            }
        } catch (Exception e) {
            throw new DataSchemaException(DataSchemaErrorCode.BAD_PARAMETERS);
        }
        return this;
    }

    @Override
    public DataValidateResult dataValidate(String data) {
        if (data == null) {
            return DataValidateResult.error(
                    ErrorUtil.errorString(this.getName(), DataSchemaErrorCode.EMPTY_DATA), DataSchemaErrorCode.EMPTY_DATA);
        }

        //检查大小
        if (data.length() > this.getMaxLength()) {
            return DataValidateResult.error(
                    ErrorUtil.errorString(this.getName(), DataSchemaErrorCode.MAX_LENGTH, this.getMaxLength()),
                    DataSchemaErrorCode.MAX_LENGTH);
        }
        if (data.length() < this.getMinLength()) {
            return DataValidateResult.error(
                    ErrorUtil.errorString(this.getName(), DataSchemaErrorCode.MIN_LENGTH, this.getMinLength()),
                    DataSchemaErrorCode.MIN_LENGTH);
        }
        return DataValidateResult.success();
    }
}
