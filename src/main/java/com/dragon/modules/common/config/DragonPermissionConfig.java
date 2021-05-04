package com.dragon.modules.common.config;

import cn.hutool.core.collection.CollectionUtil;
import com.dragon.modules.common.utils.SecurityUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service(value = "dragon")
public class DragonPermissionConfig {

    public boolean check(String... permissions) {
        // 获取当前用户的所有权限
//        List<String> perms = SecurityUtils.getCurrentUser().getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority).collect(Collectors.toList());
//        // 判断当前用户的所有权限是否包含接口上定义的权限
//        return perms.contains("admin") || Arrays.stream(permissions).anyMatch(perms::contains);
        List<String> perms = new ArrayList<>();
        if(CollectionUtil.isNotEmpty(SecurityUtils.getCurrentUser().getAuthorities())) {
            for (GrantedAuthority grantedAuthority : SecurityUtils.getCurrentUser().getAuthorities()) {
                String authority = grantedAuthority.getAuthority();
                perms.add(authority);
            }
        }
        // 判断当前用户的所有权限是否包含接口上定义的权限
        return perms.contains("admin") || Arrays.stream(permissions).anyMatch(perms::contains);
    }

}
