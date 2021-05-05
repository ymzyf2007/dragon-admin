package com.dragon.modules.system.service;

import com.dragon.modules.system.domain.Job;
import com.dragon.modules.system.dto.param.JobQueryCriteria;
import com.dragon.modules.system.dto.result.JobDto;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface JobService {

    /**
     * 根据岗位ID查询岗位信息
     *
     * @param id 岗位ID
     * @return
     */
    JobDto findById(Long id);

    /**
     * 根据条件分页查询岗位信息
     *
     * @param criteria 条件
     * @param pageable 分页参数
     * @return
     */
    Map<String, Object> queryAll(JobQueryCriteria criteria, Pageable pageable);

    /**
     * 根据条件查询全部数据
     *
     * @param criteria
     * @return
     */
    List<JobDto> queryAll(JobQueryCriteria criteria);

    /**
     * 新增岗位信息
     *
     * @param resources
     * @return
     */
    void create(Job resources);

    /**
     * 修改岗位信息
     *
     * @param resources
     */
    void update(Job resources);

    /**
     * 验证岗位信息是否被用户关联
     *
     * @param ids 岗位ID列表
     */
    void verification(Set<Long> ids);

    /**
     * 根据ID列表删除岗位信息
     *
     * @param ids
     */
    void delete(Set<Long> ids);

    /**
     * 导出数据
     *
     * @param jobDtos  待导出的数据
     * @param response
     * @throws IOException
     */
    void download(List<JobDto> jobDtos, HttpServletResponse response) throws IOException;
}
