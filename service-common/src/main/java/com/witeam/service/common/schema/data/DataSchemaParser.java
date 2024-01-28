package com.witeam.service.common.schema.data;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.witeam.service.common.schema.data.exception.DataSchemaErrorCode;
import com.witeam.service.common.schema.data.exception.DataSchemaException;
import com.witeam.service.common.schema.data.field.base.DataSchemaField;
import com.witeam.service.common.schema.data.field.base.DataSchemaFieldConstants;
import com.witeam.service.common.schema.data.field.base.DataSchemaFieldType;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * a valid schema:
 * {
 *     "name":"root", //必填
 *     "type":"object", //必填
 *     "desc":"root description", //选填
 *     "minLength":1, //类型特有可选参数
 *     "maxLength":100, //类型特有可选参数
 *     "itemListDefine":[
 *        {
 *          "name":"intName", //必填
 *          "type":"int", //必填
 *          "desc":"root description", //选填
 *          "min":1,
 *          "max":1000
 *        },
 *        {
 *            ...
 *        }
 *        ...
 *     ]
 * }
 */
public class DataSchemaParser {
    /**
     * 将String类型的schema转换成SchemaField对象
     *
     * @param defineString 模型定义字符串
     * @return SchemaField
     * @throws DataSchemaException
     */
    public static DataSchemaField<?> parse(String defineString) throws DataSchemaException {
        if (StringUtils.isEmpty(defineString)) {
            throw new DataSchemaException(DataSchemaErrorCode.BAD_PARAMETERS);
        }

        JSONObject jsonObject = JSON.parseObject(defineString);
        if (jsonObject == null) {
            throw new DataSchemaException(DataSchemaErrorCode.BAD_PARAMETERS);
        }

        return parse(jsonObject);
    }

    /**
     * 将JSONObject类型的schema转换成SchemaField对象
     *
     * @param define 模型定义json
     * @return SchemaField
     * @throws DataSchemaException
     */
    public static DataSchemaField<?> parse(JSONObject define) throws DataSchemaException {
        if (define == null) {
            throw new DataSchemaException(DataSchemaErrorCode.BAD_PARAMETERS);
        }
        String defineType = define.getString(DataSchemaFieldConstants.TYPE);
        if (StringUtils.isEmpty(defineType)) {
            throw new DataSchemaException(DataSchemaErrorCode.BAD_PARAMETERS);
        }

        DataSchemaFieldType defineFieldType = DataSchemaFieldType.parseFromName(defineType);
        if (defineFieldType == DataSchemaFieldType.UNKNOWN) {
            throw new DataSchemaException(DataSchemaErrorCode.BAD_PARAMETERS);
        }
        DataSchemaField<?> defineDataSchemaField;
        try {
            defineDataSchemaField = defineFieldType.getFieldClass().getConstructor().newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new DataSchemaException(DataSchemaErrorCode.SERVER_ERROR);
        }

        //元素名字填充“-”
        return defineDataSchemaField.parse(define);
    }
}
