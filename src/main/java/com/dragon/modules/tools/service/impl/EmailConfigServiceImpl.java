package com.dragon.modules.tools.service.impl;

import cn.hutool.extra.mail.Mail;
import cn.hutool.extra.mail.MailAccount;
import com.dragon.modules.common.exception.BadRequestException;
import com.dragon.modules.common.utils.EncryptUtils;
import com.dragon.modules.tools.domain.EmailConfig;
import com.dragon.modules.tools.domain.vo.EmailVo;
import com.dragon.modules.tools.repository.EmailConfigRepository;
import com.dragon.modules.tools.service.EmailConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service("emailConfigService")
@RequiredArgsConstructor
@CacheConfig(cacheNames = "email")
public class EmailConfigServiceImpl implements EmailConfigService {

    private final EmailConfigRepository emailConfigRepository;

    @Override
    public EmailConfig find() {
        Optional<EmailConfig> emailConfig = emailConfigRepository.findById(1L);
        return emailConfig.orElseGet(EmailConfig::new);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void send(EmailVo emailVo, EmailConfig emailConfig) {
        if(Objects.isNull(emailConfig.getId())) {
            throw new BadRequestException("请先配置，再操作");
        }
        // 封装
        MailAccount account = new MailAccount();
        account.setUser(emailConfig.getUser());
        account.setHost(emailConfig.getHost());
        account.setPort(Integer.parseInt(emailConfig.getPort()));
        account.setAuth(true);
        try {
            // 对称解密
            account.setPass(EncryptUtils.desDecrypt(emailConfig.getPass()));
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
        account.setFrom(emailConfig.getUser() + "<" + emailConfig.getFromUser() + ">");
        // ssl方式发送
        account.setSslEnable(true);
        // 使用STARTTLS安全连接
        account.setStarttlsEnable(true);
        String content = emailVo.getContent();
        // 发送
        try {
            int size = emailVo.getTos().size();
            Mail.create(account)
                    .setTos(emailVo.getTos().toArray(new String[size]))
                    .setTitle(emailVo.getSubject())
                    .setContent(content)
                    .setHtml(true)
                    // 关闭session
                    .setUseGlobalSession(false)
                    .send();
        } catch(Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

}