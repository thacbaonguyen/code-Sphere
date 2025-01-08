package com.thacbao.codeSphere.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ExerciseUdReq {
    @NotBlank(message = "Code can not be empty")
    @Size(min = 2, max = 10, message = "Code must be between 2 and 10 character")
    private String code;

    @NotBlank(message = "Title can not be empty")
    private String title;

    @NotBlank(message = "Paper cannot be empty")
    private String paper;

    @NotBlank(message = "Input test case can not be empty")
    private String input;

    @NotBlank(message = "Output test case can not be empty")
    private String output;

    private String note;

    @NotBlank(message = "Description cannot be empty")
    private String description;

    @NotNull(message = "Level cannot be empty")
    private Integer level;

    @NotNull(message = "Time limit cannot be empty")
    private Integer timeLimit;

    @NotNull(message = "Memory limit cannot be empty")
    private Integer memoryLimit;
}
