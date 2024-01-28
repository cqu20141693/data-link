package com.witeam.service.common.call;

public class ResultUtil {
    /**
     * return success
     *
     * @param data
     * @return
     */
    public static <T> CommonResult<T> returnSuccess(T data) {
        CommonResult<T> result = new CommonResult<>();
        result.setData(data);
        result.setCode(CommonCodeType.SUCCESS.getCode());
        result.setMessage(CommonCodeType.SUCCESS.getDesc());
        return result;
    }

    public static CommonResult<Void> returnSuccess() {
        CommonResult<Void> result = new CommonResult<>();
        result.setCode(CommonCodeType.SUCCESS.getCode());
        result.setMessage(CommonCodeType.SUCCESS.getDesc());
        return result;
    }

    /**
     * return error
     *
     * @param code error code
     * @param msg  error message
     * @return
     */
    public static <T> CommonResult<T> returnError(String code, String msg) {
        CommonResult<T> result = new CommonResult<>();
        if (CommonCodeType.SUCCESS.getCode().equals(code)) {
            //返回错误，但是错误码结果是成功，不对
            code = CommonCodeType.BIZ_ERROR.getCode();
        }
        result.setCode(code);
        result.setMessage(msg);
        return result;

    }

    public static <T> CommonResult<T> returnError(long code, String msg) {
        return returnError(String.valueOf(code), msg);
    }

//    /**
//     * use enum
//     *
//     * @param status
//     * @return
//     */
//    public static CommonResult<Void> returnError(IErrorCode status) {
//        return returnError(status.getCode(), status.getDesc());
//    }

    public static <T> CommonResult<T> returnError(IErrorCode status) {
        CommonResult<T> result = new CommonResult<>();
        String code = status.getCode();
        if (CommonCodeType.SUCCESS.getCode().equals(code)) {
            //返回错误，但是错误码结果是成功，不对
            code = CommonCodeType.BIZ_ERROR.getCode();
        }
        result.setCode(code);
        result.setMessage(status.getDesc());
        return result;
    }

}
