package com.dragon.modules.system.service.impl;

import com.dragon.modules.common.exception.EntityNotFoundException;
import com.dragon.modules.common.utils.PageUtil;
import com.dragon.modules.common.utils.QueryHelper;
import com.dragon.modules.system.domain.User;
import com.dragon.modules.system.dto.UserDto;
import com.dragon.modules.system.dto.param.UserQueryCriteria;
import com.dragon.modules.system.mapstruct.UserMapper;
import com.dragon.modules.system.repository.UserRepository;
import com.dragon.modules.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@CacheConfig(cacheNames = "user")
@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;

    /**
     * 根据用户名查询
     *
     * @param userName
     * @return
     */
    @Override
    public UserDto findByName(String userName) {
        User user = userRepository.findByUsername(userName);
        if (Objects.isNull(user)) {
            throw new EntityNotFoundException(User.class, "name", userName);
        } else {
            return userMapper.toDto(user);
        }
    }

    /**
     * 分页查询
     *
     * @param criteria 条件
     * @param pageable 分页参数
     * @return
     */
    @Override
    public Object queryAll(UserQueryCriteria criteria, Pageable pageable) {
        Page<User> page = userRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelper.getPredicate(root, criteria, criteriaBuilder), pageable);
        return PageUtil.toPage(page.map(userMapper::toDto));
    }

    /**
     * 查询全部
     *
     * @param criteria 条件
     * @return
     */
    @Override
    public List<UserDto> queryAll(UserQueryCriteria criteria) {
        List<User> users = userRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelper.getPredicate(root, criteria, criteriaBuilder));
        return userMapper.toDto(users);
    }
}
