package com.dragon.modules.system.service;

import com.dragon.modules.system.domain.DictDetail;
import com.dragon.modules.system.dto.param.DictDetailQueryCriteria;
import com.dragon.modules.system.dto.result.DictDetailDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface DictDetailService {

    /**
     * 分页查询字典详情
     *
     * @param criteria 条件
     * @param pageable 分页参数
     * @return
     */
    Map<String, Object> queryAll(DictDetailQueryCriteria criteria, Pageable pageable);

    /**
     * 根据字典名称获取字典详情列表
     *
     * @param dictName 字典名称
     * @return
     */
    List<DictDetailDto> getDictDetailsByDictName(String dictName);

    /**
     * 新增字典详情
     *
     * @param resources
     */
    void create(DictDetail resources);

    /**
     * 修改字典详情
     *
     * @param resources
     */
    void update(DictDetail resources);

    /**
     * 删除字典详情
     *
     * @param id
     */
    void delete(Long id);

}
