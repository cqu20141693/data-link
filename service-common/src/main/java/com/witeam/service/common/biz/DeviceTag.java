package com.witeam.service.common.biz;

import java.util.Objects;

public class DeviceTag {
    private final String groupKey;

    private final String sn;

    public DeviceTag(String groupKey, String sn) {
        this.groupKey = groupKey;
        this.sn = sn;
    }

    public static DeviceTag create(String groupKey, String sn) {
        return new DeviceTag(groupKey, sn);
    }

    public String getGroupKey() {
        return groupKey;
    }

    public String getSn() {
        return sn;
    }

    @Override
    public String toString() {
        return "DeviceObject{" +
                "groupKey='" + groupKey + '\'' +
                ", sn='" + sn + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceTag that = (DeviceTag) o;
        return Objects.equals(groupKey, that.groupKey) &&
                Objects.equals(sn, that.sn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupKey, sn);
    }

    public boolean selfCheck() {
        return this.groupKey != null && this.sn != null;
    }
}
