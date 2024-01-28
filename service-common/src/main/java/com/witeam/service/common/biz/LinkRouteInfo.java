package com.witeam.service.common.biz;

import lombok.Data;

@Data
public class LinkRouteInfo {
    /**
     * 节点标识，可直接用Host
     */
    String nodeTag;

    /**
     * 链路标识
     */
    String linkTag;

    /**
     * 连接会话key
     */
    String sessionKey;
}
