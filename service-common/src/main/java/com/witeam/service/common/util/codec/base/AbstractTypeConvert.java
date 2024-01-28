package com.witeam.service.common.util.codec.base;

public interface AbstractTypeConvert<T> {
    ConvertResponse<T> rawDataConvert(byte[] payload);

    T strDataConvert(String data);

    byte[] objectDataConvert(Object obj);

    String objectDataConvertStr(Object obj);

    boolean validType(Object obj);
}
