package com.witeam.service.common.util.http.valid.constraint;

import com.witeam.service.common.util.http.valid.annotation.MaxSkipValid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MaxSkipConstraint implements ConstraintValidator<MaxSkipValid, Object> {
    private static final Logger logger = LoggerFactory.getLogger(MaxSkipConstraint.class);

    /**
     * 最大展示数据量区间
     */
    private static final int MAX_SKIP_SIZE = 1000000;

    private String pageParamName;

    private String pageSizeParamName;

    @Override
    public void initialize(MaxSkipValid constraintAnnotation) {
        pageParamName = constraintAnnotation.pageParamName();
        pageSizeParamName = constraintAnnotation.pageSizeParamName();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }

        BeanWrapper beanWrapper = new BeanWrapperImpl(value);
        Object pageObject = beanWrapper.getPropertyValue(pageParamName);
        Object pageSizeObject = beanWrapper.getPropertyValue(pageSizeParamName);

        if (pageObject == null || pageSizeObject == null) {
            return false;
        }
        Integer page = ((Integer) pageObject);
        Integer pageSize = ((Integer) pageSizeObject);

        return page * pageSize <= MAX_SKIP_SIZE;
    }
}
