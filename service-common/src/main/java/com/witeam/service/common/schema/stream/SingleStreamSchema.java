package com.witeam.service.common.schema.stream;

public class SingleStreamSchema {
    private Long bizTime;

    private String stream;

    private String encodeType;

    private Object data;

    public Long getBizTime() {
        return bizTime;
    }

    public void setBizTime(Long bizTime) {
        this.bizTime = bizTime;
    }

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public String getEncodeType() {
        return encodeType;
    }

    public void setEncodeType(String encodeType) {
        this.encodeType = encodeType;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "SingleStreamSchema{" +
                "bizTime=" + bizTime +
                ", stream='" + stream + '\'' +
                ", encodeType='" + encodeType + '\'' +
                ", data=" + data +
                '}';
    }
}
