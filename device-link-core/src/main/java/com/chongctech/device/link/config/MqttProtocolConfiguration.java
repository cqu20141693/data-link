package com.chongctech.device.link.config;

import io.netty.util.NettyRuntime;
import io.netty.util.internal.SystemPropertyUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "com.chongctech.device.link.mqtt")
public class MqttProtocolConfiguration {
    /**
     * the listening host of mqtt server,default value "0.0.0.0"
     */
    private String host = "0.0.0.0";

    /**
     * the listen port of the mqtt server,default value:1883
     */
    private int port = 1883;

    /**
     * the thread size used to listen,default value: 2
     */
    private int bossGroupSize = 2;

    /**
     * the thread size used as worker,default value: 4
     */
    private int workerGroupSize = Math.max(1, SystemPropertyUtil
            .getInt("io.netty.eventLoopThreads", NettyRuntime.availableProcessors() * 2));

    /**
     * the thread size use as backend thread pool,default value: 4
     */
    private int processGroupSize = 4;

    /**
     * 是否支持Websocket,default value: false;
     */
    private boolean enableWebSocket = false;

    /**
     * the max client this link broker allow to connect. if the client count reach the maxClient, the link broker will
     * refuse new client login
     */
    private int maxLink = 8000;

    /**
     * the mininal seconds of the hearbeat, if the client heart beat is less than this value, the server will refuse the
     * connection
     * default value: 120
     */
    private int minHeartBeatSecond = 120;
    /**
     * mqtt 报文最大字节数
     */
    private int maxBytesInMessage = 8092;
}
