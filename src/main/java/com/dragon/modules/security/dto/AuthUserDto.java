package com.dragon.modules.security.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AuthUserDto {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private String code;

    private String uuid = "";

}
