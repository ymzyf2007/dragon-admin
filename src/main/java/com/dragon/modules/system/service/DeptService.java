package com.dragon.modules.system.service;

import com.dragon.modules.system.domain.Dept;
import com.dragon.modules.system.dto.param.DeptQueryCriteria;
import com.dragon.modules.system.dto.result.DeptDto;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface DeptService {

    /**
     * 根据部门ID查询
     *
     * @param id
     * @return
     */
    DeptDto findById(Long id);

    /**
     * 查询所有数据
     *
     * @param criteria 条件
     * @param isQuery
     * @return
     * @throws Exception
     */
    List<DeptDto> queryAll(DeptQueryCriteria criteria, Boolean isQuery) throws Exception;

    /**
     * 新增部门信息
     *
     * @param resources /
     */
    void create(Dept resources);

    /**
     * 修改部门信息
     *
     * @param resources /
     */
    void update(Dept resources);

    /**
     * 删除部门信息
     *
     * @param deptDtos /
     */
    void delete(Set<DeptDto> deptDtos);

    /**
     * 获取待删除的部门
     *
     * @param deptList
     * @param deptDtos
     * @return
     */
    Set<DeptDto> getDeleteDepts(List<Dept> deptList, Set<DeptDto> deptDtos);

    /**
     * 根据PID查询
     *
     * @param pid
     * @return
     */
    List<Dept> findByPid(long pid);

    /**
     * 根据角色ID查询
     *
     * @param id
     * @return
     */
    Set<Dept> findByRoleId(Long id);

    /**
     * 获取
     *
     * @param deptList
     * @return
     */
    List<Long> getDeptChildren(List<Dept> deptList);

    /**
     * 验证是否被角色或用户关联
     *
     * @param deptDtos
     */
    void verification(Set<DeptDto> deptDtos);

    /**
     * 根据ID获取同级与上级数据
     *
     * @param deptDto
     * @param depts
     * @return
     */
    List<DeptDto> getSuperior(DeptDto deptDto, List<Dept> depts);

    /**
     * 构建树形数据
     *
     * @param deptDtos
     * @return
     */
    Object buildTree(List<DeptDto> deptDtos);

    /**
     * 导出数据
     *
     * @param deptDtos 待导出的数据
     * @param response
     * @throws IOException
     */
    void download(List<DeptDto> deptDtos, HttpServletResponse response) throws IOException;

}
