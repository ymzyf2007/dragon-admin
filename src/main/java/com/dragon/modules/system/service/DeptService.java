package com.dragon.modules.system.service;

import com.dragon.modules.system.domain.Dept;
import com.dragon.modules.system.dto.param.DeptQueryCriteria;
import com.dragon.modules.system.dto.result.DeptDto;

import java.util.List;
import java.util.Set;

public interface DeptService {

    /**
     * 查询所有数据
     * @param criteria 条件
     * @param isQuery
     * @throws Exception
     * @return
     */
    List<DeptDto> queryAll(DeptQueryCriteria criteria, Boolean isQuery) throws Exception;

    /**
     * 根据PID查询
     * @param pid
     * @return
     */
    List<Dept> findByPid(long pid);

    /**
     * 根据角色ID查询
     * @param id
     * @return
     */
    Set<Dept> findByRoleId(Long id);

    /**
     * 获取
     * @param deptList
     * @return
     */
    List<Long> getDeptChildren(List<Dept> deptList);

}
