package com.dragon.modules.system.service;

import com.dragon.modules.system.dto.param.DictQueryCriteria;
import com.dragon.modules.system.dto.result.DictDto;

import java.util.List;

public interface DictService {

    /**
     * 查询全部数据
     * @param param
     * @return
     */
    List<DictDto> queryAll(DictQueryCriteria param);

}
