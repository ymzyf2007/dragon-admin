package com.dragon.modules.system.service.impl;

import com.dragon.modules.common.utils.PageUtil;
import com.dragon.modules.common.utils.QueryHelper;
import com.dragon.modules.common.utils.RedisUtils;
import com.dragon.modules.common.utils.ValidationUtil;
import com.dragon.modules.system.domain.Dict;
import com.dragon.modules.system.domain.DictDetail;
import com.dragon.modules.system.dto.param.DictDetailQueryCriteria;
import com.dragon.modules.system.dto.result.DictDetailDto;
import com.dragon.modules.system.mapstruct.DictDetailMapper;
import com.dragon.modules.system.repository.DictDetailRepository;
import com.dragon.modules.system.repository.DictRepository;
import com.dragon.modules.system.service.DictDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@CacheConfig(cacheNames = "dict")
@Service("dictDetailService")
public class DictDetailServiceImpl implements DictDetailService {

    @Autowired
    private DictRepository dictRepository;
    @Autowired
    private DictDetailRepository dictDetailRepository;

    @Autowired
    private DictDetailMapper dictDetailMapper;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 分页查询字典详情
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

    /**
     * 根据字典名称获取字典详情列表
     *
     * @param dictName 字典名称
     * @return
     */
    @Cacheable(key = "'name:' + #p0")
    @Override
    public List<DictDetailDto> getDictDetailsByDictName(String dictName) {
        return dictDetailMapper.toDto(dictDetailRepository.findByDictName(dictName));
    }

    /**
     * 新增字典详情
     *
     * @param resources
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void create(DictDetail resources) {
        // 清理缓存
        delCaches(resources);
        dictDetailRepository.save(resources);
    }

    /**
     * 修改字典详情
     *
     * @param resources
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(DictDetail resources) {
        // 清理缓存
        delCaches(resources);

        DictDetail dictDetail = dictDetailRepository.findById(resources.getId()).orElseGet(DictDetail::new);
        ValidationUtil.isNull(dictDetail.getId(), "DictDetail", "id", resources.getId());
        resources.setId(dictDetail.getId());
        dictDetailRepository.save(resources);
    }

    /**
     * 删除字典详情
     *
     * @param id
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(Long id) {
        DictDetail dictDetail = dictDetailRepository.findById(id).orElseGet(DictDetail::new);
        // 清理缓存
        delCaches(dictDetail);
        dictDetailRepository.deleteById(id);
    }

    public void delCaches(DictDetail dictDetail) {
        Dict dict = dictRepository.findById(dictDetail.getDict().getId()).orElseGet(Dict::new);
        redisUtils.del("dict::name:" + dict.getName());
    }

}
