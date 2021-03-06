package com.dragon.modules.quartz.repository;

import com.dragon.modules.quartz.domain.QuartzLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface QuartzLogRepository extends JpaRepository<QuartzLog, Long>, JpaSpecificationExecutor<QuartzLog> {
}