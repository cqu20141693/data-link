package com.witeam.service.common.schema.data.field;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.witeam.service.common.schema.data.exception.DataSchemaErrorCode;
import com.witeam.service.common.schema.data.exception.DataSchemaException;
import com.witeam.service.common.schema.data.exception.ErrorUtil;
import com.witeam.service.common.schema.data.field.base.DataSchemaField;
import com.witeam.service.common.schema.data.field.base.DataSchemaFieldConstants;
import com.witeam.service.common.schema.data.field.base.DataSchemaFieldType;
import com.witeam.service.common.schema.data.field.base.DataSchemaFieldValidateChooser;
import com.witeam.service.common.schema.data.validate.DataValidateResult;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;

public class DataSchemaArrayField extends DataSchemaField<JSONArray> {
    private DataSchemaField<?> itemDefine;

    private Integer minLength;

    private Integer maxLength;

    public DataSchemaField<?> getItemDefine() {
        return itemDefine;
    }

    public void setItemDefine(DataSchemaField<?> itemDefine) {
        this.itemDefine = itemDefine;
    }

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
    public DataSchemaField<JSONArray> parse(JSONObject define) throws DataSchemaException {
        parseBasic(define);
        try {
            Integer maxLength = define.getInteger(DataSchemaFieldConstants.MAX_LENGTH);
            if (maxLength != null) {
                this.setMaxLength(maxLength);
            } else {
                this.setMaxLength(10);
            }

            Integer minLength = define.getInteger(DataSchemaFieldConstants.MIN_LENGTH);
            if (minLength != null) {
                this.setMinLength(minLength);
            } else {
                this.setMinLength(0);
            }

            JSONObject itemDefine = define.getJSONObject(DataSchemaFieldConstants.ITEM_DEFINE);
            if (itemDefine == null) {
                throw new DataSchemaException(DataSchemaErrorCode.BAD_PARAMETERS);
            }

            String itemDefineType = itemDefine.getString(DataSchemaFieldConstants.TYPE);
            if (StringUtils.isEmpty(itemDefineType)) {
                throw new DataSchemaException(DataSchemaErrorCode.BAD_PARAMETERS);
            }

            DataSchemaFieldType itemDefineFieldType = DataSchemaFieldType.parseFromName(itemDefineType);
            if (itemDefineFieldType == DataSchemaFieldType.UNKNOWN) {
                throw new DataSchemaException(DataSchemaErrorCode.BAD_PARAMETERS);
            }
            DataSchemaField<?> itemDefineDataSchemaField =
                    itemDefineFieldType.getFieldClass().getConstructor().newInstance();
            //元素名字填充“-”
            itemDefineDataSchemaField.parse(itemDefine);

            this.setItemDefine(itemDefineDataSchemaField);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new DataSchemaException(DataSchemaErrorCode.SERVER_ERROR);
        } catch (Exception e) {
            throw new DataSchemaException(DataSchemaErrorCode.BAD_PARAMETERS);
        }
        return this;
    }

    @Override
    public DataValidateResult dataValidate(JSONArray data) {
        if (data == null) {
            return DataValidateResult.error(
                    ErrorUtil.errorString(this.getName(), DataSchemaErrorCode.EMPTY_DATA), DataSchemaErrorCode.EMPTY_DATA);
        }

        //检查大小
        if (data.size() > this.getMaxLength()) {
            return DataValidateResult.error(
                    ErrorUtil.errorString(this.getName(), DataSchemaErrorCode.MAX_LENGTH, this.getMaxLength()),
                    DataSchemaErrorCode.MAX_LENGTH);
        }
        if (data.size() < this.getMinLength()) {
            return DataValidateResult.error(
                    ErrorUtil.errorString(this.getName(), DataSchemaErrorCode.MIN_LENGTH, this.getMinLength()),
                    DataSchemaErrorCode.MIN_LENGTH);
        }

        int index = 0;
        for (Object value : data) {
            DataValidateResult dataValidateResult = DataSchemaFieldValidateChooser.dataSelectValidate(this.getItemDefine(), value);
            if (!dataValidateResult.isSuccess()) {
                dataValidateResult.setMsg(this.getName() + "[" + index + "]" + dataValidateResult.getMsg());
                return dataValidateResult;
            }
            index++;
        }

        return DataValidateResult.success();
    }
}
