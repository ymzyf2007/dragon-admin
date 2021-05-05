package com.dragon.modules.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.dragon.modules.common.utils.*;
import com.dragon.modules.system.domain.Dict;
import com.dragon.modules.system.dto.param.DictQueryCriteria;
import com.dragon.modules.system.dto.result.DictDetailDto;
import com.dragon.modules.system.dto.result.DictDto;
import com.dragon.modules.system.mapstruct.DictMapper;
import com.dragon.modules.system.repository.DictRepository;
import com.dragon.modules.system.service.DictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Service
@CacheConfig(cacheNames = "dict")
public class DictServiceImpl implements DictService {

    @Autowired
    private DictRepository dictRepository;

    @Autowired
    private DictMapper dictMapper;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 根据条件分页查询字典数据
     *
     * @param param    条件
     * @param pageable 分页参数
     * @return
     */
    @Override
    public Map<String, Object> queryAll(DictQueryCriteria param, Pageable pageable) {
        Page<Dict> page = dictRepository.findAll((root, query, cb) -> QueryHelper.getPredicate(root, param, cb), pageable);
        return PageUtil.toPage(page.map(dictMapper::toDto));
    }

    /**
     * 根据条件查询全部字典数据
     *
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

    /**
     * 新增数据字典
     *
     * @param resources
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void create(Dict resources) {
        dictRepository.save(resources);
    }

    /**
     * 编辑数据字典
     *
     * @param resources
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(Dict resources) {
        // 清理缓存
        delCaches(resources);
        Dict dict = dictRepository.findById(resources.getId()).orElseGet(Dict::new);
        ValidationUtil.isNull(dict.getId(), "Dict", "id", resources.getId());
        resources.setId(dict.getId());
        dictRepository.save(resources);
    }

    /**
     * 删除数据字典
     *
     * @param ids
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(Set<Long> ids) {
        // 清理缓存
        List<Dict> dicts = dictRepository.findByIdIn(ids);
        for (Dict dict : dicts) {
            delCaches(dict);
        }
        dictRepository.deleteByIdIn(ids);
    }

    /**
     * 导出数据
     *
     * @param queryAll 待导出的数据
     * @param response
     * @throws IOException
     */
    @Override
    public void download(List<DictDto> dictDtos, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DictDto dictDTO : dictDtos) {
            if (CollectionUtil.isNotEmpty(dictDTO.getDictDetails())) {
                for (DictDetailDto dictDetail : dictDTO.getDictDetails()) {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("字典名称", dictDTO.getName());
                    map.put("字典描述", dictDTO.getDescription());
                    map.put("字典标签", dictDetail.getLabel());
                    map.put("字典值", dictDetail.getValue());
                    map.put("创建日期", dictDetail.getCreateTime());
                    list.add(map);
                }
            } else {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("字典名称", dictDTO.getName());
                map.put("字典描述", dictDTO.getDescription());
                map.put("字典标签", null);
                map.put("字典值", null);
                map.put("创建日期", dictDTO.getCreateTime());
                list.add(map);
            }
        }
        FileUtil.downloadExcel(list, response);
    }

    public void delCaches(Dict dict) {
        redisUtils.del("dict::name:" + dict.getName());
    }

}
