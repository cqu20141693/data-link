package com.chongctech.device.link.biz.model.authenticate;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class WelcomeInfoModel {
    private String welcomeTopic;

    private byte[] welcomeMsg;
}
