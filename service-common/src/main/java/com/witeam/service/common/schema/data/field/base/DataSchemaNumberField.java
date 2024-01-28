package com.witeam.service.common.schema.data.field.base;


import com.alibaba.fastjson.JSONObject;
import com.witeam.service.common.schema.data.exception.DataSchemaErrorCode;
import com.witeam.service.common.schema.data.exception.DataSchemaException;
import com.witeam.service.common.schema.data.exception.ErrorUtil;
import com.witeam.service.common.schema.data.validate.DataValidateResult;

public abstract class DataSchemaNumberField<T extends Number & Comparable<T>> extends DataSchemaField<T> {
    private T min;
    private T max;
    private String unit;

    public T getMin() {
        return min;
    }

    public void setMin(T min) {
        this.min = min;
    }

    public T getMax() {
        return max;
    }

    public void setMax(T max) {
        this.max = max;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public DataSchemaField<T> parse(JSONObject define) throws DataSchemaException {
        parseBasic(define);

        this.setUnit(define.getString(DataSchemaFieldConstants.UNIT));

        parseRange(define);
        return this;
    }

    @Override
    public DataValidateResult dataValidate(T data) {
        if (data == null) {
            return DataValidateResult.error(
                    ErrorUtil.errorString(this.getName(), DataSchemaErrorCode.EMPTY_DATA), DataSchemaErrorCode.EMPTY_DATA);
        }

        //检查大小
        if (data.compareTo(this.getMax()) > 0) {
            return DataValidateResult.error(
                    ErrorUtil.errorString(this.getName(), DataSchemaErrorCode.VALUE_MUST_LESS_THAN, this.getMax()),
                    DataSchemaErrorCode.VALUE_MUST_LESS_THAN);
        }
        if (data.compareTo(this.getMin()) < 0) {
            return DataValidateResult.error(
                    ErrorUtil.errorString(this.getName(), DataSchemaErrorCode.VALUE_MUST_GREATER_THAN, this.getMin()),
                    DataSchemaErrorCode.VALUE_MUST_GREATER_THAN);
        }
        return DataValidateResult.success();
    }


    protected abstract void parseRange(JSONObject define) throws DataSchemaException;

}
