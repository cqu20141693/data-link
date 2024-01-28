package com.witeam.service.common.call;


/**
 * 错误码编码规范
 * 0 - 99999 无业务区分错误号，保留位
 * 100000 - 199999 接口层错误，包括HTTP等，后面进一步细分
 */
public interface IErrorCode {
    String getCode();
    String getDesc();
}
