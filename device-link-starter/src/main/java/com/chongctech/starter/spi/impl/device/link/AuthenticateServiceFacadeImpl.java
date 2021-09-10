package com.chongctech.starter.spi.impl.device.link;

import com.chongctech.device.authenticate.client.DeviceAuthenticateClient;
import com.chongctech.device.authenticate.domain.mqtt.MqttChannelType;
import com.chongctech.device.authenticate.domain.mqtt.MqttLoginAuthResponse;
import com.chongctech.device.authenticate.req.MqttLoginAuthReq;
import com.chongctech.device.common.util.device.LinkTagUtil;
import com.chongctech.device.link.biz.model.authenticate.AuthenticateResponse;
import com.chongctech.device.link.biz.model.authenticate.WelcomeInfoModel;
import com.chongctech.device.link.biz.model.link.ChannelAuth;
import com.chongctech.device.link.spi.authenticate.AuthenticateServiceFacade;
import com.chongctech.service.common.call.result.CommonCodeType;
import com.chongctech.service.common.call.result.CommonResult;
import com.chongctech.service.common.call.result.ResultUtil;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            MqttLoginAuthReq req = new MqttLoginAuthReq();
            req.setClientIdentifier(clientIdentifier);
            req.setUsername(username);
            req.setPassword(password);
            req.setSessionKey(sessionKey);
            req.setNodeTag(nodeTag);
            req.setPort(port);
            req.setKeepAliveSeconds(keepAliveSeconds);
            CommonResult<MqttLoginAuthResponse> mqttLoginResult =
                    deviceAuthenticateClient.mqttLogin(req);
            if (!mqttLoginResult.success()) {
                log.info("device login failed,code={} message={},identifier={},username={}", mqttLoginResult.getCode(),
                        mqttLoginResult.getMessage(), clientIdentifier, username);
                if (CommonCodeType.BIZ_ERROR.getCode().equals(mqttLoginResult.getCode())) {
                    return ResultUtil.returnError(CommonCodeType.BIZ_ERROR.getCode(), "authenticate failed");
                }
                return ResultUtil.returnError(CommonCodeType.BIZ_ERROR.getCode(), mqttLoginResult.getMessage());
            }
            MqttLoginAuthResponse response = mqttLoginResult.getData();
            ChannelAuth channelAuth;
            MqttChannelType loginType = response.getLoginType();
            if (loginType == null) {
                log.error("response login type error clientIdentifier={},username={}", clientIdentifier, username);
                return ResultUtil.returnError(CommonCodeType.BIZ_ERROR.getCode(), "login type is invalid");
            }
            switch (loginType) {
                case PUSH:
                    channelAuth = ChannelAuth.ONLY_PUSH;
                    break;
                case SUBSCRIBE:
                    channelAuth = ChannelAuth.ONLY_SUBSCRIBE;
                    break;
                case PUSH_AND_SUBSCRIBE:
                    channelAuth = ChannelAuth.PUSH_AND_SUBSCRIBE;
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
                    .setDeviceType(response.getDeviceType())
                    .setWelcomeInfoModel(welcomeInfoModel));
        } catch (Exception e) {
            log.error("", e);
            return ResultUtil.returnError(CommonCodeType.BIZ_ERROR.getCode(), "server error");
        }
    }

}

