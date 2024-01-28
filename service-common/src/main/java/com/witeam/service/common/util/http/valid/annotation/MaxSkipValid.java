package com.witeam.service.common.util.http.valid.annotation;


import com.witeam.service.common.util.http.valid.constraint.MaxSkipConstraint;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MaxSkipConstraint.class)
public @interface MaxSkipValid {
    String pageParamName() default "page";

    String pageSizeParamName() default "pageSize";

    String message() default "超出最大展示数据量区间范围，最大展示100000条数据。";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
