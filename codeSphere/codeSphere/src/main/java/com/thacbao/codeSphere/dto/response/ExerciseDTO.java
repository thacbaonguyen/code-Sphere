package com.thacbao.codeSphere.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExerciseDTO {
    private String code;

    private String title;

    private String paper;

    private String input;

    private String output;

    private String note;

    private String createdBy;

    private String createdAt;

    private String categories;

    public ExerciseDTO(String code, String title, String paper, String input, String output, String note, String createdBy, String createdAt, String categories) {
        this.code = code;
        this.title = title;
        this.paper = paper;
        this.input = input;
        this.output = output;
        this.note = note;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.categories = categories;
    }
}
