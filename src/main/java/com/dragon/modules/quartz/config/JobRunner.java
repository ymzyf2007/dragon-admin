package com.dragon.modules.quartz.config;

import com.dragon.modules.quartz.domain.QuartzJob;
import com.dragon.modules.quartz.repository.QuartzJobRepository;
import com.dragon.modules.quartz.utils.QuartzManage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 在容器启动的时候需要初始化一些内容。比如读取配置文件，数据库连接之类的。
 * SpringBoot给我们提供了两个接口来帮助我们实现这种需求。
 * 这两个接口分别为CommandLineRunner和ApplicationRunner。
 * 他们的执行时机为容器启动完成的时候
 */
@Component
@RequiredArgsConstructor
public class JobRunner implements ApplicationRunner {
    private static Logger log = LoggerFactory.getLogger(JobRunner.class);

    private final QuartzJobRepository quartzJobRepository;
    private final QuartzManage quartzManage;

    /**
     * 项目启动时重新激活启用的定时任务
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("--------------------注入定时任务---------------------");
        List<QuartzJob> quartzJobs = quartzJobRepository.findByIsPauseIsFalse();
        for (QuartzJob quartzJob : quartzJobs) {
            quartzManage.addJob(quartzJob);
        }
        log.info("--------------------定时任务注入完成---------------------");
    }

}
