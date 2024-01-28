package com.witeam.service.common.util.codec.base;

import com.alibaba.fastjson.JSON;

import java.nio.charset.Charset;

public class TypeJsonConvert implements AbstractTypeConvert<JSON> {
    @Override
    public ConvertResponse<JSON> rawDataConvert(byte[] payload) {
        ConvertResponse<JSON> response = new ConvertResponse<>();
        String text = new String(payload);
        if (JSON.isValidObject(text)) {
            return response.setConvertResult(JSON.parseObject(text));
        } else if (JSON.isValidArray(text)) {
            return response.setConvertResult(JSON.parseArray(text));
        } else {
            return response.setSuccess(false).setFailMsg("payload is not json data.");
        }
    }

    @Override
    public JSON strDataConvert(String data) {
        if (JSON.isValidObject(data)) {
            return JSON.parseObject(data);
        } else if (JSON.isValidArray(data)) {
            return JSON.parseArray(data);
        } else {
            return null;
        }
    }

    @Override
    public byte[] objectDataConvert(Object obj) {
        JSON json = (JSON) obj;
        return json.toJSONString().getBytes(Charset.defaultCharset());
    }

    @Override
    public String objectDataConvertStr(Object obj) {
        return ((JSON) obj).toJSONString();
    }

    @Override
    public boolean validType(Object obj) {
        return obj instanceof JSON;
    }
}
