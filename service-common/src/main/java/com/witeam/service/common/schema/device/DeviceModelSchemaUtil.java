package com.witeam.service.common.schema.device;

import com.alibaba.fastjson.JSONObject;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class DeviceModelSchemaUtil {
    public static DeviceModelSchema parseFrom(JSONObject rawData) {
        return JSONObject.toJavaObject(rawData, DeviceModelSchema.class);
    }

    public static JSONObject toJson(DeviceModelSchema deviceModelSchema) {
        return (JSONObject) JSONObject.toJSON(deviceModelSchema);
    }

    public static void merge(DeviceModelSchema deviceModelSchema, DeviceModelSchema appendSchema) {
        Set<DeviceModelMeasurementSchema> newMeasurementSet = appendSchema.getMeasurements();
        if (!CollectionUtils.isEmpty(newMeasurementSet)) {
            Set<DeviceModelMeasurementSchema> measurementSet = deviceModelSchema.getMeasurements();
            if (CollectionUtils.isEmpty(measurementSet)) {
                deviceModelSchema.setMeasurements(newMeasurementSet);
            } else {
                measurementSet.removeAll(newMeasurementSet);
                measurementSet.addAll(newMeasurementSet);
            }
        }
        Map<String, Set<DeviceModelMeasurementSchema>> newMeasurementGroup = appendSchema.getMeasurementGroup();
        if (!CollectionUtils.isEmpty(newMeasurementGroup)) {
            Map<String, Set<DeviceModelMeasurementSchema>> measurementGroup = deviceModelSchema.getMeasurementGroup();
            if (CollectionUtils.isEmpty(measurementGroup)) {
                deviceModelSchema.setMeasurementGroup(newMeasurementGroup);
            } else {
                newMeasurementGroup.forEach((key, value) -> {
                    if (measurementGroup.containsKey(key)) {
                        Set<DeviceModelMeasurementSchema> groupMeasurementSet = measurementGroup.get(key);
                        groupMeasurementSet.removeAll(value);
                        groupMeasurementSet.addAll(value);
                    } else {
                        measurementGroup.put(key, value);
                    }
                });
            }
        }
    }

    public static void clean(DeviceModelSchema deviceModelSchema,
                             List<DeviceModelMeasurementSchema> cleanMeasurements,
                             List<String> cleanGroupNames,
                             Map<String, List<DeviceModelMeasurementSchema>> cleanGroupMeasurements) {
        Set<DeviceModelMeasurementSchema> measurementSet = deviceModelSchema.getMeasurements();
        if (!CollectionUtils.isEmpty(measurementSet) &&
                !CollectionUtils.isEmpty(cleanMeasurements)) {
            measurementSet.removeAll(cleanMeasurements);
        }

        Map<String, Set<DeviceModelMeasurementSchema>> measurementGroup = deviceModelSchema.getMeasurementGroup();
        if (!CollectionUtils.isEmpty(measurementGroup)) {
            if (!CollectionUtils.isEmpty(cleanGroupNames)) {
                for (String cleanGroupName : cleanGroupNames) {
                    measurementGroup.remove(cleanGroupName);
                }
            }
            if (!CollectionUtils.isEmpty(cleanGroupMeasurements)) {
                cleanGroupMeasurements.forEach((key, value) -> {
                    if (measurementGroup.containsKey(key)) {
                        measurementGroup.get(key).removeAll(value);
                    }
                });
            }
        }
    }


}
