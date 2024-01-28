package com.witeam.service.common.util.codec.base;

import java.nio.ByteBuffer;

public class TypeDoubleConvert implements AbstractTypeConvert<Double> {
    @Override
    public ConvertResponse<Double> rawDataConvert(byte[] payload) {
        ConvertResponse<Double> response = new ConvertResponse<>();
        if (payload.length != Double.BYTES) {
            return response.setSuccess(false).setFailMsg("payload length is not valid.");
        }
        ByteBuffer byteBuf = ByteBuffer.wrap(payload);
        return response.setConvertResult(byteBuf.asDoubleBuffer().get());
    }

    @Override
    public Double strDataConvert(String data) {
        return Double.valueOf(data);
    }

    @Override
    public byte[] objectDataConvert(Object obj) {
        long doubleBits = Double.doubleToLongBits((Double) obj);
        byte[] bytes = new byte[8];
        bytes[0] = (byte) (doubleBits & 0xff);
        bytes[1] = (byte) ((doubleBits >> 8) & 0xff);
        bytes[2] = (byte) ((doubleBits >> 16) & 0xff);
        bytes[3] = (byte) ((doubleBits >> 24) & 0xff);
        bytes[4] = (byte) ((doubleBits >> 32) & 0xff);
        bytes[5] = (byte) ((doubleBits >> 40) & 0xff);
        bytes[6] = (byte) ((doubleBits >> 48) & 0xff);
        bytes[7] = (byte) ((doubleBits >> 56) & 0xff);
        return bytes;
    }

    @Override
    public String objectDataConvertStr(Object obj) {
        return obj.toString();
    }

    @Override
    public boolean validType(Object obj) {
        return obj instanceof Double;
    }
}
