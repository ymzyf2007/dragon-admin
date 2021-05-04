package com.dragon.modules.system.service;

import com.dragon.modules.system.dto.UserDto;
import com.dragon.modules.system.dto.param.UserQueryCriteria;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    /**
     * 根据用户名查询
     * @param userName
     * @return
     */
    UserDto findByName(String userName);

    /**
     * 分页查询
     * @param criteria 条件
     * @param pageable 分页参数
     * @return
     */
    Object queryAll(UserQueryCriteria criteria, Pageable pageable);

    /**
     * 查询全部
     * @param criteria 条件
     * @return
     */
    List<UserDto> queryAll(UserQueryCriteria criteria);

}
