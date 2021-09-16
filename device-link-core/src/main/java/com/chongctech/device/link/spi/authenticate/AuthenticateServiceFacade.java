package com.chongctech.device.link.spi.authenticate;

import com.chongctech.device.link.biz.model.authenticate.AuthenticateResponse;
import com.chongctech.service.common.call.result.CommonResult;

public interface AuthenticateServiceFacade {
    /**
     * 设备链路认证,需分配linkTag与sessionKey
     *
     * @param clientIdentifier the clientId in the Connect Message
     * @param username         the userName in the Connect Message
     * @param password         the password in the Connect Message
     * @param sessionKey       link session
     * @param nodeTag          node ip
     * @param port             rpc port
     * @param keepAliveSeconds KeepAlive
     * @return if loginCheck passed, then return the clientInfo, otherwise return null
     */
    CommonResult<AuthenticateResponse> authenticate(String clientIdentifier, String username, String password,
                                                    String sessionKey, String nodeTag, Integer port,
                                                    int keepAliveSeconds);
}
