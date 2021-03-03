package com.dragon.modules.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class SpringContextHolder implements ApplicationContextAware, DisposableBean {
    private static ApplicationContext applicationContext = null;
    private static final List<CallBack> CALL_BACKS = new ArrayList<>();
    private static boolean addCallback = true;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if(Objects.nonNull(SpringContextHolder.applicationContext)) {
            log.warn("SpringContextHolder中的ApplicationContext被覆盖, 原有ApplicationContext为: {}", SpringContextHolder.applicationContext);
        }
        SpringContextHolder.applicationContext = applicationContext;
        if(SpringContextHolder.addCallback && !CollectionUtils.isEmpty(SpringContextHolder.CALL_BACKS)) {
            for(CallBack callBack : SpringContextHolder.CALL_BACKS) {
                callBack.executor();
            }
            SpringContextHolder.CALL_BACKS.clear();
        }
        SpringContextHolder.addCallback = false;
    }

    @Override
    public void destroy() throws Exception {
        SpringContextHolder.clearHolder();
    }

    /**
     * 清除SpringContextHolder中的ApplicationContext为Null.
     */
    private static void clearHolder() {
        log.debug("清除SpringContextHolder中的ApplicationContext:" + SpringContextHolder.applicationContext);
        SpringContextHolder.applicationContext = null;
    }

    /**
     * 针对某些初始化方法，在SpringContextHolder初始化后，进行回调使用
     * @param callBack 回调函数
     */
    public synchronized static void addCallBacks(CallBack callBack) {
        if (SpringContextHolder.addCallback) {
            SpringContextHolder.CALL_BACKS.add(callBack);
        } else {
            log.warn("CallBack：{} 已无法添加！立即执行", callBack.getCallBackName());
            callBack.executor();
        }
    }

    /**
     * 从applicationContext中取得Bean, 自动转型为所赋值对象的类型.
     */
    public static <T> T getBean(String name) {
        assertContextInjected();
        return (T) SpringContextHolder.applicationContext.getBean(name);
    }

    /**
     * 从applicationContext中取得Bean, 自动转型为所赋值对象的类型.
     */
    public static <T> T getBean(Class<T> requiredType) {
        assertContextInjected();
        return SpringContextHolder.applicationContext.getBean(requiredType);
    }

    /**
     * 检查ApplicationContext不为空.
     */
    private static void assertContextInjected() {
        if (Objects.isNull(SpringContextHolder.applicationContext)) {
            throw new IllegalStateException("applicaitonContext属性未注入, 请在applicationContext" +
                    ".xml中定义SpringContextHolder或在SpringBoot启动类中注册SpringContextHolder.");
        }
    }

    /**
     * 获取SpringBoot 配置信息
     *
     * @param property     属性key
     * @param defaultValue 默认值
     * @param requiredType 返回类型
     * @return
     */
    public static <T> T getProperties(String property, T defaultValue, Class<T> requiredType) {
        T result = defaultValue;
        try {
            result = getBean(Environment.class).getProperty(property, requiredType);
        } catch (Exception ignored) {
        }
        return result;
    }

    /**
     * 获取SpringBoot 配置信息
     *
     * @param property 属性key
     * @return
     */
    public static String getProperties(String property) {
        return getProperties(property, null, String.class);
    }

    /**
     * 获取SpringBoot 配置信息
     *
     * @param property     属性key
     * @param requiredType 返回类型
     * @return
     */
    public static <T> T getProperties(String property, Class<T> requiredType) {
        return getProperties(property, null, requiredType);
    }

}