package com.witeam.service.common.schema.data.field.base;

import com.witeam.service.common.schema.data.field.*;

import java.util.HashMap;
import java.util.Map;

public enum DataSchemaFieldType {
    OBJECT("object", DataSchemaObjectField.class),
    ARRAY("array", DataSchemaArrayField.class),
    STRING("string", DataSchemaStringField.class),
    INTEGER("int", DataSchemaIntegerField.class),
    LONG("long", DataSchemaLongField.class),
    FLOAT("float", DataSchemaFloatField.class),
    DOUBLE("double", DataSchemaDoubleField.class),
    BOOLEAN("bool", DataSchemaBooleanField.class),
    UNKNOWN("unknown", null);

    private final static Map<String, DataSchemaFieldType> INNER;

    static {
        INNER = new HashMap<>();
        for (DataSchemaFieldType t : DataSchemaFieldType.values()) {
            INNER.put(t.getName(), t);
        }
    }

    private String name;

    private Class<? extends DataSchemaField<?>> fieldClass;

    DataSchemaFieldType(String name, Class<? extends DataSchemaField<?>> fieldClass) {
        this.name = name;
        this.fieldClass = fieldClass;
    }

    public static DataSchemaFieldType parseFromName(String name) {
        return INNER.getOrDefault(name, UNKNOWN);
    }

    public String getName() {
        return name;
    }

    public Class<? extends DataSchemaField<?>> getFieldClass() {
        return fieldClass;
    }
}
