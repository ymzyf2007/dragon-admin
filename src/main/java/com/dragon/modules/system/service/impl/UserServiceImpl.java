package com.dragon.modules.system.service.impl;

import com.dragon.modules.common.exception.EntityNotFoundException;
import com.dragon.modules.system.domain.User;
import com.dragon.modules.system.dto.UserDto;
import com.dragon.modules.system.mapstruct.UserMapper;
import com.dragon.modules.system.repository.UserRepository;
import com.dragon.modules.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.Objects;

@CacheConfig(cacheNames = "user")
@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDto findByName(String userName) {
        User user = userRepository.findByUsername(userName);
        if (Objects.isNull(user)) {
            throw new EntityNotFoundException(User.class, "name", userName);
        } else {
            return userMapper.toDto(user);
        }
    }
}
