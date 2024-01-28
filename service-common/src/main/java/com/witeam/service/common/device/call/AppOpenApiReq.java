package com.witeam.service.common.device.call;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

public class AppOpenApiReq {
    @NotBlank(message = "appKey不能为空")
    @Length(min = 16, max = 16, message = "appKey长度为16字节")
    @ApiModelProperty(value = "应用key", required = true)
    protected String appKey;

    @NotBlank(message = "appToken不能为空")
    @ApiModelProperty(value = "app token")
    protected String appToken;
}
