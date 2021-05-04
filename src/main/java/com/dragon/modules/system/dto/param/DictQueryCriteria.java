package com.dragon.modules.system.dto.param;

import com.dragon.modules.common.annotation.Query;
import lombok.Data;

@Data
public class DictQueryCriteria {

    @Query(blurry = "name,description")
    private String blurry;

}
