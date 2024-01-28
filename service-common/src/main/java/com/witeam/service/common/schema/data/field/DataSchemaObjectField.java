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
import java.util.ArrayList;
import java.util.List;

public class DataSchemaObjectField extends DataSchemaField<JSONObject> {
    private List<DataSchemaField<?>> itemListDefine;

    private Integer minLength;

    private Integer maxLength;

    public List<DataSchemaField<?>> getItemListDefine() {
        return itemListDefine;
    }

    public void setItemListDefine(List<DataSchemaField<?>> itemListDefine) {
        this.itemListDefine = itemListDefine;
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
    public DataSchemaField<JSONObject> parse(JSONObject define) throws DataSchemaException {
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

            List<DataSchemaField<?>> itemListDefine = new ArrayList<>();
            JSONArray itemList = define.getJSONArray(DataSchemaFieldConstants.ITEM_LIST_DEFINE);
            for (Object itemObj : itemList) {
                JSONObject itemDefine = (JSONObject) itemObj;
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

                itemDefineDataSchemaField.parse(itemDefine);

                itemListDefine.add(itemDefineDataSchemaField);
            }
            this.setItemListDefine(itemListDefine);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new DataSchemaException(DataSchemaErrorCode.SERVER_ERROR);
        } catch (Exception e) {
            throw new DataSchemaException(DataSchemaErrorCode.BAD_PARAMETERS);
        }
        return this;
    }

    @Override
    public DataValidateResult dataValidate(JSONObject data) {
        if (data == null) {
            return DataValidateResult.error(DataSchemaErrorCode.EMPTY_DATA.getMsg(), DataSchemaErrorCode.EMPTY_DATA);
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

        List<DataSchemaField<?>> dataSchemaFieldList = this.getItemListDefine();
        for (DataSchemaField<?> dataSchemaField : dataSchemaFieldList) {
            Object itemData = data.get(dataSchemaField.getName());
            if (itemData == null) {
                return DataValidateResult.error(
                        ErrorUtil.errorString(this.getName(), DataSchemaErrorCode.OBJECT_DATA_IS_EMPTY, dataSchemaField.getName()),
                        DataSchemaErrorCode.OBJECT_DATA_IS_EMPTY);
            }
            DataValidateResult dataValidateResult = DataSchemaFieldValidateChooser.dataSelectValidate(dataSchemaField, itemData);
            if (!dataValidateResult.isSuccess()) {
                dataValidateResult.setMsg(this.getName() + "/" + dataValidateResult.getMsg());
                return dataValidateResult;
            }
        }
        return DataValidateResult.success();
    }
}
