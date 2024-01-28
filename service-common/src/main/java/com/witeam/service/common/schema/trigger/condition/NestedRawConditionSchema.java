package com.witeam.service.common.schema.trigger.condition;

import com.witeam.service.common.schema.trigger.base.ConditionTypeEnum;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NestedRawConditionSchema extends BaseConditionSchema {
    private List<Map<String, Object>> nestedCondition = new LinkedList<>();

    public NestedRawConditionSchema() {
        super(ConditionTypeEnum.NESTED);
    }

    public List<Map<String, Object>> getNestedCondition() {
        return nestedCondition;
    }

    public void setNestedCondition(List<Map<String, Object>> nestedCondition) {
        this.nestedCondition = nestedCondition;
    }
}
