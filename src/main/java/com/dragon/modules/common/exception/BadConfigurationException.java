package com.dragon.modules.common.exception;

/**
 * 统一关于错误配置信息异常
 */
public class BadConfigurationException extends RuntimeException {

    public BadConfigurationException() {
        super();
    }

    public BadConfigurationException(String message) {
        super(message);
    }

    public BadConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadConfigurationException(Throwable cause) {
        super(cause);
    }

    protected BadConfigurationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
