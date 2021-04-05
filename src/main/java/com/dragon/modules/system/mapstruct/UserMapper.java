package com.dragon.modules.system.mapstruct;

import com.dragon.modules.common.base.BaseMapper;
import com.dragon.modules.system.domain.User;
import com.dragon.modules.system.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * componentModel = "spring"：生成的实现类上面会自动添加一个@Component注解
 * unmappedTargetPolicy = ReportingPolicy.IGNORE：自动忽略未映射的属性
 */
@Mapper(componentModel = "spring", uses = {}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper extends BaseMapper<UserDto, User> {
}
