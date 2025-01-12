package com.thacbao.codeSphere.dto.request.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserLoginReq {

    @NotBlank(message = "Username cannot be blank")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    private String password;
}
