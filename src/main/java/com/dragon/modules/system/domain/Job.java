package com.dragon.modules.system.domain;

import com.dragon.modules.common.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "sys_job")
public class Job extends BaseEntity implements Serializable {

    @Id
    @Column(name = "job_id")
    @NotNull(groups = {Update.class})
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "ID", hidden = true)
    private Long id;

    @NotBlank
    @ApiModelProperty(value = "岗位名称")
    private String name;

    @NotNull
    @ApiModelProperty(value = "岗位排序")
    private Long jobSort;

    @NotNull
    @ApiModelProperty(value = "是否启用")
    private Boolean enabled;

}
