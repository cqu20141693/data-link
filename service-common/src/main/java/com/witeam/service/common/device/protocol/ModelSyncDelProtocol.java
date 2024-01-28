package com.witeam.service.common.device.protocol;

import java.util.List;
import java.util.Map;

public class ModelSyncDelProtocol {
    private List<String> measurements;

    private List<String> groupNames;

    private Map<String, List<String>> groupMeasurements;

    public List<String> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(List<String> measurements) {
        this.measurements = measurements;
    }

    public List<String> getGroupNames() {
        return groupNames;
    }

    public void setGroupNames(List<String> groupNames) {
        this.groupNames = groupNames;
    }

    public Map<String, List<String>> getGroupMeasurements() {
        return groupMeasurements;
    }

    public void setGroupMeasurements(Map<String, List<String>> groupMeasurements) {
        this.groupMeasurements = groupMeasurements;
    }

    @Override
    public String toString() {
        return "ModelSyncDelProtocol{" +
                "measurements=" + measurements +
                ", groupNames=" + groupNames +
                ", groupMeasurements=" + groupMeasurements +
                '}';
    }
}
