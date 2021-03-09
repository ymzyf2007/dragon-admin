package com.dragon.modules.system.domain;

import com.dragon.modules.common.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "sys_menu")
public class Menu extends BaseEntity implements Serializable {

    @Id
    @Column(name = "menu_id")
    @NotNull(groups = {Update.class})
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "ID", hidden = true)
    private Long id;

    @ApiModelProperty(value = "上级菜单")
    private Long pid;

    @ApiModelProperty(value = "子节点数目", hidden = true)
    private Integer subCount = 0;

    @ApiModelProperty(value = "菜单类型，目录、菜单、按钮")
    private Integer type;

    @ApiModelProperty(value = "菜单标题")
    private String title;

    @ApiModelProperty(value = "菜单图标")
    private String icon;

    @ApiModelProperty(value = "菜单排序")
    private Integer menuSort = 999;

    @ApiModelProperty(value = "权限标识")
    private String permission;

    @ApiModelProperty(value = "路由地址")
    private String path;

    @Column(name = "name")
    @ApiModelProperty(value = "菜单组件名称")
    private String componentName;

    @ApiModelProperty(value = "组件路径")
    private String component;

    @Column(columnDefinition = "bit(1) default 0")
    @ApiModelProperty(value = "缓存")
    private Boolean cache;

    @Column(columnDefinition = "bit(1) default 0")
    @ApiModelProperty(value = "是否隐藏")
    private Boolean hidden;

    @ApiModelProperty(value = "是否外链菜单")
    private Boolean iFrame;

}
