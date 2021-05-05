package com.dragon.modules.system.mapstruct;

import com.dragon.modules.common.base.BaseMapper;
import com.dragon.modules.system.domain.Job;
import com.dragon.modules.system.dto.result.JobDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {DeptMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface JobMapper extends BaseMapper<JobDto, Job> {
}
