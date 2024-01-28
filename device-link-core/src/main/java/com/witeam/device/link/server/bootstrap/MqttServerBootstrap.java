package com.witeam.device.link.server.bootstrap;

import com.witeam.device.link.server.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

/**
 * @author gow
 * @date 2021/7/13
 */
@Slf4j
public class MqttServerBootstrap implements CommandLineRunner {
    @Autowired
    private Server server;

    @Override
    public void run(String... args) throws Exception {
        // 启动mqtt server
        server.startServer();
        log.info("server started");
    }
}
