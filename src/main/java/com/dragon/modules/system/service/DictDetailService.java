package com.dragon.modules.system.service;

import com.dragon.modules.system.dto.param.DictDetailQueryCriteria;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface DictDetailService {

    /**
     * 分页查询
     *
     * @param criteria 条件
     * @param pageable 分页参数
     * @return
     */
    Map<String, Object> queryAll(DictDetailQueryCriteria criteria, Pageable pageable);

}
