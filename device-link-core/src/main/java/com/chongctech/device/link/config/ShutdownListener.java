package com.chongctech.device.link.config;


import com.chongctech.device.link.biz.link.LinkStatusHandler;
import com.chongctech.device.link.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

@Component
public class ShutdownListener implements ApplicationListener<ContextClosedEvent> {

    private static Logger logger = LoggerFactory.getLogger(ShutdownListener.class);

    @Autowired
    private LinkStatusHandler linkStatusHandler;

    @Autowired
    private Server server;

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        logger.info("ShutdownListener::onApplicationEvent() is called");

        server.stopServer();
        // 断开所有连接
        linkStatusHandler.disconnectAllLink();
    }
}


