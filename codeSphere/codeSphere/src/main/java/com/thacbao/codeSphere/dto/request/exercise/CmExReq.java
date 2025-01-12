package com.thacbao.codeSphere.dto.request.exercise;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CmExReq {
    @NotBlank(message = "content cannot be empty")
    private String content;

    @NotNull(message = "exercise cannot null")
    private String code;
}
