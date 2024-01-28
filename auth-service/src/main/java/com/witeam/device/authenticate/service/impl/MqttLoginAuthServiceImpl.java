package com.witeam.device.authenticate.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.witeam.device.authenticate.algorithm.signature.HmacAlgorithm;
import com.witeam.device.authenticate.algorithm.signature.HmacSignature;
import com.witeam.device.authenticate.algorithm.signature.SignatureAlgorithm;
import com.witeam.device.authenticate.algorithm.signature.SignatureFactory;
import com.witeam.device.authenticate.service.CheckService;
import com.witeam.device.authenticate.spi.AppMetaFacade;
import com.witeam.device.authenticate.service.MqttLoginAuthService;
import com.witeam.device.authenticate.spi.DeviceMetaServiceFacade;
import com.witeam.device.authenticate.spi.GroupMetaServiceFacade;
import com.witeam.device.authenticate.spi.RouteFacade4Link;
import com.witeam.device.authenticate.spi.model.AppMetaModel;
import com.witeam.device.authenticate.spi.model.DeviceDTO;
import com.witeam.device.authenticate.spi.model.GroupDTO;
import com.witeam.device.common.model.authenticate.req.domain.model.WelcomeInfo;
import com.witeam.device.common.model.authenticate.req.domain.mqtt.MqttChannelType;
import com.witeam.device.common.model.authenticate.req.domain.mqtt.MqttLoginAuthResponse;
import com.witeam.device.common.model.authenticate.req.domain.mqtt.MqttLoginTypeEnum;
import com.witeam.device.common.model.device.base.LinkDeviceType;
import com.witeam.device.common.model.device.route.link.SelfLinkRouteInfo;
import com.witeam.device.common.util.crypto.CryptoType;
import com.witeam.device.common.util.crypto.CryptoWrapper;
import com.witeam.device.common.util.device.LinkTagUtil;
import com.witeam.service.common.biz.DeviceTypeEnum;
import com.witeam.service.common.call.CommonCodeType;
import com.witeam.service.common.call.CommonResult;
import com.witeam.service.common.call.ResultUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * @author gow 2024/01/23
 */

@Component
public class MqttLoginAuthServiceImpl implements MqttLoginAuthService {
    private static final Logger log = LoggerFactory.getLogger(MqttLoginAuthServiceImpl.class);
    @Autowired
    private DeviceMetaServiceFacade deviceMetaServiceFacade;
    @Autowired
    private GroupMetaServiceFacade groupMetaServiceFacade;
    @Autowired
    private RouteFacade4Link routeFacade4Link;
    @Autowired
    private AppMetaFacade appMetaFacade;
    private static final String WELCOME_TOPIC = "sys/welcome";
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    private final String DELIMITER = ":";
    @Autowired
    private CheckService checkService;

    public CommonResult<MqttLoginAuthResponse> auth(String clientIdentifier, String username, String password, String sessionKey, String nodeTag, Integer port, Integer keepAliveSeconds) {
        if (StringUtils.isEmpty(password)) {
            log.info("password is null, clientIdentifier:{},userName:{}", clientIdentifier, username);
            return ResultUtil.returnError(CommonCodeType.PARAM_CHECK_ERROR.getCode(), "password required not null");
        } else {
            int index = password.indexOf(":");
            if (index >= 0 && index != password.length() - 1) {
                String mode = password.substring(0, index);
                String token = password.substring(index + 1);
                CryptoType cryptoType = null;
                CommonResult<MqttLoginAuthResponse> loginAuthResult;
                switch (MqttLoginTypeEnum.parseFromTag(mode)) {
                    case GROUP_LOGIN:
                        loginAuthResult = this.groupLogin(username, token, clientIdentifier, null);
                        break;
                    case DEVICE_LOGIN:
                        loginAuthResult = this.deviceLogin(username, token, clientIdentifier, null);
                        break;
                    case MIRROR_LOGIN:
                        loginAuthResult = this.mirrorLogin(username, token, clientIdentifier);
                        break;
                    case APP_LOGIN:
                        loginAuthResult = this.appLogin(username, token, clientIdentifier);
                        break;
                    case GROUP_CRYPTO_LOGIN_HmacSHA256:
                        cryptoType = CryptoType.AES;
                    case GROUP_LOGIN_HmacSHA256:
                        loginAuthResult = this.groupSignatureLogin(HmacAlgorithm.HmacSHA256, username, token, clientIdentifier);
                        break;
                    case GROUP_CRYPTO_LOGIN_HmacSM3:
                        cryptoType = CryptoType.SM4;
                    case GROUP_LOGIN_HmacSM3:
                        loginAuthResult = this.groupSignatureLogin(HmacAlgorithm.HmacSM3, username, token, clientIdentifier);
                        break;
                    case DEVICE_CRYPTO_LOGIN_HmacSHA256:
                        cryptoType = CryptoType.AES;
                    case DEVICE_LOGIN_HmacSHA256:
                        loginAuthResult = this.deviceSignatureLogin(HmacAlgorithm.HmacSHA256, username, token, clientIdentifier);
                        break;
                    case DEVICE_CRYPTO_LOGIN_HmacSM3:
                        cryptoType = CryptoType.SM4;
                    case DEVICE_LOGIN_HmacSM3:
                        loginAuthResult = this.deviceSignatureLogin(HmacAlgorithm.HmacSM3, username, token, clientIdentifier);
                        break;
                    default:
                        return ResultUtil.returnError(CommonCodeType.PARAM_CHECK_ERROR.getCode(), "invalid login type from password");
                }

                if (loginAuthResult.success()) {
                    MqttLoginAuthResponse data = (MqttLoginAuthResponse) loginAuthResult.getData();
                    data.setLinkAddress(nodeTag);
                    if (cryptoType != null) {
                        String signatureKey = RandomStringUtils.randomAlphanumeric(16);
                        data.setSignatureTag(String.join("-", cryptoType.getName(), signatureKey));
                    }

                    CommonResult<Void> result = this.recordRouteTable(data, keepAliveSeconds, nodeTag, sessionKey, port);
                    if (!result.success()) {
                        loginAuthResult.setCode(result.getCode());
                        loginAuthResult.setMessage(result.getMessage());
                    } else {
                        this.fetchWelcome(data, cryptoType);
                    }
                    return loginAuthResult;
                } else {
                    return loginAuthResult;
                }
            } else {
                log.info("invalid structure of password {} ", password);
                return ResultUtil.returnError(CommonCodeType.PARAM_CHECK_ERROR.getCode(), "invalid structure of password");
            }
        }
    }

    private CommonResult<MqttLoginAuthResponse> deviceSignatureLogin(SignatureAlgorithm signatureAlgorithm, String username, String token, String clientIdentifier) {
        HmacSignature hmacSignature = SignatureFactory.createHmacSignatureWithoutKey(signatureAlgorithm);
        if (hmacSignature == null) {
            log.info("signatureAlgorithm is null, signatureAlgorithm={}", signatureAlgorithm);
            return ResultUtil.returnError(CommonCodeType.PARAM_CHECK_ERROR.getCode(), "signatureAlgorithm is null");
        } else {
            return this.deviceLogin(username, token, clientIdentifier, hmacSignature);
        }
    }

    private CommonResult<MqttLoginAuthResponse> groupSignatureLogin(SignatureAlgorithm signatureAlgorithm, String username, String token, String clientIdentifier) {
        HmacSignature hmacSignature = SignatureFactory.createHmacSignatureWithoutKey(signatureAlgorithm);
        if (hmacSignature == null) {
            log.info("hmacSignature is null, signatureAlgorithm={}", signatureAlgorithm);
            return ResultUtil.returnError(CommonCodeType.PARAM_CHECK_ERROR.getCode(), "signatureAlgorithm is null");
        } else {
            return this.groupLogin(username, token, clientIdentifier, hmacSignature);
        }
    }

    private void fetchWelcome(MqttLoginAuthResponse response, CryptoType cryptoType) {
        JSONObject data = new JSONObject();
        data.put("info", "welcome to login witeam iot platform.");
        data.put("gk", response.getGroupKey());
        byte[] msg = JSONObject.toJSONBytes(data, new SerializerFeature[0]);
        String signatureTag = response.getSignatureTag();
        if (cryptoType != null) {
            data.put("cryptoSecret", signatureTag.split("-")[1]);
            msg = JSONObject.toJSONBytes(data, new SerializerFeature[0]);
            String originTag = String.join("-", cryptoType.getName(), response.getToken());
            CryptoWrapper cryptoWrapper = new CryptoWrapper(originTag);
            msg = cryptoWrapper.encrypt(msg);
        }

        WelcomeInfo welcomeInfo = (new WelcomeInfo()).setWelcomeTopic("sys/welcome").setWelcomeMsg(msg);
        response.setWelcomeInfo(welcomeInfo);
    }

    private CommonResult<Void> recordRouteTable(MqttLoginAuthResponse data, Integer keepAliveSeconds, String nodeTag, String sessionKey, Integer port) {
        SelfLinkRouteInfo selfLinkRouteInfo = (new SelfLinkRouteInfo()).setLinkTag(LinkTagUtil.createLinkTag(data.getSn(), data.getGroupKey()))
                .setNodeTag(nodeTag).setPort(port).setSessionKey(sessionKey)
                .setType(data.getDeviceType().name()).setSignatureTag(data.getSignatureTag());
        return this.routeFacade4Link.registerSelfLinkRouteInfo(selfLinkRouteInfo, keepAliveSeconds);
    }

    private CommonResult<MqttLoginAuthResponse> groupLogin(String userName, String token, String clientIdentifier, HmacSignature hmacSignature) {
        DeviceDTO deviceDTO = this.deviceMetaServiceFacade.getDeviceByLoginKey(userName, clientIdentifier);
        if (deviceDTO == null) {
            return ResultUtil.returnError(CommonCodeType.PARAM_CHECK_ERROR.getCode(), "deviceDTO is null");
        } else if (this.validDeviceType(deviceDTO.getType())) {
            return ResultUtil.returnError(CommonCodeType.PARAM_CHECK_ERROR.getCode(), "deviceType is invalid");
        } else {
            GroupDTO groupDTO = this.groupMetaServiceFacade.getGroup(deviceDTO.getGroupKey());
            if (groupDTO == null) {
                log.info("group login error,username={},length={}", userName, userName.length());
                return ResultUtil.returnError(CommonCodeType.PARAM_CHECK_ERROR.getCode(), "groupDTO is null");
            } else {
                boolean checked;
                if (hmacSignature == null) {
                    checked = groupDTO.getGroupToken().equals(token);
                } else {
                    hmacSignature.setKey(groupDTO.getGroupToken().getBytes(StandardCharsets.UTF_8));
                    checked = this.checkService.check(hmacSignature, groupDTO.getGroupToken(), token);
                }

                if (!checked) {
                    return ResultUtil.returnError(CommonCodeType.AUTHORITY_ERROR.getCode(), "token is error");
                } else {
                    DeviceTypeEnum type = deviceDTO.getType();
                    LinkDeviceType linkDeviceType = type == DeviceTypeEnum.GATEWAY ? LinkDeviceType.GATEWAY : LinkDeviceType.COMMON;
                    MqttLoginAuthResponse mqttLoginAuthResponse = (new MqttLoginAuthResponse())
                            .setGroupKey(groupDTO.getGroupKey())
                            .setSn(clientIdentifier)
                            .setLoginType(MqttChannelType.parse(MqttLoginTypeEnum.GROUP_LOGIN))
                            .setToken(groupDTO.getGroupToken())
                            .setDeviceType(linkDeviceType);
                    return ResultUtil.returnSuccess(mqttLoginAuthResponse);
                }
            }
        }
    }

    private CommonResult<MqttLoginAuthResponse> deviceLogin(String userName, String token, String clientIdentifier, HmacSignature hmacSignature) {
        if (!StringUtils.equals(userName, clientIdentifier)) {
            log.info("device key login , userName {} , and clientIdentifier {} should be the same.", userName, clientIdentifier);
            return ResultUtil.returnError(CommonCodeType.PARAM_CHECK_ERROR.getCode(), "invalid login parameter");
        } else {
            DeviceDTO deviceDTO = this.deviceMetaServiceFacade.getDevice(userName);
            if (deviceDTO == null) {
                return ResultUtil.returnError(CommonCodeType.PARAM_CHECK_ERROR.getCode(), "invalid login parameter");
            } else {
                boolean checked;
                if (hmacSignature == null) {
                    checked = deviceDTO.getDeviceToken().equals(token);
                } else {
                    hmacSignature.setKey(deviceDTO.getDeviceToken().getBytes(StandardCharsets.UTF_8));
                    checked = this.checkService.check(hmacSignature, deviceDTO.getDeviceToken(), token);
                }

                if (!checked) {
                    return ResultUtil.returnError(CommonCodeType.PARAM_CHECK_ERROR.getCode(), "invalid login parameter");
                } else if (this.validDeviceType(deviceDTO.getType())) {
                    return ResultUtil.returnError(CommonCodeType.PARAM_CHECK_ERROR.getCode(), "invalid login device type");
                } else {
                    DeviceTypeEnum type = deviceDTO.getType();
                    LinkDeviceType linkDeviceType = type == DeviceTypeEnum.GATEWAY ? LinkDeviceType.GATEWAY : LinkDeviceType.COMMON;
                    MqttLoginAuthResponse mqttLoginAuthResponse = (new MqttLoginAuthResponse()).setGroupKey(deviceDTO.getGroupKey())
                            .setSn(deviceDTO.getSn()).setLoginType(MqttChannelType.parse(MqttLoginTypeEnum.DEVICE_LOGIN))
                            .setToken(deviceDTO.getDeviceToken()).setDeviceType(linkDeviceType);
                    return ResultUtil.returnSuccess(mqttLoginAuthResponse);
                }
            }
        }
    }

    private boolean validDeviceType(DeviceTypeEnum deviceType) {
        return DeviceTypeEnum.COMMON != deviceType && DeviceTypeEnum.GATEWAY != deviceType;
    }

    private CommonResult<MqttLoginAuthResponse> mirrorLogin(String userName, String token, String clientIdentifier) {
        String key = String.join(":", clientIdentifier, userName);
        String value = (String) this.stringRedisTemplate.opsForValue().get(String.join(":", "M:L", key));
        if (value != null && value.equals(token)) {
            this.stringRedisTemplate.delete(String.join(":", "M:L", key));
            DeviceDTO deviceDTO = this.deviceMetaServiceFacade.getDevice(clientIdentifier);
            if (deviceDTO == null) {
                log.info("mirror login deviceDTO is not exit,deviceKey={}", clientIdentifier);
                return ResultUtil.returnError(CommonCodeType.PARAM_CHECK_ERROR.getCode(), "mirror login deviceDTO is not exit");
            } else {
                MqttLoginAuthResponse mqttLoginAuthResponse = (new MqttLoginAuthResponse()).setGroupKey(deviceDTO.getGroupKey()).setSn(LinkTagUtil.snToMirrorSn(deviceDTO.getSn()))
                        .setLoginType(MqttChannelType.parse(MqttLoginTypeEnum.MIRROR_LOGIN)).setDeviceType(LinkDeviceType.MIRROR);
                return ResultUtil.returnSuccess(mqttLoginAuthResponse);
            }
        } else {
            log.info("mirror login token error, value={},token={},userName={},identifier={}", new Object[]{value, token, userName, clientIdentifier});
            return ResultUtil.returnError(CommonCodeType.PARAM_CHECK_ERROR.getCode(), "mirror login token error");
        }
    }

    private CommonResult<MqttLoginAuthResponse> appLogin(String userName, String token, String clientIdentifier) {
        if (!StringUtils.equals(userName, clientIdentifier)) {
            log.info("app login , userName {} , and clientIdentifier {} should be the same.", userName, clientIdentifier);
            return ResultUtil.returnError(CommonCodeType.PARAM_CHECK_ERROR.getCode(), "invalid login parameter");
        } else {
            AppMetaModel appMetaModel = this.appMetaFacade.getAppMeta(userName);
            if (appMetaModel == null) {
                log.info("app login , app is invalid: userName {} , token {} , clientIdentifier {}", new Object[]{userName, token, clientIdentifier});
                return ResultUtil.returnError(CommonCodeType.PARAM_CHECK_ERROR.getCode(), "invalid login parameter");
            } else if (appMetaModel.getAppToken().equals(token) && userName.equals(clientIdentifier)) {
                if (!appMetaModel.isEnableSubscribe()) {
                    log.info("app login , app subscribe is not enabled: userName {} , token {} , clientIdentifier {}", new Object[]{userName, token, clientIdentifier});
                    return ResultUtil.returnError(CommonCodeType.BIZ_ERROR.getCode(), "app subscribe is not enabled");
                } else {
                    MqttLoginAuthResponse mqttLoginAuthResponse = (new MqttLoginAuthResponse()).setGroupKey(userName).setSn(clientIdentifier)
                            .setLoginType(MqttChannelType.parse(MqttLoginTypeEnum.APP_LOGIN)).setDeviceType(LinkDeviceType.APP);
                    return ResultUtil.returnSuccess(mqttLoginAuthResponse);
                }
            } else {
                log.info("app login , token is invalid: userName {} , token {} , clientIdentifier {}", new Object[]{userName, token, clientIdentifier});
                return ResultUtil.returnError(CommonCodeType.PARAM_CHECK_ERROR.getCode(), "invalid login parameter");
            }
        }
    }
}

