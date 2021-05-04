package com.dragon.modules.system.service;

import com.dragon.modules.system.dto.result.MenuDto;
import com.dragon.modules.system.vo.MenuVo;

import java.util.List;

public interface MenuService {

    /**
     * 根据当前用户获取菜单
     * @param currentUserId
     * @return
     */
    List<MenuDto> findByUser(Long currentUserId);

    /**
     * 构建菜单树
     * @param menuDtos 原始数据
     * @return
     */
    List<MenuDto> buildTree(List<MenuDto> menuDtos);

    /**
     * 构建菜单树
     * @param menuDtos
     * @return
     */
    List<MenuVo> buildMenus(List<MenuDto> menuDtos);

}
