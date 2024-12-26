package com.thacbao.codeSphere.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserLoginRequest {

    @NotBlank(message = "Username cannot be blank")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    private String password;
}
