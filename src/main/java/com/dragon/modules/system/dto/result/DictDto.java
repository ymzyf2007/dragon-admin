package com.dragon.modules.system.dto.result;

import com.dragon.modules.common.base.BaseDTO;
import lombok.Data;

import java.util.List;

@Data
public class DictDto extends BaseDTO {

    private Long id;

    private String name;

    private String description;

    private List<DictDetailDto> dictDetails;

}
