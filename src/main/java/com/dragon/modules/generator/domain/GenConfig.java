package com.dragon.modules.generator.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "code_gen_config")
public class GenConfig implements Serializable {

    public GenConfig(String tableName) {
        this.tableName = tableName;
    }

    @Id
    @Column(name = "config_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 表名
     */
    @NotBlank
    private String tableName;

    /**
     * 接口名称
     */
    private String apiAlias;

    /**
     * 包路径
     */
    @NotBlank
    private String pack;

    /**
     * 模块名
     */
    @NotBlank
    private String moduleName;

    /**
     * 文件路径
     */
    @NotBlank
    private String path;

    /**
     * 前端文件路径
     */
    private String apiPath;

    /**
     * 作者
     */
    private String author;

    /**
     * 表前缀
     */
    private String prefix;

    /**
     * 是否覆盖
     */
    private Boolean cover = false;

}
