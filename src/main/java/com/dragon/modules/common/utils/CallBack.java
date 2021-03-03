package com.dragon.modules.common.utils;

/**
 * 在SpringContextHolder初始化后，进行回调使用
 */
public interface CallBack {

    /**
     * 回调执行方法
     */
    void executor();

    /**
     * 本回调任务名称
     * @return
     */
    default String getCallBackName() {
        return Thread.currentThread().getId() + "." + this.getClass().getName();
    }

}
