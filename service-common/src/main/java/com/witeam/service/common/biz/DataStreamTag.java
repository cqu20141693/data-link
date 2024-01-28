package com.witeam.service.common.biz;

import java.util.Objects;

public class DataStreamTag {
    private String groupKey;

    private String streamName;

    private EncodeTypeEnum encodeType;

    public static DataStreamTag create(String groupKey, String streamName, EncodeTypeEnum encodeType) {
        DataStreamTag dataStreamTag = new DataStreamTag();
        dataStreamTag.setGroupKey(groupKey);
        dataStreamTag.setStreamName(streamName);
        dataStreamTag.setEncodeType(encodeType);
        return dataStreamTag;
    }

    public String getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }

    public String getStreamName() {
        return streamName;
    }

    public void setStreamName(String streamName) {
        this.streamName = streamName;
    }

    public EncodeTypeEnum getEncodeType() {
        return encodeType;
    }

    public void setEncodeType(EncodeTypeEnum encodeType) {
        this.encodeType = encodeType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataStreamTag that = (DataStreamTag) o;
        return Objects.equals(groupKey, that.groupKey) &&
                Objects.equals(streamName, that.streamName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupKey, streamName);
    }

    @Override
    public String toString() {
        return "DataStreamTag{" +
                "groupKey='" + groupKey + '\'' +
                ", streamName='" + streamName + '\'' +
                ", encodeType=" + encodeType +
                '}';
    }
}
