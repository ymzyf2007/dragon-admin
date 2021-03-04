package com.dragon.config.thread;

import com.dragon.modules.common.utils.SpringContextHolder;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 用于获取自定义线程池
 */
public class ThreadPoolExecutorUtil {

    public static ThreadPoolExecutor getThreadPool() {
        TaskThreadPoolConfig config = SpringContextHolder.getBean(TaskThreadPoolConfig.class);
        return new ThreadPoolExecutor(
                config.getCorePoolSize(),
                config.getMaxPoolSize(),
                config.getKeepAliveSeconds(),
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(config.getQueueCapacity()),
                new CustomTheadFactory()
        );
    }

}