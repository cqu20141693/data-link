package com.witeam.service.common.schema.device;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DeviceModelSchema {
    /**
     * 设备业务类型标记
     */
    private String type = "default";

    /**
     * 默认检测点
     */
    private Set<DeviceModelMeasurementSchema> measurements = new HashSet<>();

    /**
     * 检测点分组
     */
    private Map<String, Set<DeviceModelMeasurementSchema>> measurementGroup = new HashMap<>();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Set<DeviceModelMeasurementSchema> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(Set<DeviceModelMeasurementSchema> measurements) {
        this.measurements = measurements;
    }

    public Map<String, Set<DeviceModelMeasurementSchema>> getMeasurementGroup() {
        return measurementGroup;
    }

    public void setMeasurementGroup(Map<String, Set<DeviceModelMeasurementSchema>> measurementGroup) {
        this.measurementGroup = measurementGroup;
    }
}
