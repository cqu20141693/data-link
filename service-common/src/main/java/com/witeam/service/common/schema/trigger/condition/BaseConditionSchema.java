package com.witeam.service.common.schema.trigger.condition;

import com.witeam.service.common.schema.trigger.base.ConditionTypeEnum;
import com.witeam.service.common.schema.trigger.base.ConjunctionTypeEnum;

public class BaseConditionSchema {
    protected ConditionTypeEnum type;

    protected ConjunctionTypeEnum conjunction = ConjunctionTypeEnum.AND;

    protected BaseConditionSchema(ConditionTypeEnum type) {
        this.type = type;
    }

    public ConjunctionTypeEnum getConjunction() {
        return conjunction;
    }

    public void setConjunction(ConjunctionTypeEnum conjunction) {
        this.conjunction = conjunction;
    }

    public ConditionTypeEnum getType() {
        return type;
    }

    public void setType(ConditionTypeEnum type) {
        this.type = type;
    }
}
