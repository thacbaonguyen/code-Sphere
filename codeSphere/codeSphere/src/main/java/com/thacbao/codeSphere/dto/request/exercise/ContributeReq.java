package com.thacbao.codeSphere.dto.request.exercise;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ContributeReq {
    @NotBlank(message = "Title is required")
    private String title;
    @NotBlank(message = "Paper is required")
    private String paper;
    @NotBlank(message = "Input is required")
    private String input;
    @NotBlank(message = "Output is required")
    private String output;

    private String note;
}
