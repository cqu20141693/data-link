package com.witeam.service.common.schema.data.field;

import com.alibaba.fastjson.JSONObject;
import com.witeam.service.common.schema.data.exception.DataSchemaErrorCode;
import com.witeam.service.common.schema.data.exception.DataSchemaException;
import com.witeam.service.common.schema.data.field.base.DataSchemaFieldConstants;
import com.witeam.service.common.schema.data.field.base.DataSchemaNumberField;
import org.apache.commons.lang3.StringUtils;

public class DataSchemaIntegerField extends DataSchemaNumberField<Integer> {
    @Override
    protected void parseRange(JSONObject define) throws DataSchemaException {
        try {
            String strMax = define.getString(DataSchemaFieldConstants.MAX);
            if (StringUtils.isNotEmpty(strMax)) {
                this.setMax(Integer.parseInt(strMax));
            } else {
                this.setMax(Integer.MAX_VALUE);
            }

            String strMin = define.getString(DataSchemaFieldConstants.MIN);
            if (StringUtils.isNotEmpty(strMin)) {
                this.setMin(Integer.parseInt(strMin));
            } else {
                this.setMin(Integer.MIN_VALUE);
            }
        } catch (Exception e) {
            throw new DataSchemaException(DataSchemaErrorCode.BAD_PARAMETERS);
        }
    }
}
