package com.dragon.modules.system.mapstruct;

import com.dragon.modules.common.base.BaseMapper;
import com.dragon.modules.system.domain.Dept;
import com.dragon.modules.system.dto.result.DeptDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DeptMapper extends BaseMapper<DeptDto, Dept> {
}
