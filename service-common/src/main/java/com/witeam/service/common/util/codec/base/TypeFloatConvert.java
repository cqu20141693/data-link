package com.witeam.service.common.util.codec.base;

import java.nio.ByteBuffer;

public class TypeFloatConvert implements AbstractTypeConvert<Float> {
    @Override
    public ConvertResponse<Float> rawDataConvert(byte[] payload) {
        ConvertResponse<Float> response = new ConvertResponse<>();
        if (payload.length != Float.BYTES) {
            return response.setSuccess(false).setFailMsg("payload length is not valid.");
        }
        ByteBuffer byteBuf = ByteBuffer.wrap(payload);
        return response.setConvertResult(byteBuf.asFloatBuffer().get());
    }

    @Override
    public Float strDataConvert(String data) {
        return Float.valueOf(data);
    }

    @Override
    public byte[] objectDataConvert(Object obj) {
        int intBits = Float.floatToIntBits((Float) obj);
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (intBits & 0xff);
        bytes[1] = (byte) ((intBits & 0xff00) >> 8);
        bytes[2] = (byte) ((intBits & 0xff0000) >> 16);
        bytes[3] = (byte) ((intBits & 0xff000000) >> 24);
        return bytes;
    }

    @Override
    public String objectDataConvertStr(Object obj) {
        return obj.toString();
    }

    @Override
    public boolean validType(Object obj) {
        return obj instanceof Float;
    }
}
