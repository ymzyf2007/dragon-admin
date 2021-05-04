package com.dragon.modules.system.service;

import com.dragon.modules.system.dto.UserDto;

import java.util.List;

public interface DataService {

    /**
     * 获取数据权限
     * @param user
     * @return
     */
    List<Long> getDeptIds(UserDto user);

}
