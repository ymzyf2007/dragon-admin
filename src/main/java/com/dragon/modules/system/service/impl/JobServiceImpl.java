package com.dragon.modules.system.service.impl;

import com.dragon.modules.common.exception.BadRequestException;
import com.dragon.modules.common.exception.EntityExistException;
import com.dragon.modules.common.utils.*;
import com.dragon.modules.system.domain.Job;
import com.dragon.modules.system.dto.param.JobQueryCriteria;
import com.dragon.modules.system.dto.result.JobDto;
import com.dragon.modules.system.mapstruct.JobMapper;
import com.dragon.modules.system.repository.JobRepository;
import com.dragon.modules.system.repository.UserRepository;
import com.dragon.modules.system.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@CacheConfig(cacheNames = "job")
@Service("jobService")
public class JobServiceImpl implements JobService {

    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private JobMapper jobMapper;

    /**
     * 根据岗位ID查询岗位信息
     *
     * @param id 岗位ID
     * @return
     */
    @Cacheable(key = "'id:' + #p0")
    @Override
    public JobDto findById(Long id) {
        Job job = jobRepository.findById(id).orElseGet(Job::new);
        ValidationUtil.isNull(job.getId(), "Job", "id", id);
        return jobMapper.toDto(job);
    }

    /**
     * 根据条件分页查询岗位信息
     *
     * @param criteria 条件
     * @param pageable 分页参数
     * @return
     */
    @Override
    public Map<String, Object> queryAll(JobQueryCriteria criteria, Pageable pageable) {
        Page<Job> page = jobRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelper.getPredicate(root, criteria, criteriaBuilder), pageable);
        return PageUtil.toPage(page.map(jobMapper::toDto).getContent(), page.getTotalElements());
    }

    /**
     * 根据条件查询全部数据
     *
     * @param criteria
     * @return
     */
    @Override
    public List<JobDto> queryAll(JobQueryCriteria criteria) {
        List<Job> list = jobRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelper.getPredicate(root, criteria, criteriaBuilder));
        return jobMapper.toDto(list);
    }

    /**
     * 新增岗位信息
     *
     * @param resources
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void create(Job resources) {
        Job job = jobRepository.findByName(resources.getName());
        if (job != null) {
            throw new EntityExistException(Job.class, "name", resources.getName());
        }
        jobRepository.save(resources);
    }

    /**
     * 修改岗位信息
     *
     * @param resources
     */
    @CacheEvict(key = "'id:' + #p0.id")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(Job resources) {
        Job job = jobRepository.findById(resources.getId()).orElseGet(Job::new);
        Job old = jobRepository.findByName(resources.getName());
        if (old != null && !old.getId().equals(resources.getId())) {
            throw new EntityExistException(Job.class, "name", resources.getName());
        }
        ValidationUtil.isNull(job.getId(), "Job", "id", resources.getId());
        resources.setId(job.getId());
        jobRepository.save(resources);
    }

    /**
     * 验证岗位信息是否被用户关联
     *
     * @param ids 岗位ID列表
     */
    @Override
    public void verification(Set<Long> ids) {
        if (userRepository.countByJobs(ids) > 0) {
            throw new BadRequestException("所选的岗位中存在用户关联，请解除关联再试！");
        }
    }

    /**
     * 根据ID列表删除岗位信息
     *
     * @param ids
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(Set<Long> ids) {
        // 删除缓存
        redisUtils.delByKeys("job::id:", ids);
        jobRepository.deleteAllByIdIn(ids);
    }

    /**
     * 导出数据
     *
     * @param jobDtos  待导出的数据
     * @param response
     * @throws IOException
     */
    @Override
    public void download(List<JobDto> jobDtos, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (JobDto jobDTO : jobDtos) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("岗位名称", jobDTO.getName());
            map.put("岗位状态", jobDTO.getEnabled() ? "启用" : "停用");
            map.put("创建日期", jobDTO.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

}
