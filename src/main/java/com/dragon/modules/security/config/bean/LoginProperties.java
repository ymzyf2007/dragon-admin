package com.dragon.modules.security.config.bean;

import com.dragon.modules.common.exception.BadConfigurationException;
import com.wf.captcha.*;
import com.wf.captcha.base.Captcha;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.util.Objects;

/**
 * 从配置文件读取
 */
@Data
public class LoginProperties {

    /**
     * 验证码信息
     */
    private LoginCode loginCode;
    /**
     * 账号单用户 登录
     */
    private boolean singleLogin = false;
    /**
     * 用户登录信息缓存
     */
    private boolean cacheEnable;

    /**
     * 获取验证码
     * @return
     */
    public Captcha getCaptcha() {
        if(Objects.isNull(loginCode)) {
            loginCode = new LoginCode();
            if(Objects.isNull(loginCode.getCodeType())) {
                loginCode.setCodeType(LoginCodeEnum.arithmetic);
            }
        }
        return switchCaptcha(loginCode);
    }

    /**
     * 依据配置信息生产验证码
     * @param loginCode
     * @return
     */
    private Captcha switchCaptcha(LoginCode loginCode) {
        Captcha captcha;
        synchronized (this) {
            switch (loginCode.getCodeType()) {
                case arithmetic:
                    // 算术类型,几位数运算，默认是两位
                    // https://gitee.com/whvse/EasyCaptcha
                    captcha = new ArithmeticCaptcha(loginCode.getWidth(), loginCode.getHeight(), loginCode.getLength());
                    break;
                case chinese:
                    captcha = new ChineseCaptcha(loginCode.getWidth(), loginCode.getHeight(), loginCode.getLength());
                    break;
                case chinese_gif:
                    captcha = new ChineseGifCaptcha(loginCode.getWidth(), loginCode.getHeight(), loginCode.getLength());
                    captcha.setLen(loginCode.getLength());
                    break;
                case gif:
                    captcha = new GifCaptcha(loginCode.getWidth(), loginCode.getHeight(), loginCode.getLength());
                    break;
                case spec:
                    captcha = new SpecCaptcha(loginCode.getWidth(), loginCode.getHeight(), loginCode.getLength());
                    break;
                default:
                    throw new BadConfigurationException("验证码配置信息错误！正确配置查看 LoginCodeEnum ");
            }
        }
        if(StringUtils.isNotBlank(loginCode.getFontName())) {
            captcha.setFont(new Font(loginCode.getFontName(), Font.PLAIN, loginCode.getFontSize()));
        }
        return captcha;
    }

}
