package com.thacbao.codeSphere.dto.request.exercise;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseReq {
    @NotBlank(message = "Code can not be empty")
    @Size(min = 2, max = 10, message = "Code must be between 2 and 10 character")
    private String code;

    @NotBlank(message = "Title can not be empty")
    private String title;

    @NotBlank(message = "Paper cannot be empty")
    private String paper;

    @NotEmpty(message = "At least one test case is required")
    @Valid
    private List<TestCaseReq> testCases;

    @NotBlank(message = "Input test case can not be empty")
    private String input;

    @NotBlank(message = "Output test case can not be empty")
    private String output;

    private String note;

    @NotNull(message = "Subject cannot be empty")
    private Integer subjectId;

    @NotBlank(message = "Topic cannot be empty")
    private String topic;

    @NotBlank(message = "Description cannot be empty")
    private String description;

    @NotNull(message = "Level cannot be empty")
    private Integer level;

    @NotNull(message = "Time limit cannot be empty")
    private Integer timeLimit;

    @NotNull(message = "Memory limit cannot be empty")
    private Integer memoryLimit;
}
