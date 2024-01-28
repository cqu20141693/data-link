package com.witeam.service.common.util.http.valid.annotation;

import com.witeam.service.common.util.http.valid.constraint.OrderTypeConstraint;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = OrderTypeConstraint.class)
public @interface OrderTypeValid {
    String orderParamName() default "order";

    String message() default "排序参数类型错误";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
