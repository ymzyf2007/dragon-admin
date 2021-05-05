package com.dragon.modules.system.repository;

import com.dragon.modules.system.domain.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Set;

public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {

    /**
     * 根据名称查询岗位信息
     *
     * @param name 名称
     * @return
     */
    Job findByName(String name);

    /**
     * 根据Id列表删除岗位信息
     *
     * @param ids
     */
    void deleteAllByIdIn(Set<Long> ids);

}
