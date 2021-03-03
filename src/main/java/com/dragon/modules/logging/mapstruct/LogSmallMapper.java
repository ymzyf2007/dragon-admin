package com.dragon.modules.logging.mapstruct;

import com.dragon.modules.common.base.BaseMapper;
import com.dragon.modules.logging.domain.Log;
import com.dragon.modules.logging.dto.result.LogSmallDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * componentModel = "spring"：生成的实现类上面会自动添加一个@Component注解
 * unmappedTargetPolicy = ReportingPolicy.IGNORE：自动忽略未映射的属性
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LogSmallMapper extends BaseMapper<LogSmallDTO, Log> {
}
