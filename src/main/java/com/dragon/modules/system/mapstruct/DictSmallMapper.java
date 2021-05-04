package com.dragon.modules.system.mapstruct;

import com.dragon.modules.common.base.BaseMapper;
import com.dragon.modules.system.domain.Dict;
import com.dragon.modules.system.dto.result.DictSmallDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DictSmallMapper extends BaseMapper<DictSmallDto, Dict> {
}
