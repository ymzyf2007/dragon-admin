package com.dragon.modules.tools.service;

import com.dragon.modules.tools.domain.EmailConfig;
import com.dragon.modules.tools.domain.vo.EmailVo;

public interface EmailConfigService {

    /**
     * 查询配置
     * @return EmailConfig 邮件配置
     */
    EmailConfig find();

    /**
     * 发送邮件
     * @param emailVo 邮件发送的内容
     * @param emailConfig 邮件配置
     */
    void send(EmailVo emailVo, EmailConfig emailConfig);

}
