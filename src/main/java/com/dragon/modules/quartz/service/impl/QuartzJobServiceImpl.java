package com.dragon.modules.quartz.service.impl;

import cn.hutool.core.util.IdUtil;
import com.dragon.modules.common.exception.BadRequestException;
import com.dragon.modules.common.utils.*;
import com.dragon.modules.quartz.domain.QuartzJob;
import com.dragon.modules.quartz.domain.QuartzLog;
import com.dragon.modules.quartz.dto.param.JobQueryCriteria;
import com.dragon.modules.quartz.repository.QuartzJobRepository;
import com.dragon.modules.quartz.repository.QuartzLogRepository;
import com.dragon.modules.quartz.service.QuartzJobService;
import com.dragon.modules.quartz.utils.QuartzManage;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Service("quartzJobService")
public class QuartzJobServiceImpl implements QuartzJobService {

    @Autowired
    private QuartzJobRepository quartzJobRepository;
    @Autowired
    private QuartzLogRepository quartzLogRepository;

    @Autowired
    private QuartzManage quartzManage;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 分页查询定时任务
     *
     * @param criteria 条件
     * @param pageable 分页参数
     * @return
     */
    @Override
    public Object queryAll(JobQueryCriteria criteria, Pageable pageable) {
        return PageUtil.toPage(quartzJobRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelper.getPredicate(root, criteria, criteriaBuilder), pageable));
    }

    /**
     * 查询全部
     *
     * @param criteria 条件
     * @return
     */
    @Override
    public List<QuartzJob> queryAll(JobQueryCriteria criteria) {
        return quartzJobRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelper.getPredicate(root, criteria, criteriaBuilder));
    }

    /**
     * 新增定时任务
     *
     * @param resources
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void create(QuartzJob resources) {
        if (!CronExpression.isValidExpression(resources.getCronExpression())) {
            throw new BadRequestException("cron表达式格式错误");
        }
        resources = quartzJobRepository.save(resources);
        quartzManage.addJob(resources);
    }

    /**
     * 编辑定时任务
     *
     * @param resources
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(QuartzJob resources) {
        if (!CronExpression.isValidExpression(resources.getCronExpression())) {
            throw new BadRequestException("cron表达式格式错误");
        }
        if (StringUtils.isNotBlank(resources.getSubTask())) {
            List<String> tasks = Arrays.asList(resources.getSubTask().split("[,，]"));
            if (tasks.contains(resources.getId().toString())) {
                throw new BadRequestException("子任务中不能添加当前任务ID");
            }
        }
        resources = quartzJobRepository.save(resources);
        quartzManage.updateJobCron(resources);
    }

    /**
     * 根据ID列表删除任务
     *
     * @param ids
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(Set<Long> ids) {
        for (Long id : ids) {
            QuartzJob quartzJob = findById(id);
            quartzManage.deleteJob(quartzJob);
            quartzJobRepository.delete(quartzJob);
        }
    }

    /**
     * 根据ID查询定时任务
     *
     * @param id ID
     * @return
     */
    @Override
    public QuartzJob findById(Long id) {
        QuartzJob quartzJob = quartzJobRepository.findById(id).orElseGet(QuartzJob::new);
        ValidationUtil.isNull(quartzJob.getId(), "QuartzJob", "id", id);
        return quartzJob;
    }

    /**
     * 更改定时任务状态
     *
     * @param quartzJob
     */
    @Override
    public void updateIsPause(QuartzJob quartzJob) {
        if (quartzJob.getIsPause()) {
            quartzManage.resumeJob(quartzJob);
            quartzJob.setIsPause(false);
        } else {
            quartzManage.pauseJob(quartzJob);
            quartzJob.setIsPause(true);
        }
        quartzJobRepository.save(quartzJob);
    }

    /**
     * 立即执行定时任务
     *
     * @param quartzJob
     */
    @Override
    public void execution(QuartzJob quartzJob) {
        quartzManage.runJobNow(quartzJob);
    }

    /**
     * 执行子任务
     *
     * @param tasks
     * @throws InterruptedException
     */
    @Async
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void executionSubJob(String[] tasks) throws InterruptedException {
        for (String id : tasks) {
            QuartzJob quartzJob = findById(Long.parseLong(id));
            // 执行任务
            String uuid = IdUtil.simpleUUID();
            quartzJob.setUuid(uuid);
            // 执行任务
            execution(quartzJob);
            // 获取执行状态，如果执行失败则停止后面的子任务执行
            Boolean result = (Boolean) redisUtils.get(uuid);
            while (result == null) {
                // 休眠5秒，再次获取子任务执行情况
                Thread.sleep(5000);
                result = (Boolean) redisUtils.get(uuid);
            }
            if (!result) {
                redisUtils.del(uuid);
                break;
            }
        }
    }

    /**
     * 导出定时任务
     *
     * @param quartzJobs 待导出的数据
     * @param response
     * @throws IOException
     */
    @Override
    public void download(List<QuartzJob> quartzJobs, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (QuartzJob quartzJob : quartzJobs) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("任务名称", quartzJob.getJobName());
            map.put("Bean名称", quartzJob.getBeanName());
            map.put("执行方法", quartzJob.getMethodName());
            map.put("参数", quartzJob.getParams());
            map.put("表达式", quartzJob.getCronExpression());
            map.put("状态", quartzJob.getIsPause() ? "暂停中" : "运行中");
            map.put("描述", quartzJob.getDescription());
            map.put("创建日期", quartzJob.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    /**************************任务执行日志**************************/
    /**
     * 分页查询任务日志
     *
     * @param criteria 条件
     * @param pageable 分页参数
     * @return
     */
    @Override
    public Object queryAllLog(JobQueryCriteria criteria, Pageable pageable) {
        return PageUtil.toPage(quartzLogRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelper.getPredicate(root, criteria, criteriaBuilder), pageable));
    }

    /**
     * 查询全部任务日志
     *
     * @param criteria 条件
     * @return
     */
    @Override
    public List<QuartzLog> queryAllLog(JobQueryCriteria criteria) {
        return quartzLogRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelper.getPredicate(root, criteria, criteriaBuilder));
    }

    /**
     * 导出任务日志
     *
     * @param queryAllLog 待导出的数据
     * @param response
     * @throws IOException
     */
    @Override
    public void downloadLog(List<QuartzLog> queryAllLog, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (QuartzLog quartzLog : queryAllLog) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("任务名称", quartzLog.getJobName());
            map.put("Bean名称", quartzLog.getBeanName());
            map.put("执行方法", quartzLog.getMethodName());
            map.put("参数", quartzLog.getParams());
            map.put("表达式", quartzLog.getCronExpression());
            map.put("异常详情", quartzLog.getExceptionDetail());
            map.put("耗时/毫秒", quartzLog.getTime());
            map.put("状态", quartzLog.getIsSuccess() ? "成功" : "失败");
            map.put("创建日期", quartzLog.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

}
