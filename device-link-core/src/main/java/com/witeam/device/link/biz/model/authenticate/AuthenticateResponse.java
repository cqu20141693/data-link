package com.witeam.device.link.biz.model.authenticate;

import com.witeam.device.common.model.device.base.LinkDeviceType;
import com.witeam.device.link.biz.model.link.ChannelAuth;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AuthenticateResponse {
    /**
     * 链路权限
     */
    private ChannelAuth channelAuth;

    /**
     * 设备类型
     */
    private LinkDeviceType deviceType;

    /**
     * 链路标记
     */
    private String linkTag;

    /**
     * signature tag
     */
    private String signatureTag;
    /**
     * session key
     */
    private String sessionKey;

    private WelcomeInfoModel welcomeInfoModel;
}
