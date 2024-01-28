package com.witeam.device.common.model.authenticate.req.domain.mqtt;

/**
 * @author gow 2024/01/23
 */
public enum MqttChannelType {
    PUSH,
    SUBSCRIBE,
    PUSH_AND_SUBSCRIBE,
    UNKNOWN;

    private MqttChannelType() {
    }

    public static MqttChannelType parse(MqttLoginTypeEnum loginType) {
        switch(loginType) {
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
                return PUSH;
            case SUB_GROUP_LOGIN:
            case MIRROR_LOGIN:
            case APP_LOGIN:
                return SUBSCRIBE;
            default:
                return UNKNOWN;
        }
    }
}