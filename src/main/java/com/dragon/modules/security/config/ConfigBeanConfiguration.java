package com.dragon.modules.security.config;

import com.dragon.modules.security.config.bean.SecurityProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置文件转换Pojo类的统一配置类
 */
@Configuration
public class ConfigBeanConfiguration {

//    @Bean
//    @ConfigurationProperties(prefix = "login", ignoreUnknownFields = true)
//    public LoginProperties loginProperties() {
//        return new LoginProperties();
//    }

    @Bean
    @ConfigurationProperties(prefix = "jwt", ignoreUnknownFields = true)
    public SecurityProperties securityProperties() {
        return new SecurityProperties();
    }

}
