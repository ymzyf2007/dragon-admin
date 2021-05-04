package com.dragon.modules.system.dto.result;

import com.dragon.modules.common.base.BaseDTO;
import lombok.Data;

@Data
public class DictDetailDto extends BaseDTO {

    private Long id;

    private String label;

    private String value;

    private Integer dictSort;

}
