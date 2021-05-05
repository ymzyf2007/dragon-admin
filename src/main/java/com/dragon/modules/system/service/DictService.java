package com.dragon.modules.system.service;

import com.dragon.modules.system.domain.Dict;
import com.dragon.modules.system.dto.param.DictQueryCriteria;
import com.dragon.modules.system.dto.result.DictDto;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DictService {

    /**
     * 根据条件分页查询字典数据
     *
     * @param criteria 条件
     * @param pageable 分页参数
     * @return
     */
    Map<String, Object> queryAll(DictQueryCriteria criteria, Pageable pageable);

    /**
     * 根据条件查询全部字典数据
     *
     * @param dict
     * @return
     */
    List<DictDto> queryAll(DictQueryCriteria dict);

    /**
     * 新增数据字典
     *
     * @param resources
     * @return
     */
    void create(Dict resources);

    /**
     * 编辑数据字典
     *
     * @param resources
     */
    void update(Dict resources);

    /**
     * 删除数据字典
     *
     * @param ids
     */
    void delete(Set<Long> ids);

    /**
     * 导出数据
     *
     * @param dictDtos 待导出的数据
     * @param response
     * @throws IOException
     */
    void download(List<DictDto> dictDtos, HttpServletResponse response) throws IOException;

}
