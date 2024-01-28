package com.witeam.service.common.schema.trigger;

import com.witeam.service.common.schema.trigger.base.OperationEnum;

public class TriggerRuleSchema {
    private OperationEnum operation = OperationEnum.UNKNOWN;

    private String valueType;

    private Object value;

    private Integer effectiveTimeStart = 0;

    private Integer effectiveTimeEnd = 1440;  // 24 * 60
    
    public OperationEnum getOperation() {
        return operation;
    }

    public void setOperation(OperationEnum operation) {
        this.operation = operation;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Integer getEffectiveTimeStart() {
        return effectiveTimeStart;
    }

    public void setEffectiveTimeStart(Integer effectiveTimeStart) {
        this.effectiveTimeStart = effectiveTimeStart;
    }

    public Integer getEffectiveTimeEnd() {
        return effectiveTimeEnd;
    }

    public void setEffectiveTimeEnd(Integer effectiveTimeEnd) {
        this.effectiveTimeEnd = effectiveTimeEnd;
    }

    @Override
    public String toString() {
        return "TriggerRuleSchema{" +
                "operationEnum=" + operation +
                ", valueType='" + valueType + '\'' +
                ", value=" + value +
                ", effectiveTimeStart=" + effectiveTimeStart +
                ", effectiveTimeEnd=" + effectiveTimeEnd +
                '}';
    }
}
