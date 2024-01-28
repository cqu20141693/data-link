package com.witeam;

import com.witeam.device.link.server.bootstrap.EnableMqttServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author gow
 * @date 2021/7/13
 */
@Slf4j
@SpringBootApplication
@EnableFeignClients
@EnableMqttServer
public class App {
    public static void main(String[] args) {

        SpringApplication.run(App.class, args);
    }
}
