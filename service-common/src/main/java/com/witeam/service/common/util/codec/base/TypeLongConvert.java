package com.witeam.service.common.util.codec.base;

import java.nio.ByteBuffer;

public class TypeLongConvert implements AbstractTypeConvert<Long> {
    @Override
    public ConvertResponse<Long> rawDataConvert(byte[] payload) {
        ConvertResponse<Long> response = new ConvertResponse<>();
        if (payload.length != Long.BYTES) {
            return response.setSuccess(false).setFailMsg("payload length is not valid.");
        }
        ByteBuffer byteBuf = ByteBuffer.wrap(payload);
        return response.setConvertResult(byteBuf.asLongBuffer().get());
    }

    @Override
    public Long strDataConvert(String data) {
        return Long.valueOf(data);
    }

    @Override
    public byte[] objectDataConvert(Object obj) {
        Long longObj = (Long) obj;
        byte[] bytes = new byte[8];
        bytes[0] = (byte) (longObj & 0xff);
        bytes[1] = (byte) ((longObj >> 8) & 0xff);
        bytes[2] = (byte) ((longObj >> 16) & 0xff);
        bytes[3] = (byte) ((longObj >> 24) & 0xff);
        bytes[4] = (byte) ((longObj >> 32) & 0xff);
        bytes[5] = (byte) ((longObj >> 40) & 0xff);
        bytes[6] = (byte) ((longObj >> 48) & 0xff);
        bytes[7] = (byte) ((longObj >> 56) & 0xff);
        return bytes;
    }

    @Override
    public String objectDataConvertStr(Object obj) {
        return obj.toString();
    }

    @Override
    public boolean validType(Object obj) {
        return obj instanceof Long;
    }
}
