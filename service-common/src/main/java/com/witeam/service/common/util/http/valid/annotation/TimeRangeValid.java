package com.witeam.service.common.util.http.valid.annotation;

import com.witeam.service.common.util.http.valid.constraint.TimeRangeConstraint;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TimeRangeConstraint.class)
public @interface TimeRangeValid {
    String startParamName() default "start";

    String endParamName() default "end";

    String message() default "时间范围错误";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
