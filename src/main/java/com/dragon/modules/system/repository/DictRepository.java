package com.dragon.modules.system.repository;

import com.dragon.modules.system.domain.Dict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Set;

public interface DictRepository extends JpaRepository<Dict, Long>, JpaSpecificationExecutor<Dict> {

    /**
     * 根据ID列表查询数据字典
     *
     * @param ids
     * @return
     */
    List<Dict> findByIdIn(Set<Long> ids);

    /**
     * 根据ID列表删除数据字典
     *
     * @param ids
     */
    void deleteByIdIn(Set<Long> ids);


}
