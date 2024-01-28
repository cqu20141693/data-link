package com.witeam.service.common.device.call;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

public class AppWebReq {
    @NotBlank(message = "app不能为空")
    @Length(min = 16, max = 16, message = "groupKey长度为16字节")
    @ApiModelProperty(value = "组key", required = true)
    private String appKey;

    @NotBlank(message = "userId不能为空")
    @ApiModelProperty(value = "用户id")
    private Integer userId;
}
