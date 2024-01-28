package com.witeam.service.common.schema.trigger.condition;

import com.witeam.service.common.schema.trigger.base.ConditionTypeEnum;

import java.util.LinkedList;
import java.util.List;

public class NestedConditionSchema extends BaseConditionSchema {
    private List<BaseConditionSchema> nestedCondition = new LinkedList<>();

    public NestedConditionSchema() {
        super(ConditionTypeEnum.NESTED);
    }

    public List<BaseConditionSchema> getNestedCondition() {
        return nestedCondition;
    }

    public void setNestedCondition(List<BaseConditionSchema> nestedCondition) {
        this.nestedCondition = nestedCondition;
    }
}
