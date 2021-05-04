package com.dragon.modules.system.mapstruct;

import com.dragon.modules.common.base.BaseMapper;
import com.dragon.modules.system.domain.DictDetail;
import com.dragon.modules.system.dto.result.DictDetailDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {DictSmallMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DictDetailMapper extends BaseMapper<DictDetailDto, DictDetail> {

}
