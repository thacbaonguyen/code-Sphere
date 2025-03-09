package com.thacbao.codeSphere.dto.request.exercise;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class TestCaseReq {

    @NotBlank(message = "Input test case can not be empty")
    private String input;

    @NotBlank(message = "Expected output test case can not be empty")
    private String expectedOutput;
}
