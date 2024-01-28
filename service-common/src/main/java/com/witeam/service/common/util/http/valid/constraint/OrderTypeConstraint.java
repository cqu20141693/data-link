package com.witeam.service.common.util.http.valid.constraint;

import com.witeam.service.common.util.http.valid.annotation.OrderTypeValid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class OrderTypeConstraint implements ConstraintValidator<OrderTypeValid, Object> {
    private static final Logger logger = LoggerFactory.getLogger(OrderTypeConstraint.class);

    private static final String ORDER_ASC = "asc";

    private static final String ORDER_DESC = "desc";

    private String orderParamName;


    @Override
    public void initialize(OrderTypeValid constraintAnnotation) {
        orderParamName = constraintAnnotation.orderParamName();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }

        BeanWrapper beanWrapper = new BeanWrapperImpl(value);
        Object orderObject = beanWrapper.getPropertyValue(orderParamName);

        if (orderObject == null) {
            return false;
        }
        String order = ((String) orderObject);

        return ORDER_ASC.equalsIgnoreCase(order) || ORDER_DESC.equalsIgnoreCase(order);
    }
}
