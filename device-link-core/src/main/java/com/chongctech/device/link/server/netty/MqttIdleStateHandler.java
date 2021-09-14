package com.chongctech.device.link.server.netty;


import com.chongctech.device.link.server.netty.domain.ChannelAliveEvent;
import io.netty.channel.Channel;
import io.netty.channel.Channel.Unsafe;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * @author gow
 */
@Slf4j
public class MqttIdleStateHandler extends ChannelDuplexHandler {
    private static final long MIN_TIMEOUT_NANOS;
    private final ChannelFutureListener writeListener;
    private final boolean observeOutput;
    private final long readerIdleTimeNanos;
    private final long writerIdleTimeNanos;
    private final long allIdleTimeNanos;
    private ScheduledFuture<?> readerIdleTimeout;
    private long lastReadTime;
    private boolean firstReaderIdleEvent;
    private ScheduledFuture<?> writerIdleTimeout;
    private long lastWriteTime;
    private boolean firstWriterIdleEvent;
    private ScheduledFuture<?> allIdleTimeout;
    private boolean firstAllIdleEvent;
    private byte state;
    private boolean reading;
    private long lastChangeCheckTimeStamp;
    private int lastMessageHashCode;
    private long lastPendingWriteBytes;
    private long lastFlushProgress;

    public MqttIdleStateHandler(int readerIdleTimeSeconds, int writerIdleTimeSeconds, int allIdleTimeSeconds) {
        this(readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds, TimeUnit.SECONDS);
    }

    public MqttIdleStateHandler(long readerIdleTime, long writerIdleTime, long allIdleTime, TimeUnit unit) {
        this(false, readerIdleTime, writerIdleTime, allIdleTime, unit);
    }

    public MqttIdleStateHandler(boolean observeOutput, long readerIdleTime, long writerIdleTime, long allIdleTime,
                                TimeUnit unit) {
        this.writeListener = future -> {
            MqttIdleStateHandler.this.lastWriteTime = MqttIdleStateHandler.this.ticksInNanos();
            MqttIdleStateHandler.this.firstWriterIdleEvent = MqttIdleStateHandler.this.firstAllIdleEvent = true;
        };
        this.firstReaderIdleEvent = true;
        this.firstWriterIdleEvent = true;
        this.firstAllIdleEvent = true;
        if (unit == null) {
            throw new NullPointerException("unit");
        } else {
            this.observeOutput = observeOutput;
            if (readerIdleTime <= 0L) {
                this.readerIdleTimeNanos = 0L;
            } else {
                this.readerIdleTimeNanos = Math.max(unit.toNanos(readerIdleTime), MIN_TIMEOUT_NANOS);
            }

            if (writerIdleTime <= 0L) {
                this.writerIdleTimeNanos = 0L;
            } else {
                this.writerIdleTimeNanos = Math.max(unit.toNanos(writerIdleTime), MIN_TIMEOUT_NANOS);
            }

            if (allIdleTime <= 0L) {
                this.allIdleTimeNanos = 0L;
            } else {
                this.allIdleTimeNanos = Math.max(unit.toNanos(allIdleTime), MIN_TIMEOUT_NANOS);
            }

        }
    }

    public long getReaderIdleTimeInMillis() {
        return TimeUnit.NANOSECONDS.toMillis(this.readerIdleTimeNanos);
    }

    public long getWriterIdleTimeInMillis() {
        return TimeUnit.NANOSECONDS.toMillis(this.writerIdleTimeNanos);
    }

    public long getAllIdleTimeInMillis() {
        return TimeUnit.NANOSECONDS.toMillis(this.allIdleTimeNanos);
    }

    public void handlerAdded(ChannelHandlerContext ctx) {
        if (ctx.channel().isActive() && ctx.channel().isRegistered()) {
            this.initialize(ctx);
        }

    }

    public void handlerRemoved(ChannelHandlerContext ctx) {
        this.destroy();
    }

    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        if (ctx.channel().isActive()) {
            this.initialize(ctx);
        }

        super.channelRegistered(ctx);
    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.initialize(ctx);
        super.channelActive(ctx);
    }

    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.destroy();
        super.channelInactive(ctx);
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof MqttMessage) {
            //mqtt包，才进行计时刷新
            if (this.readerIdleTimeNanos > 0L || this.allIdleTimeNanos > 0L) {
                this.reading = true;
                this.firstReaderIdleEvent = this.firstAllIdleEvent = true;
            }

            ctx.fireChannelRead(msg);
        } else {
            log.warn("receive none mqtt package , channel : {}", ctx.channel());
        }
    }

    public void channelReadComplete(ChannelHandlerContext ctx) {
        if ((this.readerIdleTimeNanos > 0L || this.allIdleTimeNanos > 0L) && this.reading) {
            this.lastReadTime = this.ticksInNanos();
            this.reading = false;
        }

        ctx.fireChannelReadComplete();
    }

    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        if (this.writerIdleTimeNanos <= 0L && this.allIdleTimeNanos <= 0L) {
            ctx.write(msg, promise);
        } else {
            ctx.write(msg, promise.unvoid()).addListener(this.writeListener);
        }

    }

    private void initialize(ChannelHandlerContext ctx) {
        switch (this.state) {
            case 1:
            case 2:
                return;
            default:
                this.state = 1;
                this.initOutputChanged(ctx);
                this.lastReadTime = this.lastWriteTime = this.ticksInNanos();
                if (this.readerIdleTimeNanos > 0L) {
                    this.readerIdleTimeout =
                            this.schedule(ctx, new ReaderIdleTimeoutTask(ctx), this.readerIdleTimeNanos,
                                    TimeUnit.NANOSECONDS);
                }

                if (this.writerIdleTimeNanos > 0L) {
                    this.writerIdleTimeout =
                            this.schedule(ctx, new WriterIdleTimeoutTask(ctx), this.writerIdleTimeNanos,
                                    TimeUnit.NANOSECONDS);
                }

                if (this.allIdleTimeNanos > 0L) {
//                    log.debug("MqttIdleStateHandler initialize allIdleTimeNacos={}", allIdleTimeNanos);
                    this.allIdleTimeout = this.schedule(ctx, new AllIdleTimeoutTask(ctx), this.allIdleTimeNanos,
                            TimeUnit.NANOSECONDS);
                }

        }
    }

    long ticksInNanos() {
        return System.nanoTime();
    }

    ScheduledFuture<?> schedule(ChannelHandlerContext ctx, Runnable task, long delay, TimeUnit unit) {
        return ctx.executor().schedule(task, delay, unit);
    }

    private void destroy() {
        this.state = 2;
        if (this.readerIdleTimeout != null) {
            this.readerIdleTimeout.cancel(false);
            this.readerIdleTimeout = null;
        }

        if (this.writerIdleTimeout != null) {
            this.writerIdleTimeout.cancel(false);
            this.writerIdleTimeout = null;
        }

        if (this.allIdleTimeout != null) {
            this.allIdleTimeout.cancel(false);
            this.allIdleTimeout = null;
        }

    }

    /**
     * 发送刷新事件
     *
     * @param ctx channel上下文
     */
    protected void channelRefresh(ChannelHandlerContext ctx) {
        ChannelAliveEvent evt = new ChannelAliveEvent()
                .setChannelAliveCheckTime((int) (MqttIdleStateHandler.this.allIdleTimeNanos / 1000000000));
        ctx.fireUserEventTriggered(evt);
    }

    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) {
        ctx.fireUserEventTriggered(evt);
    }

    protected IdleStateEvent newIdleStateEvent(IdleState state, boolean first) {
        switch (state) {
            case ALL_IDLE:
                return first ? IdleStateEvent.FIRST_ALL_IDLE_STATE_EVENT : IdleStateEvent.ALL_IDLE_STATE_EVENT;
            case READER_IDLE:
                return first ? IdleStateEvent.FIRST_READER_IDLE_STATE_EVENT : IdleStateEvent.READER_IDLE_STATE_EVENT;
            case WRITER_IDLE:
                return first ? IdleStateEvent.FIRST_WRITER_IDLE_STATE_EVENT : IdleStateEvent.WRITER_IDLE_STATE_EVENT;
            default:
                throw new IllegalArgumentException("Unhandled: state=" + state + ", first=" + first);
        }
    }

    private void initOutputChanged(ChannelHandlerContext ctx) {
        if (this.observeOutput) {
            Channel channel = ctx.channel();
            Unsafe unsafe = channel.unsafe();
            ChannelOutboundBuffer buf = unsafe.outboundBuffer();
            if (buf != null) {
                this.lastMessageHashCode = System.identityHashCode(buf.current());
                this.lastPendingWriteBytes = buf.totalPendingWriteBytes();
                this.lastFlushProgress = buf.currentProgress();
            }
        }

    }

    private boolean hasOutputChanged(ChannelHandlerContext ctx, boolean first) {
        if (this.observeOutput) {
            if (this.lastChangeCheckTimeStamp != this.lastWriteTime) {
                this.lastChangeCheckTimeStamp = this.lastWriteTime;
                if (!first) {
                    return true;
                }
            }

            Channel channel = ctx.channel();
            Unsafe unsafe = channel.unsafe();
            ChannelOutboundBuffer buf = unsafe.outboundBuffer();
            if (buf != null) {
                int messageHashCode = System.identityHashCode(buf.current());
                long pendingWriteBytes = buf.totalPendingWriteBytes();
                if (messageHashCode != this.lastMessageHashCode || pendingWriteBytes != this.lastPendingWriteBytes) {
                    this.lastMessageHashCode = messageHashCode;
                    this.lastPendingWriteBytes = pendingWriteBytes;
                    if (!first) {
                        return true;
                    }
                }

                long flushProgress = buf.currentProgress();
                if (flushProgress != this.lastFlushProgress) {
                    this.lastFlushProgress = flushProgress;
                    return !first;
                }
            }
        }

        return false;
    }

    static {
        MIN_TIMEOUT_NANOS = TimeUnit.MILLISECONDS.toNanos(1L);
    }

    private final class AllIdleTimeoutTask extends AbstractIdleTask {
        AllIdleTimeoutTask(ChannelHandlerContext ctx) {
            super(ctx);
        }

        protected void run(ChannelHandlerContext ctx) {
            long nextDelay = MqttIdleStateHandler.this.allIdleTimeNanos;

            if (!MqttIdleStateHandler.this.reading) {
                nextDelay -= MqttIdleStateHandler.this.ticksInNanos() - Math
                        .max(MqttIdleStateHandler.this.lastReadTime, MqttIdleStateHandler.this.lastWriteTime);
            }
            if (nextDelay <= 0L) {
                MqttIdleStateHandler.this.allIdleTimeout = MqttIdleStateHandler.this
                        .schedule(ctx, this, MqttIdleStateHandler.this.allIdleTimeNanos, TimeUnit.NANOSECONDS);
                boolean first = MqttIdleStateHandler.this.firstAllIdleEvent;
                MqttIdleStateHandler.this.firstAllIdleEvent = false;

                try {
                    if (MqttIdleStateHandler.this.hasOutputChanged(ctx, first)) {
                        MqttIdleStateHandler.this.channelRefresh(ctx);
                        return;
                    }
                    IdleStateEvent event = MqttIdleStateHandler.this.newIdleStateEvent(IdleState.ALL_IDLE, first);
                    MqttIdleStateHandler.this.channelIdle(ctx, event);
                } catch (Throwable var6) {
                    ctx.fireExceptionCaught(var6);
                }
            } else {

                MqttIdleStateHandler.this.channelRefresh(ctx);
                MqttIdleStateHandler.this.allIdleTimeout =
                        MqttIdleStateHandler.this.schedule(ctx, this, nextDelay, TimeUnit.NANOSECONDS);
            }

        }
    }

    private final class WriterIdleTimeoutTask extends AbstractIdleTask {
        WriterIdleTimeoutTask(ChannelHandlerContext ctx) {
            super(ctx);
        }

        protected void run(ChannelHandlerContext ctx) {
            long lastWriteTime = MqttIdleStateHandler.this.lastWriteTime;
            long nextDelay = MqttIdleStateHandler.this.writerIdleTimeNanos - (MqttIdleStateHandler.this.ticksInNanos()
                    - lastWriteTime);
            if (nextDelay <= 0L) {
                MqttIdleStateHandler.this.writerIdleTimeout = MqttIdleStateHandler.this
                        .schedule(ctx, this, MqttIdleStateHandler.this.writerIdleTimeNanos, TimeUnit.NANOSECONDS);
                boolean first = MqttIdleStateHandler.this.firstWriterIdleEvent;
                MqttIdleStateHandler.this.firstWriterIdleEvent = false;

                try {
                    if (MqttIdleStateHandler.this.hasOutputChanged(ctx, first)) {
                        return;
                    }

                    IdleStateEvent event = MqttIdleStateHandler.this.newIdleStateEvent(IdleState.WRITER_IDLE, first);
                    MqttIdleStateHandler.this.channelIdle(ctx, event);
                } catch (Throwable var8) {
                    ctx.fireExceptionCaught(var8);
                }
            } else {
                MqttIdleStateHandler.this.writerIdleTimeout =
                        MqttIdleStateHandler.this.schedule(ctx, this, nextDelay, TimeUnit.NANOSECONDS);
            }

        }
    }

    private final class ReaderIdleTimeoutTask extends AbstractIdleTask {
        ReaderIdleTimeoutTask(ChannelHandlerContext ctx) {
            super(ctx);
        }

        protected void run(ChannelHandlerContext ctx) {
            long nextDelay = MqttIdleStateHandler.this.readerIdleTimeNanos;
            if (!MqttIdleStateHandler.this.reading) {
                nextDelay -= MqttIdleStateHandler.this.ticksInNanos() - MqttIdleStateHandler.this.lastReadTime;
            }

            if (nextDelay <= 0L) {
                MqttIdleStateHandler.this.readerIdleTimeout = MqttIdleStateHandler.this
                        .schedule(ctx, this, MqttIdleStateHandler.this.readerIdleTimeNanos, TimeUnit.NANOSECONDS);
                boolean first = MqttIdleStateHandler.this.firstReaderIdleEvent;
                MqttIdleStateHandler.this.firstReaderIdleEvent = false;

                try {
                    IdleStateEvent event = MqttIdleStateHandler.this.newIdleStateEvent(IdleState.READER_IDLE, first);
                    MqttIdleStateHandler.this.channelIdle(ctx, event);
                } catch (Throwable var6) {
                    ctx.fireExceptionCaught(var6);
                }
            } else {
                MqttIdleStateHandler.this.readerIdleTimeout =
                        MqttIdleStateHandler.this.schedule(ctx, this, nextDelay, TimeUnit.NANOSECONDS);
            }

        }
    }

    private abstract static class AbstractIdleTask implements Runnable {
        private final ChannelHandlerContext ctx;

        AbstractIdleTask(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        public void run() {
            if (this.ctx.channel().isOpen()) {
                this.run(this.ctx);
            }
        }

        protected abstract void run(ChannelHandlerContext var1);
    }
}

