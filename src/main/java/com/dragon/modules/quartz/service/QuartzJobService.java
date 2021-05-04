package com.dragon.modules.quartz.service;

import com.dragon.modules.quartz.domain.QuartzJob;
import com.dragon.modules.quartz.domain.QuartzLog;
import com.dragon.modules.quartz.dto.param.JobQueryCriteria;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface QuartzJobService {

    /**
     * 分页查询定时任务
     *
     * @param criteria 条件
     * @param pageable 分页参数
     * @return
     */
    Object queryAll(JobQueryCriteria criteria, Pageable pageable);

    /**
     * 查询全部
     *
     * @param criteria 条件
     * @return
     */
    List<QuartzJob> queryAll(JobQueryCriteria criteria);

    /**
     * 新增定时任务
     *
     * @param resources
     */
    void create(QuartzJob resources);

    /**
     * 编辑定时任务
     *
     * @param resources
     */
    void update(QuartzJob resources);

    /**
     * 根据ID列表删除任务
     *
     * @param ids
     */
    void delete(Set<Long> ids);

    /**
     * 根据ID查询定时任务
     *
     * @param id ID
     * @return
     */
    QuartzJob findById(Long id);

    /**
     * 更改定时任务状态
     *
     * @param quartzJob
     */
    void updateIsPause(QuartzJob quartzJob);

    /**
     * 立即执行定时任务
     *
     * @param quartzJob
     */
    void execution(QuartzJob quartzJob);

    /**
     * 执行子任务
     *
     * @param tasks
     * @throws InterruptedException
     */
    void executionSubJob(String[] tasks) throws InterruptedException;

    /**
     * 导出定时任务
     *
     * @param queryAll 待导出的数据
     * @param response
     * @throws IOException
     */
    void download(List<QuartzJob> queryAll, HttpServletResponse response) throws IOException;

    /**************************任务执行日志**************************/
    /**
     * 分页查询任务日志
     *
     * @param criteria 条件
     * @param pageable 分页参数
     * @return
     */
    Object queryAllLog(JobQueryCriteria criteria, Pageable pageable);

    /**
     * 查询全部任务日志
     *
     * @param criteria 条件
     * @return
     */
    List<QuartzLog> queryAllLog(JobQueryCriteria criteria);

    /**
     * 导出任务日志
     * @param queryAllLog 待导出的数据
     * @param response
     * @throws IOException
     */
    void downloadLog(List<QuartzLog> queryAllLog, HttpServletResponse response) throws IOException;

}
