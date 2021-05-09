package com.dragon.modules.generator.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 表的数据信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableInfo {

    /** 表名称 */
    private String tableName;

    /** 创建日期 */
    private Object createTime;

    /** 数据库引擎 */
    private String engine;

    /** 编码集 */
    private String coding;

    /** 备注 */
    private String remark;

}
