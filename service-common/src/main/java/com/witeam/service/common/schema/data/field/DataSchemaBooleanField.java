package com.witeam.service.common.schema.data.field;

import com.alibaba.fastjson.JSONObject;
import com.witeam.service.common.schema.data.exception.DataSchemaErrorCode;
import com.witeam.service.common.schema.data.exception.DataSchemaException;
import com.witeam.service.common.schema.data.exception.ErrorUtil;
import com.witeam.service.common.schema.data.field.base.DataSchemaField;
import com.witeam.service.common.schema.data.validate.DataValidateResult;

public class DataSchemaBooleanField extends DataSchemaField<Boolean> {
    @Override
    public DataSchemaField<Boolean> parse(JSONObject define) throws DataSchemaException {
        parseBasic(define);
        return this;
    }

    @Override
    public DataValidateResult dataValidate(Boolean data) {
        if (data == null) {
            return DataValidateResult.error(
                    ErrorUtil.errorString(this.getName(), DataSchemaErrorCode.EMPTY_DATA), DataSchemaErrorCode.EMPTY_DATA);
        }
        return DataValidateResult.success();
    }
}
