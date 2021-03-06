package com.dragon.modules.quartz.utils;

import com.dragon.modules.common.exception.BadRequestException;
import com.dragon.modules.quartz.domain.QuartzJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class QuartzManage {

    private static final String JOB_NAME_PREFIX = "TASK_";

    @Autowired
    private Scheduler scheduler;

    /**
     * 添加定时任务Job
     * @param quartzJob
     */
    public void addJob(QuartzJob quartzJob) {
        try {
            // 构建job信息
            JobDetail jobDetail = JobBuilder.newJob(ExecutionJob.class).withIdentity(JOB_NAME_PREFIX + quartzJob.getId()).build();
            // 通过触发器名和cron表达式创建Trigger
            Trigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(JOB_NAME_PREFIX + quartzJob.getId())
                    .startNow().withSchedule(CronScheduleBuilder.cronSchedule(quartzJob.getCronExpression())).build();
            // 设置数据到JobData中，执行的时候取出
            cronTrigger.getJobDataMap().put(QuartzJob.JOB_KEY, quartzJob);
            // 重置启动时间
            ((CronTriggerImpl) cronTrigger).setStartTime(new Date());

            // 执行定时任务
            scheduler.scheduleJob(jobDetail, cronTrigger);

            // 暂停任务
            if (quartzJob.getIsPause()) {
                pauseJob(quartzJob);
            }
        } catch (SchedulerException e) {
            log.error("创建定时任务失败", e);
            throw new BadRequestException("创建定时任务失败");
        }
    }

    /**
     * 暂停一个job
     * @param quartzJob /
     */
    public void pauseJob(QuartzJob quartzJob) {
        try {
            JobKey jobKey = JobKey.jobKey(JOB_NAME_PREFIX + quartzJob.getId());
            scheduler.pauseJob(jobKey);
        } catch (Exception e) {
            log.error("定时任务暂停失败", e);
            throw new BadRequestException("定时任务暂停失败");
        }
    }

}