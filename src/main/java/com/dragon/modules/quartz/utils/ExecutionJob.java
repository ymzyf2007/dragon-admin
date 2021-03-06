package com.dragon.modules.quartz.utils;

import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import com.dragon.config.thread.ThreadPoolExecutorUtil;
import com.dragon.modules.common.utils.RedisUtils;
import com.dragon.modules.common.utils.SpringContextHolder;
import com.dragon.modules.common.utils.StringUtils;
import com.dragon.modules.common.utils.ThrowableUtil;
import com.dragon.modules.quartz.domain.QuartzJob;
import com.dragon.modules.quartz.domain.QuartzLog;
import com.dragon.modules.quartz.repository.QuartzLogRepository;
import com.dragon.modules.tools.domain.vo.EmailVo;
import com.dragon.modules.tools.service.EmailConfigService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Async
public class ExecutionJob extends QuartzJobBean {

    private final static ThreadPoolExecutor EXECUTOR = ThreadPoolExecutorUtil.getThreadPool();

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        QuartzJob quartzJob = (QuartzJob) context.getMergedJobDataMap().get(QuartzJob.JOB_KEY);
        // 获取spring bean
        QuartzLogRepository quartzLogRepository = SpringContextHolder.getBean(QuartzLogRepository.class);
        RedisUtils redisUtils = SpringContextHolder.getBean(RedisUtils.class);
        String uuid = quartzJob.getUuid();

        QuartzLog quartzLog = new QuartzLog();
        quartzLog.setJobName(quartzJob.getJobName());
        quartzLog.setBeanName(quartzJob.getBeanName());
        quartzLog.setMethodName(quartzJob.getMethodName());
        quartzLog.setParams(quartzJob.getParams());
        quartzLog.setCronExpression(quartzJob.getCronExpression());
        long startTime = System.currentTimeMillis();
        try {
            log.info("任务开始执行，任务名称：{}", quartzJob.getJobName());
            ConcreteQuartzTaskRunnable task = new ConcreteQuartzTaskRunnable(quartzJob.getBeanName(), quartzJob.getMethodName(), quartzJob.getParams());
            Future<?> future = EXECUTOR.submit(task);
            future.get();
            long durationTime = System.currentTimeMillis() - startTime;
            quartzLog.setTime(durationTime);
            // 任务状态
            quartzLog.setIsSuccess(true);
            log.info("任务执行完毕，任务名称：{}，执行时间：{}毫秒", quartzJob.getJobName(), durationTime);
            if(StringUtils.isNotBlank(uuid)) {
                redisUtils.set(uuid, true);
            }
            // 判断是否存在子任务
            if(StringUtils.isNotEmpty(quartzJob.getSubTask())) {
                String[] tasks = quartzJob.getSubTask().split("[,，]");
//                // 执行子任务
//                quartzJobService.executionSubJob(tasks);
            }
        } catch (Exception e) {
            long durationTime = System.currentTimeMillis() - startTime;
            quartzLog.setTime(durationTime);
            // 任务状态
            quartzLog.setIsSuccess(false);
            quartzLog.setExceptionDetail(ThrowableUtil.getStackTrace(e));
            // 任务如果失败了则暂停
            if(Objects.nonNull(quartzJob.getPauseAfterFailure()) && quartzJob.getPauseAfterFailure()) {
                quartzJob.setIsPause(false);
                // 更新状态
//                quartzJobService.updateIsPause(quartzJob);
            }
            if(Objects.nonNull(quartzJob.getEmail())) {
                EmailConfigService emailConfigService = SpringContextHolder.getBean(EmailConfigService.class);
                // 邮箱报警
                EmailVo emailVo = taskAlarm(quartzJob, ThrowableUtil.getStackTrace(e));
                emailConfigService.send(emailVo, emailConfigService.find());
            }
            if(StringUtils.isNotBlank(uuid)) {
                redisUtils.set(uuid, false);
            }
            log.error("任务执行失败，任务名称：{}，执行时间：{}毫秒", quartzJob.getJobName(), durationTime, e);
        } finally {
            quartzLogRepository.save(quartzLog);
        }
    }

    private EmailVo taskAlarm(QuartzJob quartzJob, String msg) {
        EmailVo emailVo = new EmailVo();
        emailVo.setSubject("定时任务【"+ quartzJob.getJobName() +"】执行失败，请尽快处理！");
        Map<String, Object> data = new HashMap<>(16);
        data.put("task", quartzJob);
        data.put("msg", msg);
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("template", TemplateConfig.ResourceMode.CLASSPATH));
        Template template = engine.getTemplate("email/taskAlarm.ftl");
        emailVo.setContent(template.render(data));
        List<String> emails = Arrays.asList(quartzJob.getEmail().split("[,，]"));
        emailVo.setTos(emails);
        return emailVo;
    }
}
