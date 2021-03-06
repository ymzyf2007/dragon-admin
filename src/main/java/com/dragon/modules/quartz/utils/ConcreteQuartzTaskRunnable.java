package com.dragon.modules.quartz.utils;

import com.dragon.modules.common.utils.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * 执行定时任务
 */
@Slf4j
public class ConcreteQuartzTaskRunnable implements Callable<Object> {

    private final Object target;
    private final Method method;
    private final String params;

    public ConcreteQuartzTaskRunnable(String beanName, String methodName, String params) throws NoSuchMethodException {
        this.target = SpringContextHolder.getBean(beanName);
        this.params = params;
        if(StringUtils.isNotBlank(params)) {
            this.method = target.getClass().getDeclaredMethod(methodName, String.class);
        } else {
            this.method = target.getClass().getDeclaredMethod(methodName);
        }
    }

    @Override
    public Object call() throws Exception {
        // 使 method 变为可访问
        ReflectionUtils.makeAccessible(method);
        if (StringUtils.isNotBlank(params)) {
            method.invoke(target, params);
        } else {
            method.invoke(target);
        }
        return null;
    }

}
