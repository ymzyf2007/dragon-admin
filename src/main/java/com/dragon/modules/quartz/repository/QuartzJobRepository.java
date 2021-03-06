package com.dragon.modules.quartz.repository;

import com.dragon.modules.quartz.domain.QuartzJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface QuartzJobRepository extends JpaRepository<QuartzJob, Long>, JpaSpecificationExecutor<QuartzJob> {

    /**
     * 查询状态为已启用的任务
     * @return
     */
    List<QuartzJob> findByIsPauseIsFalse();

}
