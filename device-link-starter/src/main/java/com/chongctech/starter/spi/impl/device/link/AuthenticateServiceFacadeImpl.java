package com.chongctech.starter.spi.impl.device.link;

import com.chongctech.device.authenticate.client.DeviceAuthenticateClient;
import com.chongctech.device.authenticate.domain.mqtt.MqttLoginAuthResponse;
import com.chongctech.device.common.util.device.LinkTagUtil;
import com.chongctech.device.link.biz.model.authenticate.AuthenticateResponse;
import com.chongctech.device.link.biz.model.authenticate.WelcomeInfoModel;
import com.chongctech.device.link.biz.model.link.ChannelAuth;
import com.chongctech.device.link.spi.authenticate.AuthenticateServiceFacade;
import com.chongctech.service.common.call.result.CommonCodeType;
import com.chongctech.service.common.call.result.CommonResult;
import com.chongctech.service.common.call.result.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author gow
 * @date 2021/7/13
 */
@Service
@Slf4j
public class AuthenticateServiceFacadeImpl implements AuthenticateServiceFacade {

    @Autowired
    private DeviceAuthenticateClient deviceAuthenticateClient;

    @Override
    public CommonResult<AuthenticateResponse> authenticate(String clientIdentifier, String username, String password,
                                                           String sessionKey,
                                                           String nodeTag, Integer port, int keepAliveSeconds) {
        try {
            CommonResult<MqttLoginAuthResponse> mqttLoginResult =
                    deviceAuthenticateClient.mqttLogin(clientIdentifier, username, password, sessionKey, nodeTag, port,
                            keepAliveSeconds);
            if (!mqttLoginResult.success()) {
                log.info("device login failed,code={} message={},identifier={},username={}", mqttLoginResult.getCode(),
                        mqttLoginResult.getMessage(), clientIdentifier, username);
                return ResultUtil.returnError(CommonCodeType.BIZ_ERROR.getCode(), mqttLoginResult.getMessage());
            }
            MqttLoginAuthResponse response = mqttLoginResult.getData();
            ChannelAuth channelAuth;
            switch (response.getLoginType()) {
                case GROUP_LOGIN:
                case DEVICE_LOGIN:
                case GROUP_LOGIN_HmacSHA256:
                case GROUP_LOGIN_HmacSM3:
                case DEVICE_LOGIN_HmacSHA256:
                case DEVICE_LOGIN_HmacSM3:
                case GROUP_CRYPTO_LOGIN_HmacSHA256:
                case GROUP_CRYPTO_LOGIN_HmacSM3:
                case DEVICE_CRYPTO_LOGIN_HmacSHA256:
                case DEVICE_CRYPTO_LOGIN_HmacSM3:
                    channelAuth = ChannelAuth.ONLY_PUSH;
                    break;
                case MIRROR_LOGIN:
                case APP_LOGIN:
                    channelAuth = ChannelAuth.ONLY_SUBSCRIBE;
                    break;
                default:
                    log.error("response login type error clientIdentifier={},username={}", clientIdentifier, username);
                    return ResultUtil.returnError(CommonCodeType.BIZ_ERROR.getCode(), "login type is invalid");
            }
            WelcomeInfoModel welcomeInfoModel = Optional.ofNullable(response.getWelcomeInfo())
                    .map(info -> new WelcomeInfoModel().setWelcomeMsg(response.getWelcomeInfo().getWelcomeMsg())
                            .setWelcomeTopic(response.getWelcomeInfo().getWelcomeTopic())).orElse(null);
            return ResultUtil.returnSuccess(new AuthenticateResponse()
                    .setChannelAuth(channelAuth)
                    .setLinkTag(LinkTagUtil.createLinkTag(response.getSn(), response.getGroupKey()))
                    .setSessionKey(sessionKey)
                    .setSignatureTag(response.getSignatureTag())
                    .setDeviceTypeEnum(response.getDeviceTypeEnum())
                    .setWelcomeInfoModel(welcomeInfoModel));
        } catch (Exception e) {
            log.error("", e);
            return ResultUtil.returnError(CommonCodeType.BIZ_ERROR.getCode(), "server error");
        }
    }

}

