package com.chongctech.device.link.server.netty;

import com.chongctech.device.link.biz.link.LinkStatusHandler;
import com.chongctech.device.link.biz.stream.down.DownStreamHandler;
import com.chongctech.device.link.config.MqttProtocolConfiguration;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.util.concurrent.Future;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NettyAcceptor {
    private static final String MQTT_SUBPROTOCOL_CSV_LIST = "mqtt, mqttv3.1, mqttv3.1.1";
    private static final Logger logger = LoggerFactory.getLogger(NettyAcceptor.class);

    /**
     * 在TCP连接成功后，收到Connect消息之前的超时时间间隔，默认为20秒
     */
    private final static int MAX_CONNECT_TIMEOUT = 60;
    /**
     * netty acceptor中用于超时判断的计时器
     */
    private final static String IDLE_STAT_HANDLER = "idleStateHandler";


    private final NettyMqttHandler handler;

    private final MqttProtocolConfiguration config;
    /**
     * 监听的事件循环
     */
    private EventLoopGroup bossGroup;
    /**
     * 工作的事件循环
     */
    private EventLoopGroup workerGroup;

    /**
     * 下行操作类
     */
    private DownStreamHandler downStreamHandler;
    /**
     * 状态操作类
     */
    private LinkStatusHandler linkStatusHandler;

    @Autowired
    public NettyAcceptor(MqttProtocolConfiguration configuration
            , NettyMqttHandler nettyMqttHandler
            , DownStreamHandler downStreamHandler
            , LinkStatusHandler linkStatusHandler) {
        this.config = configuration;
        this.handler = nettyMqttHandler;
        this.downStreamHandler = downStreamHandler;
        this.linkStatusHandler = linkStatusHandler;
    }

    public void initialize() throws IOException {
        bossGroup = new NioEventLoopGroup(config.getBossGroupSize());
        workerGroup = new NioEventLoopGroup(config.getWorkerGroupSize());
        initializePlainTCPTransport();
        if (config.isEnableWebSocket()) {
            initializeWebSocketTransport();
        }
    }

    private void initFactory(String host, int port, final AbstractPipelineInitializer pipeliner) {
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        try {
                            pipeliner.init(pipeline);
                        } catch (Throwable th) {
                            logger.error("Severe error during pipeline creation", th);
                            throw th;
                        }
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                // 设置写Buffer的水位
                .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(1024 * 8, 1024 * 16))
                .childOption(ChannelOption.SO_SNDBUF, 32 * 1024)
                .childOption(ChannelOption.SO_RCVBUF, 32 * 1024)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.SO_KEEPALIVE, false)
                .childOption(ChannelOption.TCP_NODELAY, true);
        try {
            ChannelFuture f = b.bind(host, port);
            f.sync();
        } catch (InterruptedException ex) {
            logger.error("bind server ", ex);
        }
    }

    private void initializePlainTCPTransport() throws IOException {
        String host = config.getHost();
        int port = config.getPort();
        initFactory(host, port, new AbstractPipelineInitializer() {
            @Override
            void init(ChannelPipeline pipeline) {
                pipeline.addFirst("decoder", new MqttDecoder());
                pipeline.addAfter("decoder", "encoder", MqttEncoder.INSTANCE);
                pipeline.addLast(IDLE_STAT_HANDLER, new MqttIdleStateHandler(0,
                        0, config.getMinHeartBeatSecond()));
                pipeline.addLast("idleEventHandler", new MqttUserEventHandler(downStreamHandler, linkStatusHandler));
                pipeline.addLast("handler", handler);
            }
        });
        logger.info("Started TCP on host: {}, port {}", host, port);
    }

    private void initializeWebSocketTransport() throws IOException {
        String host = config.getHost();
        int port = config.getPort() + 1000;
        initFactory(host, port, new AbstractPipelineInitializer() {
            @Override
            void init(ChannelPipeline pipeline) throws Exception {
                pipeline.addLast(new HttpServerCodec());
                pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
                pipeline.addLast("webSocketHandler",
                        new WebSocketServerProtocolHandler("/mqtt", MQTT_SUBPROTOCOL_CSV_LIST));
                pipeline.addLast("ws2bytebufDecoder", new WebSocketFrameToByteBufDecoder());
                pipeline.addLast("bytebuf2wsEncoder", new ByteBufToWebSocketFrameEncoder());
                pipeline.addLast("decoder", new MqttDecoder());
                pipeline.addLast("encoder", MqttEncoder.INSTANCE);
                pipeline.addLast(IDLE_STAT_HANDLER, new MqttIdleStateHandler(0,
                        0, config.getMinHeartBeatSecond()));
                pipeline.addLast("idleEventHandler", new MqttUserEventHandler(downStreamHandler, linkStatusHandler));
                pipeline.addLast("handler", handler);
            }
        });
        logger.info("Started websocket on host: {}, port {}", host, port);
    }

    public void close() {
        if (workerGroup == null) {
            throw new IllegalStateException("Invoked close on an Acceptor that wasn't initialized");
        }
        if (bossGroup == null) {
            throw new IllegalStateException("Invoked close on an Acceptor that wasn't initialized");
        }
        Future workerWaiter = workerGroup.shutdownGracefully();
        Future bossWaiter = bossGroup.shutdownGracefully();

        try {
            workerWaiter.await(1000);
        } catch (InterruptedException iex) {
            throw new IllegalStateException(iex);
        }

        try {
            bossWaiter.await(1000);
        } catch (InterruptedException iex) {
            throw new IllegalStateException(iex);
        }
    }

    static class WebSocketFrameToByteBufDecoder extends MessageToMessageDecoder<BinaryWebSocketFrame> {

        @Override
        protected void decode(ChannelHandlerContext chc, BinaryWebSocketFrame frame, List<Object> out)
                throws Exception {
            // convert the frame to a ByteBuf
            ByteBuf bb = frame.content();
            // System.out.println("WebSocketFrameToByteBufDecoder decode - " +
            // ByteBufUtil.hexDump(bb));
            bb.retain();
            out.add(bb);
        }
    }

    static class ByteBufToWebSocketFrameEncoder extends MessageToMessageEncoder<ByteBuf> {

        @Override
        protected void encode(ChannelHandlerContext chc, ByteBuf bb, List<Object> out) throws Exception {
            // convert the ByteBuf to a WebSocketFrame
            BinaryWebSocketFrame result = new BinaryWebSocketFrame();
            // System.out.println("ByteBufToWebSocketFrameEncoder encode - " +
            // ByteBufUtil.hexDump(bb));
            result.content().writeBytes(bb);
            out.add(result);
        }
    }

    abstract class AbstractPipelineInitializer {
        /**
         * netty pipeline 初始化
         *
         * @param pipeline 输入的pipeline对象
         * @throws Exception
         */
        abstract void init(ChannelPipeline pipeline) throws Exception;
    }
}
