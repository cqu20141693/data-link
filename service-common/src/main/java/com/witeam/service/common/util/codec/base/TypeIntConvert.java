package com.witeam.service.common.util.codec.base;

import java.nio.ByteBuffer;

public class TypeIntConvert implements AbstractTypeConvert<Integer> {
    @Override
    public ConvertResponse<Integer> rawDataConvert(byte[] payload) {
        ConvertResponse<Integer> response = new ConvertResponse<>();
        if (payload.length != Integer.BYTES) {
            return response.setSuccess(false).setFailMsg("payload length is not valid.");
        }
        ByteBuffer byteBuf = ByteBuffer.wrap(payload);
        return response.setConvertResult(byteBuf.asIntBuffer().get());
    }

    @Override
    public Integer strDataConvert(String data) {
        return Integer.valueOf(data);
    }

    @Override
    public byte[] objectDataConvert(Object obj) {
        Integer intObj = (Integer) obj;
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (intObj & 0xff);
        bytes[1] = (byte) ((intObj & 0xff00) >> 8);
        bytes[2] = (byte) ((intObj & 0xff0000) >> 16);
        bytes[3] = (byte) ((intObj & 0xff000000) >> 24);
        return bytes;
    }

    @Override
    public String objectDataConvertStr(Object obj) {
        return obj.toString();
    }

    @Override
    public boolean validType(Object obj) {
        return obj instanceof Integer;
    }
}
