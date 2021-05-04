package com.dragon.modules.system.service.impl;

import com.dragon.modules.common.utils.QueryHelper;
import com.dragon.modules.system.domain.Dict;
import com.dragon.modules.system.dto.param.DictQueryCriteria;
import com.dragon.modules.system.dto.result.DictDto;
import com.dragon.modules.system.mapstruct.DictMapper;
import com.dragon.modules.system.repository.DictRepository;
import com.dragon.modules.system.service.DictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@Service
@CacheConfig(cacheNames = "dict")
public class DictServiceImpl implements DictService {

    @Autowired
    private DictRepository dictRepository;

    @Autowired
    private DictMapper dictMapper;

    /**
     * 查询全部数据
     * @param param
     * @return
     */
    @Override
    public List<DictDto> queryAll(DictQueryCriteria param) {
        List<Dict> list = dictRepository.findAll(new Specification<Dict>() {
            @Override
            public Predicate toPredicate(Root<Dict> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return QueryHelper.getPredicate(root, param, cb);
            }
        });
        return dictMapper.toDto(list);
    }
}
