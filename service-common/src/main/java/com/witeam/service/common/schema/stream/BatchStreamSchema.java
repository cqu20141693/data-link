package com.witeam.service.common.schema.stream;

import java.util.List;

public class BatchStreamSchema {
    private Long bizTime;

    private List<SingleStreamSchema> data;

    public Long getBizTime() {
        return bizTime;
    }

    public void setBizTime(Long bizTime) {
        this.bizTime = bizTime;
    }

    public List<SingleStreamSchema> getData() {
        return data;
    }

    public void setData(List<SingleStreamSchema> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BatchStreamSchema{" +
                "bizTime=" + bizTime +
                ", data=" + data +
                '}';
    }
}
