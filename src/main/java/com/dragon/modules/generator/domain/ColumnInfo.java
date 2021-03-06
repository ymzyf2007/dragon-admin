package com.dragon.modules.generator.domain;

import com.dragon.modules.generator.utils.GenUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "code_column_config")
public class ColumnInfo implements Serializable {

    @Id
    @Column(name = "column_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 数据库字段名称
     */
    private String columnName;

    /**
     * 数据库字段类型
     */
    private String columnType;

    /**
     * 数据库字段键类型
     */
    private String keyType;

    /**
     * 字段额外的参数
     */
    private String extra;

    /**
     * 数据库字段描述
     */
    private String remark;

    /**
     * 是否必填
     */
    private Boolean notNull;

    /**
     * 是否在列表显示
     */
    private Boolean listShow;

    /**
     * 是否表单显示
     */
    private Boolean formShow;

    /**
     * 表单类型
     */
    private String formType;

    /**
     * 查询 1:模糊 2：精确
     */
    private String queryType;

    /**
     * 字典名称
     */
    private String dictName;

    /**
     * 日期注解
     */
    private String dateAnnotation;

    public ColumnInfo(String tableName, String columnName, Boolean notNull, String columnType, String remark, String keyType, String extra) {
        this.tableName = tableName;
        this.columnName = columnName;
        this.columnType = columnType;
        this.keyType = keyType;
        this.extra = extra;
        this.notNull = notNull;
        if (GenUtil.PK.equalsIgnoreCase(keyType) && GenUtil.EXTRA.equalsIgnoreCase(extra)) {
            this.notNull = false;
        }
        this.remark = remark;
        this.listShow = true;
        this.formShow = true;
    }

}
