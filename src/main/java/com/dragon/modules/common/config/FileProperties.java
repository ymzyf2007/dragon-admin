package com.dragon.modules.common.config;

import com.dragon.modules.common.utils.DragonAdminConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "file")
public class FileProperties {

    /** 文件大小限制 */
    private Long maxSize;

    /** 头像大小限制 */
    private Long avatarMaxSize;

    private DragonPath mac;

    private DragonPath linux;

    private DragonPath windows;

    public DragonPath getPath(){
        String os = System.getProperty("os.name");
        if(os.toLowerCase().startsWith(DragonAdminConstant.WIN)) {
            return windows;
        } else if(os.toLowerCase().startsWith(DragonAdminConstant.MAC)){
            return mac;
        }
        return linux;
    }

    @Data
    public static class DragonPath {

        private String path;

        private String avatar;
    }

}