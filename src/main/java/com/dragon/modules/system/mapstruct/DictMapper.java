package com.dragon.modules.system.mapstruct;

import com.dragon.modules.common.base.BaseMapper;
import com.dragon.modules.system.domain.Dict;
import com.dragon.modules.system.dto.result.DictDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * componentModel = "spring"：生成的实现类上面会自动添加一个@Component注解
 * unmappedTargetPolicy = ReportingPolicy.IGNORE：自动忽略未映射的属性
 */
@Mapper(componentModel = "spring", uses = {}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DictMapper extends BaseMapper<DictDto, Dict> {
}
