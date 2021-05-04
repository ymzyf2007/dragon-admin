package com.dragon.modules.security.service;

import com.dragon.modules.common.exception.BadRequestException;
import com.dragon.modules.common.exception.EntityNotFoundException;
import com.dragon.modules.security.config.bean.LoginProperties;
import com.dragon.modules.security.dto.JwtUserDto;
import com.dragon.modules.system.dto.UserDto;
import com.dragon.modules.system.service.DataService;
import com.dragon.modules.system.service.RoleService;
import com.dragon.modules.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private DataService dataService;
    @Autowired
    private LoginProperties loginProperties;
    /**
     * 用户信息缓存
     */
    public static Map<String, JwtUserDto> USER_CACHE = new ConcurrentHashMap<>();

    @Override
    public JwtUserDto loadUserByUsername(String username) throws UsernameNotFoundException {
        boolean searchDb = true;
        JwtUserDto jwtUserDto = null;
        if (loginProperties.isCacheEnable() && USER_CACHE.containsKey(username)) {
            jwtUserDto = USER_CACHE.get(username);
            searchDb = false;
        }
        if (searchDb) {
            UserDto user;
            try {
                user = userService.findByName(username);
            } catch (EntityNotFoundException e) {
                // SpringSecurity会自动转换UsernameNotFoundException为BadCredentialsException
                throw new UsernameNotFoundException("", e);
            }
            if (Objects.isNull(user)) {
                throw new UsernameNotFoundException("");
            } else {
                if (!user.getEnabled()) {
                    throw new BadRequestException("账号未激活！");
                }
                jwtUserDto = new JwtUserDto(
                        user,
                        dataService.getDeptIds(user),
                        roleService.mapToGrantedAuthorities(user)
                );
                USER_CACHE.put(username, jwtUserDto);
            }
        }
        return jwtUserDto;
    }

}
