package com.dragon.modules.generator.repository;

import com.dragon.modules.generator.domain.GenConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenConfigRepository extends JpaRepository<GenConfig, Long> {

    /**
     * 查询表配置
     *
     * @param tableName 表名
     * @return
     */
    GenConfig findByTableName(String tableName);

}
