package com.dragon.modules.system.service;

import com.dragon.modules.system.dto.UserDto;

public interface UserService {

    /**
     * 根据用户名查询
     * @param userName
     * @return
     */
    UserDto findByName(String userName);

}
