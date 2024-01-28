package com.witeam.service.common.util.http.valid.constraint;
import com.witeam.service.common.util.http.valid.annotation.TimeRangeValid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TimeRangeConstraint implements ConstraintValidator<TimeRangeValid, Object> {
    private static Logger logger = LoggerFactory.getLogger(TimeRangeConstraint.class);

    private String startParamName;

    private String endParamName;

    @Override
    public void initialize(TimeRangeValid constraintAnnotation) {
        startParamName = constraintAnnotation.startParamName();
        endParamName = constraintAnnotation.endParamName();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }

        BeanWrapper beanWrapper = new BeanWrapperImpl(value);
        Object start = beanWrapper.getPropertyValue(startParamName);
        Object end = beanWrapper.getPropertyValue(endParamName);

        if (start == null || end == null) {
            return false;
        }
        Long startLong = ((Long) start);
        Long endLong = ((Long) end);

        return endLong >= startLong;
    }
}
