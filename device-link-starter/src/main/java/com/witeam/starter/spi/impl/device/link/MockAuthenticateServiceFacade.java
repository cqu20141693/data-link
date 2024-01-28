package com.witeam.starter.spi.impl.device.link;

import com.alibaba.fastjson.JSONObject;
import com.witeam.device.authenticate.algorithm.signature.HmacAlgorithm;
import com.witeam.device.common.model.authenticate.req.domain.model.WelcomeInfo;
import com.witeam.device.common.model.authenticate.req.domain.mqtt.MqttChannelType;
import com.witeam.device.common.model.authenticate.req.domain.mqtt.MqttLoginAuthResponse;
import com.witeam.device.common.model.authenticate.req.MqttLoginAuthReq;
import com.witeam.device.common.model.authenticate.req.domain.mqtt.MqttLoginTypeEnum;
import com.witeam.device.common.model.device.base.LinkDeviceType;
import com.witeam.device.common.util.crypto.CryptoType;
import com.witeam.device.common.util.device.LinkTagUtil;
import com.witeam.device.link.biz.model.authenticate.AuthenticateResponse;
import com.witeam.device.link.biz.model.authenticate.WelcomeInfoModel;
import com.witeam.device.link.biz.model.link.ChannelAuth;
import com.witeam.device.link.spi.authenticate.AuthenticateServiceFacade;
import com.witeam.service.common.call.CommonCodeType;
import com.witeam.service.common.call.CommonResult;
import com.witeam.service.common.call.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * @author gow
 * @date 2021/7/13
 */
@Service
@Slf4j
public class MockAuthenticateServiceFacade implements AuthenticateServiceFacade {

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
                    mockMqttLogin(req);
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

    private CommonResult<MqttLoginAuthResponse> mockMqttLogin(MqttLoginAuthReq req) {
        log.info("MqttLogin success:{}", JSONObject.toJSONString(req));
        MqttLoginAuthResponse response = new MqttLoginAuthResponse();
        response.setLoginType(MqttChannelType.PUSH);
        response.setGroupKey("mock");
        response.setSn(req.getClientIdentifier());
        response.setDeviceType(LinkDeviceType.GATEWAY);
        String password = req.getPassword();
        response.setToken(password);
        response.setLinkAddress(req.getNodeTag());
        int index = password.indexOf(":");
        if (index >= 0 && index != password.length() - 1) {
            String mode = password.substring(0, index);
            String token = password.substring(index + 1);
            CryptoType cryptoType = null;
            switch (MqttLoginTypeEnum.parseFromTag(mode)) {

                case GROUP_CRYPTO_LOGIN_HmacSHA256:
                    cryptoType = CryptoType.AES;
                    break;
                case GROUP_CRYPTO_LOGIN_HmacSM3:
                    cryptoType = CryptoType.SM4;
                    break;
                case DEVICE_CRYPTO_LOGIN_HmacSHA256:
                    cryptoType = CryptoType.AES;
                    break;
                case DEVICE_CRYPTO_LOGIN_HmacSM3:
                    cryptoType = CryptoType.SM4;
                    break;
            }
            if (cryptoType != null) {
                String signatureKey = RandomStringUtils.randomAlphanumeric(16);
                response.setSignatureTag(String.join("-", cryptoType.getName(), signatureKey));
            }
        }
        response.setWelcomeInfo(new WelcomeInfo().setWelcomeTopic("/sys/welcome")
                .setWelcomeMsg("welcome link witeam iot".getBytes(StandardCharsets.UTF_8)));

        return ResultUtil.returnSuccess(response);
    }

}

