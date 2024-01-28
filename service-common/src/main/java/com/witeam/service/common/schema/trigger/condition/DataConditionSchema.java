package com.witeam.service.common.schema.trigger.condition;

import com.witeam.service.common.schema.trigger.base.ConditionTypeEnum;
import com.witeam.service.common.schema.trigger.base.OperationEnum;

public class DataConditionSchema extends BaseConditionSchema {
    private String groupKey;

    private String sn;

    private String stream;

    private OperationEnum operation = OperationEnum.UNKNOWN;

    private String valueType;

    private Object value;

    public DataConditionSchema() {
        super(ConditionTypeEnum.DATA);
    }

    public String getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

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

    @Override
    public String toString() {
        return "DataConditionSchema{" +
                "groupKey='" + groupKey + '\'' +
                ", sn='" + sn + '\'' +
                ", stream='" + stream + '\'' +
                ", operationEnum=" + operation +
                ", valueType='" + valueType + '\'' +
                ", value=" + value +
                ", conditionTypeEnum=" + type +
                ", conjunctionTypeEnum=" + conjunction +
                '}';
    }
}
