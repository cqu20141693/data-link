package com.witeam.service.common.schema.data.field;

import com.alibaba.fastjson.JSONObject;
import com.witeam.service.common.schema.data.exception.DataSchemaErrorCode;
import com.witeam.service.common.schema.data.exception.DataSchemaException;
import com.witeam.service.common.schema.data.field.base.DataSchemaFieldConstants;
import com.witeam.service.common.schema.data.field.base.DataSchemaNumberField;
import org.apache.commons.lang3.StringUtils;

public class DataSchemaLongField extends DataSchemaNumberField<Long> {
    @Override
    protected void parseRange(JSONObject define) throws DataSchemaException {
        try {
            String strMax = define.getString(DataSchemaFieldConstants.MAX);
            if (StringUtils.isNotEmpty(strMax)) {
                this.setMax(Long.parseLong(strMax));
            } else {
                this.setMax(Long.MAX_VALUE);
            }

            String strMin = define.getString(DataSchemaFieldConstants.MIN);
            if (StringUtils.isNotEmpty(strMin)) {
                this.setMin(Long.parseLong(strMin));
            } else {
                this.setMin(Long.MIN_VALUE);
            }
        } catch (Exception e) {
            throw new DataSchemaException(DataSchemaErrorCode.BAD_PARAMETERS);
        }
    }
}
