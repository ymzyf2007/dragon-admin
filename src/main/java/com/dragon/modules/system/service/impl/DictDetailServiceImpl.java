package com.dragon.modules.system.service.impl;

import com.dragon.modules.common.utils.PageUtil;
import com.dragon.modules.common.utils.QueryHelper;
import com.dragon.modules.system.domain.DictDetail;
import com.dragon.modules.system.dto.param.DictDetailQueryCriteria;
import com.dragon.modules.system.mapstruct.DictDetailMapper;
import com.dragon.modules.system.repository.DictDetailRepository;
import com.dragon.modules.system.service.DictDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;

@CacheConfig(cacheNames = "dict")
@Service("dictDetailService")
public class DictDetailServiceImpl implements DictDetailService {

    @Autowired
    private DictDetailRepository dictDetailRepository;

    @Autowired
    private DictDetailMapper dictDetailMapper;

    /**
     * 分页查询
     *
     * @param criteria 条件
     * @param pageable 分页参数
     * @return
     */
    @Override
    public Map<String, Object> queryAll(DictDetailQueryCriteria criteria, Pageable pageable) {
        Page<DictDetail> page = dictDetailRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelper.getPredicate(root, criteria, criteriaBuilder), pageable);
        return PageUtil.toPage(page.map(dictDetailMapper::toDto));
    }
}
