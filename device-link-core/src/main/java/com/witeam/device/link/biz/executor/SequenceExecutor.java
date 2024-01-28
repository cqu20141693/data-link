package com.witeam.device.link.biz.executor;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author witeam
 * @date 2019/11/7 15:06
 */
public class SequenceExecutor implements Executor {
    private static final Logger logger = LoggerFactory.getLogger(SequenceExecutor.class);

    /**
     * 代理的executor
     */
    private final Executor delegateExecutor;

    private final LinkedBlockingQueue<Runnable> processQueue;

    /**
     * 顺序工作者
     */
    private final SequenceWorker worker;

    public SequenceExecutor(Executor delegateExecutor, int queueSize) {
        this.delegateExecutor = delegateExecutor;
        this.processQueue = new LinkedBlockingQueue<>(queueSize);
        this.worker = new SequenceWorker(processQueue);
    }

    @Override
    public void execute(@NonNull final Runnable runnable) {
        //提交任务入队
        if (!processQueue.offer(runnable)) {
            throw new RejectedExecutionException("SequenceExecutor queue is full.");
        }
        //尝试启动队列任务,double check
        if (worker.getState() == WorkerState.IDLE
                || worker.getState() == WorkerState.PRE_IDLE) {
            synchronized (worker) {
                //二次检查工作者状态与队列内任务数量
                if (worker.getState() == WorkerState.IDLE
                        && processQueue.size() > 0) {
                    worker.setState(WorkerState.RUNNING);
                    delegateExecutor.execute(worker);
                }
            }
        }
    }

    enum WorkerState {
        IDLE,
        PRE_IDLE,
        RUNNING
    }

    private final class SequenceWorker implements Runnable {

        private final LinkedBlockingQueue<Runnable> processQueue;

        private volatile WorkerState state;

        SequenceWorker(LinkedBlockingQueue<Runnable> processQueue) {
            this.processQueue = processQueue;
            this.state = WorkerState.IDLE;
        }

        WorkerState getState() {
            return state;
        }

        void setState(WorkerState state) {
            this.state = state;
        }

        @Override
        public void run() {
            boolean interruptedDuringTask = false;
            try {
                while (true) {
                    Runnable task = processQueue.poll();
                    if (task == null) {
                        //设定将停止工作,状态将标识新的task由存在worker优先处理
                        setState(WorkerState.PRE_IDLE);
                        synchronized (worker) {
                            //double poll
                            task = processQueue.poll();
                            if (task == null) {
                                //仍然没任务则停止工作
                                setState(WorkerState.IDLE);
                                return;
                            } else {
                                //已存在,设定继续开始工作
                                setState(WorkerState.RUNNING);
                            }
                        }
                    }
                    //清理并记录线程interrupted位
                    interruptedDuringTask |= Thread.interrupted();
                    try {
                        task.run();
                    } catch (RuntimeException e) {
                        logger.error("Exception while executing runnable " + task, e);
                    }
                }
            } catch (Error error) {
                logger.error("SequenceWorker error happen ", error);
            } finally {
                setState(WorkerState.IDLE);
                if (interruptedDuringTask) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
