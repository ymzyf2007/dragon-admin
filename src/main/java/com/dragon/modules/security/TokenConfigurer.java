package com.dragon.modules.security;

import com.dragon.modules.security.config.bean.SecurityProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class TokenConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final TokenProvider tokenProvider;
    private final SecurityProperties properties;
//    private final OnlineUserService onlineUserService;
//    private final UserCacheClean userCacheClean;

    @Override
    public void configure(HttpSecurity http) {
        TokenFilter customFilter = new TokenFilter(tokenProvider, properties/*, onlineUserService, userCacheClean*/);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }

}