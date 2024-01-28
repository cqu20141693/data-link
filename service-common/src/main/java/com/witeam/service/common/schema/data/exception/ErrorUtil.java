package com.witeam.service.common.schema.data.exception;

import org.apache.commons.lang3.StringUtils;

public class ErrorUtil {
    public static String errorString(String name, DataSchemaErrorCode dataSchemaErrorCode, Object... params) {
        if (StringUtils.isEmpty(name) || dataSchemaErrorCode == null) {
            return "create errorString also error";
        }

        if (params == null) {
            return name + "," + dataSchemaErrorCode.getMsg();
        } else {
            return name + "," + dataSchemaErrorCode.getMsg() + " , params:" + StringUtils.join(params, ",");
        }
    }
}
