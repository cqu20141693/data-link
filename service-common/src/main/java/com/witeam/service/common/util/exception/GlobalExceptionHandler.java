package com.witeam.service.common.util.exception;

import com.witeam.service.common.call.CommonCodeType;
import com.witeam.service.common.call.CommonResult;
import com.witeam.service.common.call.ResultUtil;
import com.witeam.service.common.exception.CommonAuthorityException;
import com.witeam.service.common.exception.CommonBizException;
import com.witeam.service.common.exception.OpenAPIException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolationException;
import java.util.List;

public class GlobalExceptionHandler {
    @ExceptionHandler({BindException.class})
    @ResponseBody
    public CommonResult<Void> BindExceptionHandler(BindException bindException) {
        List<FieldError> fieldErrors = bindException.getBindingResult().getFieldErrors();
        StringBuilder sb = new StringBuilder();
        fieldErrors.forEach(fieldError -> sb.append(fieldError.getDefaultMessage()).append(","));

        String errorMsg = CommonCodeType.PARAM_CHECK_ERROR.getDesc() +
                ":" + sb.deleteCharAt(sb.length() - 1).toString();
        return ResultUtil.returnError(CommonCodeType.PARAM_CHECK_ERROR.getCode(), errorMsg);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseBody
    public CommonResult<Void> ConstraintViolationExceptionHandler(ConstraintViolationException constraintViolationException) {
        StringBuilder sb = new StringBuilder();
        constraintViolationException
                .getConstraintViolations()
                .forEach(violation -> sb.append(violation.getMessage()).append(","));

        String errorMsg = CommonCodeType.PARAM_CHECK_ERROR.getDesc() +
                ":" + sb.deleteCharAt(sb.length() - 1).toString();

        return ResultUtil.returnError(CommonCodeType.PARAM_CHECK_ERROR.getCode(), errorMsg);
    }

    /**
     * 操作权限异常处理
     *
     * @param commonAuthorityException
     * @return
     */
    @ExceptionHandler({CommonAuthorityException.class})
    @ResponseBody
    public CommonResult<Void> CommonAuthorityExceptionHandler(CommonAuthorityException commonAuthorityException) {
        return ResultUtil.returnError(CommonCodeType.AUTHORITY_ERROR.getCode(),
                CommonCodeType.AUTHORITY_ERROR.getDesc() + ":" + commonAuthorityException.getMessage());
    }

    /**
     * 操作权限异常处理
     *
     * @param commonBizException
     * @return
     */
    @ExceptionHandler({CommonBizException.class})
    @ResponseBody
    public CommonResult<Void> CommonBizExceptionHandler(CommonBizException commonBizException) {
        return ResultUtil.returnError(CommonCodeType.BIZ_ERROR.getCode(),
                CommonCodeType.BIZ_ERROR.getDesc() + ":" + commonBizException.getMessage());
    }

    /**
     * 全局异常处理
     *
     * @param exception
     * @return
     */
    @ExceptionHandler({Exception.class})
    @ResponseBody
    public CommonResult<Void> AllExceptionHandler(Exception exception) {
        return ResultUtil.returnError(CommonCodeType.PARAM_CHECK_ERROR.getCode(),
                CommonCodeType.UNKNOWN_ERROR.getDesc() + ":" + exception.getMessage());
    }

    @ExceptionHandler(value = OpenAPIException.class)
    @ResponseBody
    public CommonResult<Void> openAPIExceptionHandler(OpenAPIException e) {
        return ResultUtil.returnError(e.getErrorCode(), e.getMessage());
    }

}
