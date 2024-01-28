package com.witeam.device.link.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class NodeUtil {
    private static Logger logger = LoggerFactory.getLogger(NodeUtil.class);

    /**
     * The Environment.
     */
    @Autowired
    private Environment environment;

    private String nodeTag;

    @Value("${server.port:8080}")
    private Integer port;

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @PostConstruct
    public void init() {
        try {
            String ip = environment.getProperty("server.address");
            if (StringUtils.isEmpty(ip)) {
                ip = InetAddress.getLocalHost().getHostAddress();
            }
            nodeTag = ip;
        } catch (UnknownHostException e) {
            logger.error("get local ip failed!", e);
        }
        logger.info("nodeTag={},port={}", nodeTag, port);
    }

    public String getNodeTag() {
        return nodeTag;
    }

    public void setNodeTag(String nodeTag) {
        this.nodeTag = nodeTag;
    }
}
