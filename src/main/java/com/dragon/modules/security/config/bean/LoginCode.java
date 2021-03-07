package com.dragon.modules.security.config.bean;

import lombok.Data;

/**
 * 登录验证码配置信息
 */
@Data
public class LoginCode {
    /**
     * 验证码类型
     */
    private LoginCodeEnum codeType;
    /**
     * 验证码宽度
     */
    private int width = 111;
    /**
     * 验证码高度
     */
    private int height = 36;
    /**
     * 验证码字体,值为空则使用默认字体
     */
    private String fontName;
    /**
     * 字体大小
     */
    private int fontSize = 25;
    /**
     * 验证码内容长度
     */
    private int length = 2;
    /**
     * 验证码有效期 分钟
     */
    private Long expiration = 2L;
}
