package com.chongctech.device.link.biz.session;

import com.chongctech.device.link.biz.model.link.LinkInfo;
import com.chongctech.device.link.biz.model.link.SendInfo;
import com.chongctech.device.link.config.MqttProtocolConfiguration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class LinkSession {
    private static final Logger logger = LoggerFactory.getLogger(LinkSession.class);

    @Autowired
    private MqttProtocolConfiguration mqttConfig;

    /**
     * linkTag --> linkInfo,
     */
    private ConcurrentHashMap<String, LinkInfo> linkInfoMap;

    @PostConstruct
    public void init() {
        linkInfoMap = new ConcurrentHashMap<>(mqttConfig.getMaxLink() + 10000,
                0.95f, 32);
    }

    public LinkInfo getLinkInfo(String linkTag) {
        if (StringUtils.isEmpty(linkTag)) {
            return null;
        }
        return linkInfoMap.get(linkTag);
    }

    /**
     * 检查是否存在链接
     */
    public boolean containLink(String linkTag) {
        if (StringUtils.isEmpty(linkTag)) {
            return false;
        }
        return linkInfoMap.containsKey(linkTag);
    }

    /**
     * @param linkTag  链路唯一标识
     * @param linkInfo 链路信息
     * @return pre linkInfo
     */
    public LinkInfo recordLink(String linkTag, LinkInfo linkInfo) {
        return linkInfoMap.putIfAbsent(linkTag, linkInfo);
    }

    /**
     * @param linkTag 链路唯一标识
     */
    public LinkInfo removeLink(String linkTag) {
        if (!StringUtils.isEmpty(linkTag)) {
            return linkInfoMap.remove(linkTag);
        }
        return null;
    }

    /**
     * 对所有链接执行指定动作
     *
     * @param action 执行动作
     */
    public void executeForEachLink(Consumer<LinkInfo> action) {
        //依次执行 避免并发。
        linkInfoMap.forEach((k, v) -> action.accept(v));
    }

    public void printTraceLink(List<String> traceLinkList) {
        if (!CollectionUtils.isEmpty(traceLinkList)) {
            for (String linkTag : traceLinkList) {
                logger.info("LinkTag:" + linkTag + ", LinkInfo:" + linkInfoMap.get(linkTag));
            }
        }
    }

}
