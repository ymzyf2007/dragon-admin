package com.dragon.modules.system.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.dragon.modules.common.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "sys_dept")
public class Dept extends BaseEntity implements Serializable {

    @Id
    @Column(name = "dept_id")
    @NotNull(groups = Update.class)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "ID", hidden = true)
    private Long id;

    @ApiModelProperty(value = "上级部门")
    private Long pid;

    @NotBlank
    @ApiModelProperty(value = "部门名称")
    private String name;

    @ApiModelProperty(value = "排序")
    private Integer deptSort;

    @ApiModelProperty(value = "子节点数目", hidden = true)
    private Integer subCount = 0;

    @NotNull
    @ApiModelProperty(value = "是否启用")
    private Boolean enabled;

    @JSONField(serialize = false)
    @ManyToMany(mappedBy = "depts")
    @ApiModelProperty(value = "角色")
    private Set<Role> roles;

}